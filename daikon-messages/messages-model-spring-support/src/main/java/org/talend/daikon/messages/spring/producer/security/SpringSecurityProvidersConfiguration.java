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
package org.talend.daikon.messages.spring.producer.security;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.talend.daikon.messages.header.producer.SecurityTokenProvider;
import org.talend.daikon.messages.header.producer.UserProvider;
import org.talend.daikon.messages.spring.producer.DefaultProducerProvidersConfiguration;

@Configuration
@ConditionalOnClass({ SecurityContextHolder.class })
@AutoConfigureBefore({ DefaultProducerProvidersConfiguration.class })
public class SpringSecurityProvidersConfiguration {

    @Bean
    public UserProvider springSecurityUserProvider() {
        return new UserProvider() {

            @Override
            public String getUserId() {
                return SecurityContextHolder.getContext().getAuthentication().getName();
            }
        };
    }

    @Bean
    public SecurityTokenProvider springSecurityTokenProvider() {
        return new SecurityTokenProvider() {

            @Override
            public String getSecurityToken() {
                return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            }
        };
    }
}
