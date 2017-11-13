// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.web;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.NativeWebRequest;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * @author agonzalez
 */
@RunWith(MockitoJUnitRunner.class)
public class TenancyContextCallableProcessingInterceptorTest {

    @Mock
    private TenancyContext tenancyContext;

    @Mock
    private Callable<?> callable;

    @Mock
    private NativeWebRequest webRequest;

    @After
    public void clearTenancyContext() {
        TenancyContextHolder.clearContext();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNull() {
        new TenancyContextCallableProcessingInterceptor(null);
    }

    @Test
    public void currentTenancyContext() throws Exception {
        TenancyContextCallableProcessingInterceptor interceptor = new TenancyContextCallableProcessingInterceptor();
        TenancyContextHolder.setContext(tenancyContext);
        interceptor.beforeConcurrentHandling(webRequest, callable);
        TenancyContextHolder.clearContext();

        interceptor.preProcess(webRequest, callable);
        assertSame(tenancyContext, TenancyContextHolder.getContext());

        interceptor.postProcess(webRequest, callable, null);
        assertNotSame(tenancyContext, TenancyContextHolder.getContext());
    }

    @Test
    public void specificTenancyContext() throws Exception {
        TenancyContextCallableProcessingInterceptor interceptor = new TenancyContextCallableProcessingInterceptor(tenancyContext);

        interceptor.preProcess(webRequest, callable);
        assertSame(tenancyContext, TenancyContextHolder.getContext());

        interceptor.postProcess(webRequest, callable, null);
        assertNotSame(tenancyContext, TenancyContextHolder.getContext());
    }
}
