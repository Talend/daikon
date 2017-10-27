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
package org.talend.daikon.messages.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.events.*;
import org.talend.daikon.events.impl.LocalEventIdGenerator;
import org.talend.daikon.events.impl.LocalTimestampProvider;

@Configuration
public class DefaultProvidersConfiguration {

    @Value("spring.application.name")
    private String appName;

    @Value("application.version")
    private String appVersion;

    @Value("daikon.events.defaultAccountId:")
    private String defaultAccountId;

    @Value("daikon.events.defaultUserId:")
    private String defaultUserId;

    @Bean
    @ConditionalOnMissingBean(ServiceInfoProvider.class)
    public ServiceInfoProvider defaultServiceInfoProvider() {
        return new ServiceInfoProvider() {

            @Override
            public String getServiceName() {
                return appName;
            }

            @Override
            public String getServiceVersion() {
                return appVersion;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(EventIdGenerator.class)
    public EventIdGenerator defaultEventIdGenerator() {
        return new LocalEventIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(TimestampProvider.class)
    public TimestampProvider defaultTimestampProvider() {
        return new LocalTimestampProvider();
    }

    @Bean
    @ConditionalOnMissingBean(UserProvider.class)
    public UserProvider defaultUserProvider() {
        return new UserProvider() {

            @Override
            public String getUserId() {
                return defaultUserId;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(AccountIdProvider.class)
    public AccountIdProvider defaultAccountIdProvider() {
        return new AccountIdProvider() {

            @Override
            public String getAccountId() {
                return defaultAccountId;
            }
        };
    }
}
