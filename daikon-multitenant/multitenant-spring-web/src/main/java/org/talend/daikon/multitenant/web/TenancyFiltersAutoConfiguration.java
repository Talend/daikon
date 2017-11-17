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
package org.talend.daikon.multitenant.web;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.multitenant.provider.TenantProvider;

import java.util.List;

@Configuration
public class TenancyFiltersAutoConfiguration {

    private static final int SPRING_SECURITY_FILTERS_ORDER = 5;

    public static final int TENANCY_CONTEXT_INTEGRATION_FILTER_ORDER = SPRING_SECURITY_FILTERS_ORDER + 2;

    @Bean
    @Conditional(TenancyFiltersAutoConfiguration.TenantCondition.class)
    public FilterRegistrationBean tenancyContextIntegrationFilter(TenantProvider tenantProvider,
            List<TenantIdentificationStrategy> strategyList) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        TenancyContextIntegrationFilter filter = new TenancyContextIntegrationFilter(strategyList, tenantProvider);
        registration.setFilter(filter);
        // just after all Security Filters
        registration.setOrder(TENANCY_CONTEXT_INTEGRATION_FILTER_ORDER);
        return registration;
    }

    public static class TenantCondition extends AllNestedConditions {

        TenantCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(TenantProvider.class)
        static class TenantProviderCondition {
        }

        @ConditionalOnBean(TenantIdentificationStrategy.class)
        static class TenantIdentificationStrategyCondition {
        }
    }

}
