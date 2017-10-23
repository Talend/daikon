package org.talend.daikon.logging.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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