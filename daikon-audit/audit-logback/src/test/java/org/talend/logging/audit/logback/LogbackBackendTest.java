package org.talend.logging.audit.logback;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.talend.logging.audit.LogLevel;

public class LogbackBackendTest {

    private static final String PREFIX = "prefix";

    private static final String CATEGORY = "category";

    private static final String MESSAGE = "message";

    private static final RuntimeException EXCEPTION = new RuntimeException("Test");

    @Test
    public void testLog() {
        final Logger logger = createMock(Logger.class);
        final ILoggerFactory context = createMock(ILoggerFactory.class);

        expect(context.getLogger(PREFIX + '.' + CATEGORY)).andReturn(logger).times(3);

        logger.info("{}", MESSAGE, EXCEPTION);
        logger.warn("{}", MESSAGE, EXCEPTION);
        logger.error("{}", MESSAGE, EXCEPTION);

        replay(context, logger);

        final LogbackBackend backend = new LogbackBackend(PREFIX, context);

        backend.log(CATEGORY, LogLevel.INFO, MESSAGE, EXCEPTION);
        backend.log(CATEGORY, LogLevel.WARNING, MESSAGE, EXCEPTION);
        backend.log(CATEGORY, LogLevel.ERROR, MESSAGE, EXCEPTION);

        verify(context, logger);
    }
}
