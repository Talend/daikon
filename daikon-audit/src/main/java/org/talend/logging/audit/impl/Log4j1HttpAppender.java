package org.talend.logging.audit.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.talend.logging.audit.AuditAppenderException;
import org.talend.logging.audit.LogAppenders;

/**
 *
 */
public class Log4j1HttpAppender extends AppenderSkeleton {

    private String url;

    private String username;

    private String password;

    private int connectTimeout;

    private int readTimeout;

    private boolean async;

    private PropagateExceptions propagateExceptions;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void setUrl(String url) {
        this.url = url;

        // validating url
        openConnection();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setPropagateExceptions(PropagateExceptions propagateExceptions) {
        this.propagateExceptions = propagateExceptions;
    }

    @Override
    protected void append(LoggingEvent event) {
        if (event == null) {
            return;
        }
        if (closed) {
            return;
        }

        try {
            if (async) {
                sendEventAsync(event);
            } else {
                sendEvent(event);
            }
        } catch (AuditAppenderException e) {
            switch (propagateExceptions) {
            case ALL:
                throw e;

            case NONE:
                errorHandler.error("Http appender error", e, -1, event);
                break;

            default:
                errorHandler.error("Http appender error with unknown exception propagation mode: " + propagateExceptions, e, -1,
                        event);
                break;
            }
        }
    }

    @Override
    public void close() {
        closed = true;

        executor.shutdown();

        try {
            executor.awaitTermination(30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdownNow();

        openConnection().disconnect();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    private void sendEventAsync(LoggingEvent event) {
        executor.execute(new LogSender(event));
    }

    private void sendEvent(LoggingEvent event) {
        HttpURLConnection conn = openConnection();

        String payloadStr = getPayload(event);
        byte[] payload = payloadStr.getBytes(StandardCharsets.UTF_8);

        conn.setFixedLengthStreamingMode(payload.length);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        } catch (IOException e) {
            throw new AuditAppenderException(LogAppenders.HTTP, e);
        }

        try {
            int resp = conn.getResponseCode();
            if (resp < 200 || resp >= 300) {
                throw new AuditAppenderException(LogAppenders.HTTP,
                        "Error response from server: code=" + resp + ", message=" + conn.getResponseMessage());
            }
        } catch (IOException e) {
            throw new AuditAppenderException(LogAppenders.HTTP, e);
        }
    }

    private HttpURLConnection openConnection() {
        try {
            URLConnection conn = new URL(url).openConnection();
            if (!(conn instanceof HttpURLConnection)) {
                throw new AuditAppenderException(LogAppenders.HTTP, "URL " + url + " is not http(s)");
            }

            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setConnectTimeout(connectTimeout);
            httpConn.setReadTimeout(readTimeout);

            if (username != null && !username.trim().isEmpty() && password != null) {
                httpConn.setRequestProperty("Authorization", getAuthorizationHeader());
            }

            return httpConn;
        } catch (IOException e) {
            throw new AuditAppenderException(LogAppenders.HTTP, e);
        }
    }

    private String getAuthorizationHeader() {
        byte[] authData = (username + ':' + password).getBytes(StandardCharsets.UTF_8);
        return "Basic " + DatatypeConverter.printBase64Binary(authData);
    }

    private String getPayload(LoggingEvent event) {
        return layout.format(event);
    }

    private class LogSender implements Runnable {

        private final LoggingEvent event;

        private LogSender(LoggingEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            sendEvent(event);
        }
    }
}
