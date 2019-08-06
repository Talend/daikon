package org.talend.logging.audit.log4j1;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.talend.logging.audit.impl.http.HttpAppenderException;
import org.talend.logging.audit.impl.http.HttpEventSender;

/**
 *
 */
public class Log4j1HttpAppender extends AppenderSkeleton {

    private boolean async;

    private boolean propagateExceptions;

    private final HttpEventSender sender;

    public Log4j1HttpAppender() {
        this(new HttpEventSender());
    }

    public Log4j1HttpAppender(HttpEventSender sender) {
        this.sender = sender;
    }

    public void setSupportsSystemPropertiesOverride(final boolean supportsSystemPropertiesOverride) {
        sender.setSupportsSystemPropertiesOverride(supportsSystemPropertiesOverride);
    }

    public void setQueueSize(final int queueSize) {
        sender.setQueueSize(queueSize);
    }

    public void setCoreSize(final int coreSize) {
        sender.setCoreSize(coreSize);
    }

    public void setMaxSize(final int maxSize) {
        sender.setMaxSize(maxSize);
    }

    public void setKeepAliveMs(final int keepAliveMs) {
        sender.setKeepAliveMs(keepAliveMs);
    }

    public String getUrl() {
        return sender.getUrl();
    }

    public void setUrl(String url) {
        sender.setUrl(url);
    }

    public String getUsername() {
        return sender.getUsername();
    }

    public void setUsername(String username) {
        sender.setUsername(username);
    }

    public void setPassword(String password) {
        sender.setPassword(password);
    }

    public int getConnectTimeout() {
        return sender.getConnectTimeout();
    }

    public void setConnectTimeout(int connectTimeout) {
        sender.setConnectTimeout(connectTimeout);
    }

    public int getReadTimeout() {
        return sender.getReadTimeout();
    }

    public void setReadTimeout(int readTimeout) {
        sender.setReadTimeout(readTimeout);
    }

    public String getEncoding() {
        return sender.getEncoding() == null ? null : sender.getEncoding().toString();
    }

    public void setEncoding(String encoding) {
        sender.setEncoding(encoding);
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isPropagateExceptions() {
        return propagateExceptions;
    }

    public void setPropagateExceptions(boolean propagateExceptions) {
        this.propagateExceptions = propagateExceptions;
    }

    @Override
    public void activateOptions() {
        sender.start();
        super.activateOptions();
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
                sender.sendEventAsync(layout.format(event));
            } else {
                sender.sendEvent(layout.format(event));
            }
        } catch (HttpAppenderException e) {
            if (propagateExceptions) {
                throw e;
            }
            errorHandler.error("Http appender error", e, -1, event);
        }
    }

    @Override
    public void close() {
        closed = true;

        sender.stop();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
