package org.talend.logging.audit.logback;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import org.talend.logging.audit.impl.http.HttpAppenderException;
import org.talend.logging.audit.impl.http.HttpEventSender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Layout;

public class LogbackHttpAppenderTest {

    private static final String URL = "someurl";

    private static final String USERNAME = "someusername";

    private static final String PASSWORD = "somepassword";

    private static final int CONNECTION_TIMEOUT = 30;

    private static final int READ_TIMEOUT = 30;

    private static final String ENCODING = "UTF8";

    @Test
    public void testHttpAppender() {
        final String formattedEvent = "formattedEvent";
        final LoggingEvent event = new LoggingEvent();

        final Layout<ILoggingEvent> layout = createLayout(event, formattedEvent);
        final HttpEventSender sender = createSender();

        sender.sendEvent(formattedEvent);

        replay(sender, layout);

        final LogbackHttpAppender appender = createAppender(layout, sender);

        appender.setAsync(false);
        appender.append(event);

        verify(sender, layout);
    }

    @Test
    public void testHttpAppenderAsync() {
        final String formattedEvent = "formattedEvent";
        final LoggingEvent event = new LoggingEvent();

        final Layout<ILoggingEvent> layout = createLayout(event, formattedEvent);
        final HttpEventSender sender = createSender();

        sender.sendEventAsync(formattedEvent);

        replay(sender, layout);

        final LogbackHttpAppender appender = createAppender(layout, sender);

        appender.setAsync(true);
        appender.append(event);

        verify(sender, layout);
    }

    @Test
    public void testPropagateExceptionFalse() {
        testPropagateException(false);
    }

    @Test(expected = HttpAppenderException.class)
    public void testPropagateExceptionTrue() {
        testPropagateException(true);
    }

    private static void testPropagateException(boolean propagate) {
        final String formattedEvent = "formattedEvent";
        final LoggingEvent event = new LoggingEvent();

        final Layout<ILoggingEvent> layout = createLayout(event, formattedEvent);
        final HttpEventSender sender = createSender();

        sender.sendEvent(formattedEvent);
        expectLastCall().andThrow(new HttpAppenderException("Expected"));

        replay(sender, layout);

        final LogbackHttpAppender appender = createAppender(layout, sender);

        appender.setPropagateExceptions(propagate);
        appender.append(event);

        verify(sender, layout);
    }

    private static HttpEventSender createSender() {
        final HttpEventSender sender = createMock(HttpEventSender.class);

        sender.setUrl(URL);
        sender.setUsername(USERNAME);
        sender.setPassword(PASSWORD);
        sender.setConnectTimeout(CONNECTION_TIMEOUT);
        sender.setReadTimeout(READ_TIMEOUT);
        sender.setEncoding(ENCODING);

        return sender;
    }

    private static LogbackHttpAppender createAppender(Layout<ILoggingEvent> layout, HttpEventSender sender) {
        final LogbackHttpAppender appender = new LogbackHttpAppender(sender);

        appender.setAsync(false);
        appender.setLayout(layout);
        appender.setPropagateExceptions(true);

        appender.setUrl(URL);
        appender.setUsername(USERNAME);
        appender.setPassword(PASSWORD);
        appender.setConnectTimeout(CONNECTION_TIMEOUT);
        appender.setReadTimeout(READ_TIMEOUT);
        appender.setEncoding(ENCODING);

        return appender;
    }

    @SuppressWarnings({ "unchecked" })
    private static Layout<ILoggingEvent> createLayout(ILoggingEvent event, String formattedEvent) {
        final Layout<ILoggingEvent> layout = createMock(Layout.class);
        expect(layout.doLayout(event)).andReturn(formattedEvent);
        return layout;
    }

}
