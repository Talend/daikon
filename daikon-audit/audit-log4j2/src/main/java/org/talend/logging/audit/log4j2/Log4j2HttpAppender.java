package org.talend.logging.audit.log4j2;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.talend.logging.audit.impl.http.HttpAppenderException;
import org.talend.logging.audit.impl.http.HttpEventSender;

/**
 *
 */
public class Log4j2HttpAppender extends AbstractAppender {

    private boolean async;

    private final HttpEventSender sender;

    protected Log4j2HttpAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions, final Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.sender = new HttpEventSender();
    }

    protected Log4j2HttpAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions, final Property[] properties, HttpEventSender sender) {
        super(name, filter, layout, ignoreExceptions, properties);
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

    @Override
    public void start() {
        super.start();
        sender.start();
    }

    @Override
    public void append(LogEvent event) {
        if (event == null) {
            return;
        }
        if (this.isStopped()) {
            return;
        }

        try {
            if (async) {
                sender.sendEventAsync(formatByLayout(event));
            } else {
                sender.sendEvent(formatByLayout(event));
            }
        } catch (HttpAppenderException e) {
            this.error("Http appender error", event, e);
        }
    }

    private String formatByLayout(LogEvent event) {
        String content = (String) this.getLayout().toSerializable(event);
        return content;
    }

    @Override
    public void stop() {
        sender.stop();
        super.stop();
    }

}
