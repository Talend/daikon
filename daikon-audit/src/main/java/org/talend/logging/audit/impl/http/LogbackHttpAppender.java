package org.talend.logging.audit.impl.http;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

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

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    public void setUrl(String url) {
        sender.setUrl(url);
    }

    public void setUsername(String username) {
        sender.setUsername(username);
    }

    public void setPassword(String password) {
        sender.setPassword(password);
    }

    public void setConnectTimeout(int connectTimeout) {
        sender.setConnectTimeout(connectTimeout);
    }

    public void setReadTimeout(int readTimeout) {
        sender.setReadTimeout(readTimeout);
    }

    public void setEncoding(String encoding) {
        sender.setEncoding(encoding);
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setPropagateExceptions(boolean propagateExceptions) {
        this.propagateExceptions = propagateExceptions;
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
