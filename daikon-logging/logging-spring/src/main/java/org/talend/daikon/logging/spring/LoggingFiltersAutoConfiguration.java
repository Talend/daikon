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
package org.talend.daikon.logging.spring;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.talend.daikon.logging.multitenant.MultitenantLogsFilter;
import org.talend.daikon.multitenant.provider.TenantProvider;
import org.talend.daikon.multitenant.web.TenancyFiltersAutoConfiguration;

import static org.talend.daikon.multitenant.web.TenancyFiltersAutoConfiguration.TENANCY_CONTEXT_INTEGRATION_FILTER_ORDER;

@Configuration
@AutoConfigureAfter(TenancyFiltersAutoConfiguration.class)
public class LoggingFiltersAutoConfiguration {

    public static final int TENANCY_LOGS_FILTER_ORDER = TENANCY_CONTEXT_INTEGRATION_FILTER_ORDER + 1;

    public static final int USER_ID_LOGS_FILTER_ORDER = TENANCY_LOGS_FILTER_ORDER + 1;

    @Bean
    @ConditionalOnBean(TenantProvider.class)
    public FilterRegistrationBean tenancyLoggingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        MultitenantLogsFilter filter = new MultitenantLogsFilter();
        registration.setFilter(filter);
        registration.setOrder(TENANCY_LOGS_FILTER_ORDER);
        return registration;
    }

    @Bean
    @ConditionalOnClass(SecurityContextHolder.class)
    public FilterRegistrationBean userIdLoggingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        SpringSecurityUserIdLogsFilter filter = new SpringSecurityUserIdLogsFilter();
        registration.setFilter(filter);
        registration.setOrder(USER_ID_LOGS_FILTER_ORDER);
        return registration;
    }
}