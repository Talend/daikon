package org.talend.logging.audit.impl.http;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

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
