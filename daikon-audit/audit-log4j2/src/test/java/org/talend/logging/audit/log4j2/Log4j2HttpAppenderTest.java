package org.talend.logging.audit.log4j2;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.junit.Test;
import org.talend.logging.audit.impl.http.HttpAppenderException;
import org.talend.logging.audit.impl.http.HttpEventSender;

public class Log4j2HttpAppenderTest {

    private static final String URL = "someurl";

    private static final String USERNAME = "someusername";

    private static final String PASSWORD = "somepassword";

    private static final int CONNECTION_TIMEOUT = 30;

    private static final int READ_TIMEOUT = 30;

    private static final String ENCODING = "UTF8";

    @Test
    public void testHttpAppender() {
        final String formattedEvent = "formattedEvent";
        final LogEvent event = createMock(LogEvent.class);

        final Layout layout = createLayout(event, formattedEvent);
        final HttpEventSender sender = createSender();

        sender.sendEvent(formattedEvent);

        replay(sender, layout);

        final Log4j2HttpAppender appender = createAppender(layout, sender, false);

        appender.setAsync(false);
        appender.append(event);

        verify(sender, layout);
    }

    @Test
    public void testHttpAppenderAsync() {
        final String formattedEvent = "formattedEvent";
        final LogEvent event = createMock(LogEvent.class);

        final Layout layout = createLayout(event, formattedEvent);
        final HttpEventSender sender = createSender();

        sender.sendEventAsync(formattedEvent);

        replay(sender, layout);

        final Log4j2HttpAppender appender = createAppender(layout, sender, false);

        appender.setAsync(true);
        appender.append(event);

        verify(sender, layout);
    }

    @Test
    public void testPropagateExceptionFalse() {
        testPropagateException(false);
    }

    @Test(expected = AppenderLoggingException.class)
    public void testPropagateExceptionTrue() {
        testPropagateException(true);
    }

    private static void testPropagateException(boolean propagate) {
        final String formattedEvent = "formattedEvent";
        final LogEvent event = createMock(LogEvent.class);

        final Layout layout = createLayout(event, formattedEvent);
        final HttpEventSender sender = createSender();

        sender.sendEvent(formattedEvent);
        expectLastCall().andThrow(new HttpAppenderException("Expected"));

        replay(sender, layout);

        final Log4j2HttpAppender appender = createAppender(layout, sender, !propagate);

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

    private static Log4j2HttpAppender createAppender(Layout layout, HttpEventSender sender, boolean ignoreExceptions) {
        final Log4j2HttpAppender appender = new Log4j2HttpAppender("httpappender", null, layout, ignoreExceptions, null, sender);

        appender.setAsync(false);
        appender.setUrl(URL);
        appender.setUsername(USERNAME);
        appender.setPassword(PASSWORD);
        appender.setConnectTimeout(CONNECTION_TIMEOUT);
        appender.setReadTimeout(READ_TIMEOUT);
        appender.setEncoding(ENCODING);

        return appender;
    }

    private static Layout createLayout(LogEvent event, String formattedEvent) {
        final Layout layout = createMock(Layout.class);
        expect(layout.toSerializable(event)).andReturn(formattedEvent);
        return layout;
    }

}
