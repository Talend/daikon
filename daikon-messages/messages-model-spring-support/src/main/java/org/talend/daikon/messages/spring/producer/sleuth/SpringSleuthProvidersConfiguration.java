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
package org.talend.daikon.messages.spring.producer.sleuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.producer.CorrelationIdProvider;

@Configuration
@ConditionalOnClass({ Tracer.class })
public class SpringSleuthProvidersConfiguration {

    @Value("${org.talend.daikon.messages.spring.producer.sleuth.defaultSpanName:MESSAGING_DEFAULT_SPAN")
    private String defaultSpanName;

    @Bean
    public CorrelationIdProvider sleuthCorrelationIdProvider(@Autowired final Tracer tracer) {
        return new CorrelationIdProvider() {

            @Override
            public String getCorrelationId() {
                Span currentSpan = tracer.getCurrentSpan();
                if (currentSpan == null) {
                    currentSpan = tracer.createSpan(defaultSpanName);
                }
                return Span.idToHex(currentSpan.getSpanId());
            }
        };
    }

}
