package org.talend.logging.audit.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;

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
}
