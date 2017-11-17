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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring boot auto-configuration class to automatically set the appropriate {@link org.apache.log4j.MDC}
 * for current user id.
 *
 * It works with both synchronous and asynchronous endpoints.
 */
@Configuration
@ConditionalOnClass(SecurityContextHolder.class)
public class UserIdLoggingAutoConfiguration {

    public int USER_ID_LOGS_FILTER_ORDER = SecurityProperties.DEFAULT_FILTER_ORDER + 1;

    @Bean
    public FilterRegistrationBean userIdLoggingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        UserIdLoggingFilter filter = new UserIdLoggingFilter();
        registration.setFilter(filter);
        registration.setOrder(USER_ID_LOGS_FILTER_ORDER);
        return registration;
    }
}