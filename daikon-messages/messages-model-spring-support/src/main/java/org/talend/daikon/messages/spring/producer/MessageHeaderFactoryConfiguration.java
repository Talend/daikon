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
package org.talend.daikon.messages.spring.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.producer.*;

@Configuration
public class MessageHeaderFactoryConfiguration {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TimestampProvider timestampProvider;

    @Autowired
    private ServiceInfoProvider serviceInfoProvider;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private TenantIdProvider tenantIdProvider;

    @Autowired
    private CorrelationIdProvider correlationIdProvider;

    @Autowired
    private SecurityTokenProvider securityTokenProvider;

    @Bean
    public MessageHeaderFactory messageHeaderFactory() {
        return new MessageHeaderFactoryImpl(idGenerator, serviceInfoProvider, timestampProvider, userProvider, tenantIdProvider,
                correlationIdProvider, securityTokenProvider);
    }

}
