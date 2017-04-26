package org.talend.daikon.logging.correlate;

import org.junit.Assert;
import org.junit.Test;

public class RequestCorrelationContextTest {

    @Test
    public void test() throws Exception {
        RequestCorrelationContext context = RequestCorrelationContext.getCurrent();
        Assert.assertTrue(context != null);
        Assert.assertTrue(context.getCorrelationId() == null);
        Assert.assertTrue(context.equals(RequestCorrelationContext.getCurrent()));

        context.setCorrelationId("foo");
        Assert.assertTrue("foo".equals(context.getCorrelationId()));

        RequestCorrelationContext.clearCurrent();
        Assert.assertTrue(!context.equals(RequestCorrelationContext.getCurrent()));
    }

}