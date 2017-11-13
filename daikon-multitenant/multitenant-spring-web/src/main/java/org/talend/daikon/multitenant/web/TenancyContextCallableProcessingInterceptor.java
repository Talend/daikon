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

import java.util.concurrent.Callable;

import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;

public class TenancyContextCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

    private TenancyContext tenancyContext;

    /**
     * Create a new {@link TenancyContextCallableProcessingInterceptor} that uses the
     * {@link TenancyContext} from the {@link TenancyContextHolder} at the time
     * {@link #beforeConcurrentHandling(NativeWebRequest, Callable)} is invoked.
     */
    public TenancyContextCallableProcessingInterceptor() {
    }

    /**
     * Creates a new {@link TenancyContextCallableProcessingInterceptor} with the
     * specified {@link TenancyContext}.
     * 
     * @param tenancyContext the {@link TenancyContext} to set on the
     * {@link org.talend.daikon.multitenant.context.TenancyContextHolder} in {@link #preProcess(NativeWebRequest, Callable)}.
     * Cannot be null.
     * @throws IllegalArgumentException if tenancyContext is null.
     */
    public TenancyContextCallableProcessingInterceptor(TenancyContext tenancyContext) {
        Assert.notNull(tenancyContext, "tenancyContext cannot be null");
        setTenancyContext(tenancyContext);
    }

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        if (tenancyContext == null) {
            setTenancyContext(TenancyContextHolder.getContext());
        }
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        TenancyContextHolder.setContext(tenancyContext);
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
        TenancyContextHolder.clearContext();
    }

    private void setTenancyContext(TenancyContext tenancyContext) {
        this.tenancyContext = tenancyContext;
    }
}
