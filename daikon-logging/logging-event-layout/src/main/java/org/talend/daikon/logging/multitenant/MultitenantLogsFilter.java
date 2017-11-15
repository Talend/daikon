// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.logging.multitenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.daikon.logging.event.field.MdcKeys;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;

import javax.servlet.*;
import java.io.IOException;
import java.util.Optional;

/**
 * An HTTP filter that will query the {@link TenancyContextHolder} to retrieve the current
 * tenant and set a custom {@link MDC} key with its identifier.
 */
public class MultitenantLogsFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultitenantLogsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("Initializing MultitenantLogsFilter");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        getCurrentTenantId().ifPresent(tenantId -> MDC.put(MdcKeys.ACCOUNT_ID, tenantId));
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(MdcKeys.ACCOUNT_ID);
        }
    }

    private Optional<String> getCurrentTenantId() {
        TenancyContext context = TenancyContextHolder.getContext();
        if (context != null) {
            Tenant tenant = context.getTenant();
            if (tenant != null) {
                String tenantId = String.valueOf(tenant.getIdentity());
                LOGGER.debug("Found tenant {} in current context", tenantId);
                return Optional.of(tenantId);
            }
        }
        LOGGER.debug("Not tenant found in current context");
        return Optional.empty();
    }
}
