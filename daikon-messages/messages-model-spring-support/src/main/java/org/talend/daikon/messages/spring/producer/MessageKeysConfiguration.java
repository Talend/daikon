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
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.producer.TenantIdProvider;
import org.talend.daikon.messages.keys.MessageKeyFactory;
import org.talend.daikon.messages.keys.MessageKeyFactoryImpl;

@Configuration
@AutoConfigureBefore({ DefaultProducerProvidersConfiguration.class })
public class MessageKeysConfiguration {

    @Bean
    public MessageKeyFactory messageKeyFactory(@Autowired TenantIdProvider tenantIdProvider) {
        return new MessageKeyFactoryImpl(tenantIdProvider);
    }

}
