package org.talend.logging.audit.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import org.talend.logging.audit.impl.http.HttpAppenderException;
import org.talend.logging.audit.impl.http.HttpEventSender;

/**
 *
 */
public class LogbackHttpAppender extends AppenderBase<ILoggingEvent> {

    private Layout<ILoggingEvent> layout;

    private boolean async;

    private boolean propagateExceptions;

    private final HttpEventSender sender;

    public LogbackHttpAppender() {
        this(new HttpEventSender());
    }

    public LogbackHttpAppender(HttpEventSender sender) {
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

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
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
    public void start() {
        sender.start();
        super.start();
    }

    @Override
    public synchronized void doAppend(ILoggingEvent eventObject) {
        if (!this.started) {
            return;
        }
        append(eventObject);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            if (async) {
                sender.sendEventAsync(layout.doLayout(eventObject));
            } else {
                sender.sendEvent(layout.doLayout(eventObject));
            }
        } catch (HttpAppenderException e) {
            if (propagateExceptions) {
                throw e;
            }
            addError("Http appender error", e);
        }
    }

    @Override
    public void stop() {
        super.stop();

        sender.stop();
    }
}
