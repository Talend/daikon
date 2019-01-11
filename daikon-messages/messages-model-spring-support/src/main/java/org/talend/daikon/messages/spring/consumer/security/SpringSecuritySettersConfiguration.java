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
package org.talend.daikon.messages.spring.consumer.security;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.talend.daikon.messages.header.consumer.UserIdSetter;
import org.talend.daikon.messages.spring.consumer.DefaultConsumerSettersConfiguration;

@Configuration
@ConditionalOnClass(SecurityContextHolder.class)
@AutoConfigureBefore({ DefaultConsumerSettersConfiguration.class })
public class SpringSecuritySettersConfiguration {

    @Bean
    public UserIdSetter userIdSetter() {
        return new UserIdSetter() {

            @Override
            public void setCurrentUserId(String userId) {
                Authentication authentication = null;
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        };
    }
}
