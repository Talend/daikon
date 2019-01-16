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
package org.talend.daikon.messages.spring.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.consumer.CorrelationIdSetter;
import org.talend.daikon.messages.header.consumer.SecurityTokenSetter;
import org.talend.daikon.messages.header.consumer.TenantIdSetter;
import org.talend.daikon.messages.header.consumer.UserIdSetter;

@Configuration
public class DefaultConsumerSettersConfiguration {

    @Bean
    @ConditionalOnMissingBean(CorrelationIdSetter.class)
    public CorrelationIdSetter noopCorrelationIdSetter() {
        return correlationId -> {
            // mocked bean: do nothing
        };
    }

    @Bean
    @ConditionalOnMissingBean(TenantIdSetter.class)
    public TenantIdSetter noopTenantIdSetter() {
        return tenantId -> {
            // mocked bean: do nothing
        };
    }

    @Bean
    @ConditionalOnMissingBean(UserIdSetter.class)
    public UserIdSetter noopUserIdSetter() {
        return userId -> {
            // mocked bean: do nothing
        };
    }

    @Bean
    @ConditionalOnMissingBean(SecurityTokenSetter.class)
    public SecurityTokenSetter noopSecurityTokenSetter() {
        return securityToken -> {
            // mocked bean: do nothing
        };
    }

}
