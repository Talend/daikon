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
package org.talend.daikon.messages.spring.consumer.sleuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.consumer.CorrelationIdSetter;

@Configuration
@ConditionalOnClass({ Tracer.class })
public class SpringSleuthSettersConfiguration {

    @Bean
    public CorrelationIdSetter correlationIdSetter(@Autowired final Tracer tracer) {
        return new CorrelationIdSetter() {

            @Override
            public void setCurrentCorrelationId(String correlationId) {
                long spanId = 0;
                Span currentSpan = tracer.getCurrentSpan();
                if (currentSpan != null) {
                    spanId = currentSpan.getSpanId();
                }
                long traceId = Span.hexToId(correlationId, 0);
                Span span = Span.builder().traceId(traceId).spanId(spanId).shared(true).build();
                tracer.continueSpan(span);
            }
        };
    }

}
