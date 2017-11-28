package org.talend.logging.audit.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsTest {

    @Test
    public void testGetSpecificClassParamArray() {
        Throwable thr = new RuntimeException();
        Context ctx = ContextBuilder.emptyContext();
        String str = "string";
        Boolean bol = Boolean.TRUE;

        Object[] params = new Object[] { bol, thr, ctx, str };

        assertEquals(thr, Utils.getSpecificClassParam(params, Throwable.class));
        assertEquals(ctx, Utils.getSpecificClassParam(params, Context.class));
        assertEquals(str, Utils.getSpecificClassParam(params, String.class));
        assertEquals(bol, Utils.getSpecificClassParam(params, Boolean.class));
    }

    @Test
    public void testGetSpecificClassParamList() {
        Throwable thr = new IllegalArgumentException();
        Context ctx = ContextBuilder.emptyContext();
        String str = "newstring";
        Boolean bol = Boolean.FALSE;

        List<Object> params = new ArrayList<>(Arrays.asList(bol, thr, ctx, str));

        assertEquals(thr, Utils.getSpecificClassParam(params, Throwable.class));
        assertEquals(ctx, Utils.getSpecificClassParam(params, Context.class));
        assertEquals(str, Utils.getSpecificClassParam(params, String.class));
        assertEquals(bol, Utils.getSpecificClassParam(params, Boolean.class));
    }

    @Test
    public void testGetCategoryFromLoggerName() {
        String oldLoggerValue = AuditConfiguration.ROOT_LOGGER.getString();
        boolean oldAlreadySetValue = AuditConfiguration.ROOT_LOGGER.getAlreadySet();
        AuditConfiguration.ROOT_LOGGER.setAlreadySet(false);
        AuditConfiguration.ROOT_LOGGER.setValue("testlogger", String.class);

        try {
            assertEquals("testcat", Utils.getCategoryFromLoggerName("testlogger.testcat"));
        } finally {
            AuditConfiguration.ROOT_LOGGER.setAlreadySet(false);
            AuditConfiguration.ROOT_LOGGER.setValue(oldLoggerValue, String.class);
            AuditConfiguration.ROOT_LOGGER.setAlreadySet(oldAlreadySetValue);
        }
    }
}
