package org.talend.logging.audit.impl.http;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class HttpEventSender {

    private final AtomicReference<ExecutorService> executor = new AtomicReference<>();

    /**
     * Target server URL where event are pushed to.
     */
    private String url;

    /**
     * Optional username (requires a password) for basic authentication.
     */
    private String username;

    /**
     * Optional password (requires an username) for basic authentication.
     */
    private String password;

    /**
     * Optional (but recommended) HTTP connection timeout.
     */
    private int connectTimeout;

    /**
     * Optional (but recommended) HTTP read timeout.
     */
    private int readTimeout;

    /**
     * Encoding used to create the basic token and content-type header, <b>it is not used to encode the event</b>.
     */
    private Charset encoding;

    /**
     * Number of core threads in async mode (recommended to align it with max value).
     */
    private int coreSize = 1;

    /**
     * Number of max threads in async mode (recommended to align it with max value).
     */
    private int maxSize = 1;

    /**
     * Thread pool queue size. If negative it will be infinite, if zero it will be blocking when no thread is available,
     * otherwise it is the number of allowed stacked events.
     */
    private int queueSize = -1;

    /**
     * How long to keep idle threads up in ms.
     */
    private int keepAliveMs = 60000;

    /**
     * Should current configuration be overridable with system properties. If <code>true</code>, you can use
     * <code>org.talend.logging.audit.impl.http.HttpEventSender.&lt;property name&gt;</code> to override a value.
     */
    private boolean supportsSystemPropertiesOverride = true;

    private String authorization;

    private URL connectionFactory;

    public void setSupportsSystemPropertiesOverride(final boolean supportsSystemPropertiesOverride) {
        this.supportsSystemPropertiesOverride = supportsSystemPropertiesOverride;
    }

    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }

    public void setCoreSize(final int coreSize) {
        this.coreSize = coreSize;
    }

    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    public void setKeepAliveMs(final int keepAliveMs) {
        this.keepAliveMs = keepAliveMs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = Charset.forName(encoding);
    }

    public void sendEventAsync(String jsonEvent) {
        ExecutorService executorService = executor.get();
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(Math.min(coreSize, maxSize), maxSize, keepAliveMs, MILLISECONDS,
                    queueSize < 0 ? new LinkedBlockingQueue<>()
                            : (queueSize == 0 ? new SynchronousQueue<>() : new ArrayBlockingQueue<>(queueSize)),
                    new ThreadFactory() {

                        private final AtomicInteger counter = new AtomicInteger();

                        @Override
                        public Thread newThread(final Runnable worker) {
                            final Thread thread = new Thread(worker,
                                    HttpEventSender.class.getName() + "-thread-" + counter.incrementAndGet());
                            thread.setPriority(Thread.NORM_PRIORITY);
                            thread.setDaemon(false);
                            return thread;
                        }
                    });
            if (!executor.compareAndSet(null, executorService)) {
                executorService.shutdownNow();
                executorService = executor.get();
            }
        }
        executorService.execute(new LogSender(jsonEvent));
    }

    public void sendEvent(String jsonEvent) {
        HttpURLConnection conn = openConnection();

        byte[] payload = jsonEvent.getBytes();

        conn.setFixedLengthStreamingMode(payload.length);
        conn.setRequestProperty("Content-Type", "application/json; charset=" + encoding.name());

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        } catch (IOException e) {
            throw new HttpAppenderException(e);
        }

        try {
            int resp = conn.getResponseCode();
            if (resp < 200 || resp >= 300) {
                throw new HttpAppenderException(
                        "Error response from server: code=" + resp + ", message=" + conn.getResponseMessage());
            }
        } catch (IOException e) {
            throw new HttpAppenderException(e);
        }
    }

    protected HttpURLConnection openConnection() {
        try {
            URLConnection conn = connectionFactory.openConnection();
            if (!(conn instanceof HttpURLConnection)) {
                throw new HttpAppenderException("URL " + url + " is not http(s)");
            }

            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setConnectTimeout(connectTimeout);
            httpConn.setReadTimeout(readTimeout);

            if (authorization != null) {
                httpConn.setRequestProperty("Authorization", getAuthorizationHeader());
            }

            return httpConn;
        } catch (IOException e) {
            throw new HttpAppenderException(e);
        }
    }

    public void start() {
        if (supportsSystemPropertiesOverride) {
            overrideConfigurationWithSystemProperties();
        }

        authorization = username != null && !username.trim().isEmpty() && password != null ? getAuthorizationHeader() : null;
        try {
            connectionFactory = new URL(url);
        } catch (final MalformedURLException e) {
            throw new HttpAppenderException(e);
        }
        openConnection();
    }

    public void stop() {
        final ExecutorService executorService = executor.get();
        if (executor.compareAndSet(executorService, null)) {
            executorService.shutdown();

            try {
                executorService.awaitTermination(30L, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            executorService.shutdownNow();

            openConnection().disconnect();
        }
    }

    private void overrideConfigurationWithSystemProperties() {
        Stream.of(HttpEventSender.class.getDeclaredFields())
                .forEach(field -> ofNullable(System.getProperty(HttpEventSender.class.getName() + "." + field.getName()))
                        .ifPresent(value -> {
                            field.setAccessible(true);
                            try {
                                if (field.getType() == int.class) {
                                    field.set(HttpEventSender.this, Integer.parseInt(value));
                                } else if (field.getType() == String.class) {
                                    field.set(HttpEventSender.this, value);
                                } else if (field.getType() == Charset.class) {
                                    field.set(HttpEventSender.this, Charset.forName(value));
                                } else {
                                    throw new IllegalArgumentException(field + " can't be set through a system property");
                                }
                            } catch (final IllegalAccessException iae) {
                                throw new HttpAppenderException(iae);
                            }
                        }));
    }

    private String getAuthorizationHeader() {
        byte[] authData = (username + ':' + password).getBytes(encoding);
        return "Basic " + Base64.getEncoder().encodeToString(authData);
    }

    private class LogSender implements Runnable {

        private final String eventJson;

        private LogSender(String eventJson) {
            this.eventJson = eventJson;
        }

        @Override
        public void run() {
            sendEvent(eventJson);
        }
    }
}
