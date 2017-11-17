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

import org.slf4j.MDC;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.talend.daikon.logging.event.field.MdcKeys;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.daikon.multitenant.provider.TenantProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Responsible for setting and removing the {@link TenancyContext tenancy context} for the scope of every request. This
 * filter should be installed before any components that need access to the {@link TenancyContext tenancy context}.
 *
 * @author Clint Morgan (Tasktop Technologies Inc.)
 *
 * @see TenancyContext
 * @see TenancyContextHolder
 */
public class TenancyContextIntegrationFilter extends OncePerRequestFilter {

    private static final Object CALLABLE_INTERCEPTOR_KEY = new Object();

    private final List<TenantIdentificationStrategy> tenantIdentificationStrategyChain;

    private final TenantProvider tenantProvider;

    public TenancyContextIntegrationFilter(List<TenantIdentificationStrategy> tenantIdentificationStrategyChain,
            TenantProvider tenantProvider) {
        this.tenantIdentificationStrategyChain = tenantIdentificationStrategyChain;
        this.tenantProvider = tenantProvider;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        TenancyContextIntegrationFilter tenancyProcessingInterceptor = (TenancyContextIntegrationFilter) asyncManager
                .getCallableInterceptor(CALLABLE_INTERCEPTOR_KEY);
        if (tenancyProcessingInterceptor == null) {
            asyncManager.registerCallableInterceptor(CALLABLE_INTERCEPTOR_KEY, new TenancyContextCallableProcessingInterceptor());
        }

        TenancyContext contextBeforeChainExecution = determineTenancyContext(request);

        try {
            TenancyContextHolder.setContext(contextBeforeChainExecution);
            setMdc(contextBeforeChainExecution);

            chain.doFilter(request, response);

        } finally {
            // Crucial removal of ContextHolder contents - do this
            // before anything else.
            TenancyContextHolder.clearContext();
            removeMdc();
        }

    }

    private TenancyContext determineTenancyContext(HttpServletRequest request) {
        TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
        tenancyContext.setTenant(determineTenant(request));
        return tenancyContext;
    }

    private static void setMdc(TenancyContext tenancyContext) {
        if (tenancyContext != null && tenancyContext.getTenant() != null) {
            MDC.put(MdcKeys.ACCOUNT_ID, String.valueOf(tenancyContext.getTenant().getIdentity()));
        }
    }

    private static void removeMdc() {
        MDC.remove(MdcKeys.ACCOUNT_ID);
    }

    /**
     * Determine the tenant based on a given request. Default implementation goes through the identification strategies
     * in order. The first identification strategy to find a tenant identification in the request will be used, via the
     * tenantProvider, to locate the tenant.
     * 
     * @param request
     * @return the tenant context for the given httpRequest. <code>null</code> is a valid return value.
     */
    protected Tenant determineTenant(HttpServletRequest request) {
        for (TenantIdentificationStrategy tenantIdentificationStrategy : tenantIdentificationStrategyChain) {
            Object tenantId = tenantIdentificationStrategy.identifyTenant(request);
            if (tenantId != null) {
                return tenantProvider.findTenant(tenantId);
            }
        }
        return null;
    }

    private static class TenancyContextCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

        private TenancyContext tenancyContext;

        @Override
        public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
            if (tenancyContext == null) {
                setTenancyContext(TenancyContextHolder.getContext());
            }
        }

        @Override
        public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
            TenancyContextHolder.setContext(tenancyContext);
            setMdc(tenancyContext);
        }

        @Override
        public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
            TenancyContextHolder.clearContext();
            removeMdc();
        }

        private void setTenancyContext(TenancyContext tenancyContext) {
            this.tenancyContext = tenancyContext;
        }
    }
}