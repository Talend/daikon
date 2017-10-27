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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.events.*;

@Configuration
public class EventMetadataFactoryConfiguration {

    @Autowired
    private EventIdGenerator eventIdGenerator;

    @Autowired
    private TimestampProvider timestampProvider;

    @Autowired
    private ServiceInfoProvider serviceInfoProvider;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AccountIdProvider accountIdProvider;

    @Bean
    public EventMetadataFactory eventMetadataBuilderFactory() {
        return new EventMetadataFactory(eventIdGenerator, serviceInfoProvider, timestampProvider, userProvider,
                accountIdProvider);
    }

}
