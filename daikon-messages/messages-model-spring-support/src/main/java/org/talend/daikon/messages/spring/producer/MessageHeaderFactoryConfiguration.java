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

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.AvroRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.envelope.MessageConverter;
import org.talend.daikon.messages.envelope.MessageConverterRegistry;
import org.talend.daikon.messages.envelope.MessageConverterRegistryImpl;
import org.talend.daikon.messages.envelope.MessageEnvelopeHandler;
import org.talend.daikon.messages.envelope.MessageEnvelopeHandlerImpl;
import org.talend.daikon.messages.header.producer.CorrelationIdProvider;
import org.talend.daikon.messages.header.producer.IdGenerator;
import org.talend.daikon.messages.header.producer.MessageHeaderFactory;
import org.talend.daikon.messages.header.producer.MessageHeaderFactoryImpl;
import org.talend.daikon.messages.header.producer.SecurityTokenProvider;
import org.talend.daikon.messages.header.producer.ServiceInfoProvider;
import org.talend.daikon.messages.header.producer.TenantIdProvider;
import org.talend.daikon.messages.header.producer.TimestampProvider;
import org.talend.daikon.messages.header.producer.UserProvider;

@Configuration
@AutoConfigureBefore({ DefaultProducerProvidersConfiguration.class })
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

    @Bean
    public MessageConverterRegistry messageConverterRegistry() {
        MessageConverterRegistryImpl messageConverterRegistry = new MessageConverterRegistryImpl();
        messageConverterRegistry.registerConverter("json", new MessageConverter() {

            private final ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public <T> T deserialize(String content, Class<T> clazz) {
                try {
                    return objectMapper.readValue(content, clazz);
                } catch (IOException e) {
                    throw new AvroRuntimeException("", e);
                }
            }

            @Override
            public <T> String serialize(T content) {
                try {
                    return objectMapper.writeValueAsString(content);
                } catch (IOException e) {
                    throw new AvroRuntimeException("", e);
                }
            }
        });
        return messageConverterRegistry;
    }

    @Bean
    public MessageEnvelopeHandler messageEnvelopeHandler(MessageConverterRegistry messageConverterRegistry,
            MessageHeaderFactory messageHeaderFactory) {
        return new MessageEnvelopeHandlerImpl(messageConverterRegistry, messageHeaderFactory);
    }

}
