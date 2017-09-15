package org.talend.daikon.logging.user;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.daikon.logging.user.RequestUserActivityContext;

public class RequestUserActivityContextTest {

    @Test
    public void test() throws Exception {
        RequestUserActivityContext context = RequestUserActivityContext.getCurrent();
        assertNotNull(context);
        assertTrue(context.getCorrelationId() == null);
        assertEquals(context, RequestUserActivityContext.getCurrent());

        context.setCorrelationId("foo");
        assertTrue("foo".equals(context.getCorrelationId()));

        RequestUserActivityContext.clearCurrent();
        assertFalse(context.equals(RequestUserActivityContext.getCurrent()));
    }

}