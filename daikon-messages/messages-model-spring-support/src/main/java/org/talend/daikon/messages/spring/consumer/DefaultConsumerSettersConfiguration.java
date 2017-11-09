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
    public CorrelationIdSetter correlationIdSetter() {
        return new CorrelationIdSetter() {

            @Override
            public void setCurrentCorrelationId(String correlationId) {

            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(TenantIdSetter.class)
    public TenantIdSetter tenantIdSetter() {
        return new TenantIdSetter() {

            @Override
            public void setCurrentTenantId(String tenantId) {

            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(UserIdSetter.class)
    public UserIdSetter userIdSetter() {
        return new UserIdSetter() {

            @Override
            public void setCurrentUserId(String userId) {

            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(SecurityTokenSetter.class)
    public SecurityTokenSetter securityTokenSetter() {
        return new SecurityTokenSetter() {

            @Override
            public void setCurrentSecurityToken(String securityToken) {

            }
        };
    }

}
