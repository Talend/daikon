package org.talend.logging.audit.impl.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

/**
 *
 */
public class HttpEventSender {

    private String url;

    private String username;

    private String password;

    private int connectTimeout;

    private int readTimeout;

    private Charset encoding;

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

    public void setEncoding(String encoding) {
        this.encoding = Charset.forName(encoding);
    }

    void sendEventAsync(String jsonEvent) {
        executor.execute(new LogSender(jsonEvent));
    }

    void sendEvent(String jsonEvent) {
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

    HttpURLConnection openConnection() {
        try {
            URLConnection conn = new URL(url).openConnection();
            if (!(conn instanceof HttpURLConnection)) {
                throw new HttpAppenderException("URL " + url + " is not http(s)");
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
            throw new HttpAppenderException(e);
        }
    }

    void stop() {
        executor.shutdown();

        try {
            executor.awaitTermination(30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdownNow();

        openConnection().disconnect();
    }

    private String getAuthorizationHeader() {
        byte[] authData = (username + ':' + password).getBytes(encoding);
        return "Basic " + DatatypeConverter.printBase64Binary(authData);
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
