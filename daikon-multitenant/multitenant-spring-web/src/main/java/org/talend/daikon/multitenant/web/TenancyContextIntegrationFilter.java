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

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.daikon.multitenant.provider.DefaultTenantProvider;
import org.talend.daikon.multitenant.provider.TenantProvider;

/**
 * Responsible for setting and removing the {@link TenancyContext tenancy context} for the scope of every request. This
 * filter should be installed before any components that need access to the {@link TenancyContext tenancy context}.
 *
 * @author Clint Morgan (Tasktop Technologies Inc.)
 *
 * @see TenancyContext
 * @see TenancyContextHolder
 */
// TODO fail fast if no idStratagies
public class TenancyContextIntegrationFilter extends OncePerRequestFilter {

    private List<TenantIdentificationStrategy> tenantIdentificationStrategyChain;

    private TenantProvider tenantProvider = new DefaultTenantProvider();

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        TenancyContext contextBeforeChainExecution = determineTenancyContext(request);

        try {
            TenancyContextHolder.setContext(contextBeforeChainExecution);

            chain.doFilter(req, res);

        } finally {
            // Crucial removal of ContextHolder contents - do this
            // before anything else.
            TenancyContextHolder.clearContext();
        }

    }

    private TenancyContext determineTenancyContext(HttpServletRequest request) {
        TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
        tenancyContext.setTenant(determineTenant(request));
        return tenancyContext;
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

    /**
     * Set the tenant identification strategy chain. The first member of the chain to identify a tenant will be used.
     * 
     * @param tenantIdentificationStrategyChain
     */
    public void setTenantIdentificationStrategyChain(List<TenantIdentificationStrategy> tenantIdentificationStrategyChain) {
        this.tenantIdentificationStrategyChain = tenantIdentificationStrategyChain;
    }

    public void setTenantProvider(TenantProvider tenantProvider) {
        this.tenantProvider = tenantProvider;
    }
}