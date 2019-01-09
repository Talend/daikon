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

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.consumer.CorrelationIdSetter;

@Configuration
@ConditionalOnClass({ Tracer.class })
public class SpringSleuthSettersConfiguration {

    @Bean
    public CorrelationIdSetter correlationIdSetter(Tracer tracer) {
        return new CorrelationIdSetter() {

            @Override
            public void setCurrentCorrelationId(String correlationId) {
                long spanId = 0;
                String name = "";
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    spanId = currentSpan.context().spanId();
                    // FIXME name = currentSpan.name();
                }
                long traceId = 0;// FIXME Span.hexToId(correlationId, 0);
                TraceContext traceContext = TraceContext.newBuilder().traceId(traceId).spanId(spanId).shared(true).build();
                tracer.nextSpan(TraceContextOrSamplingFlags.create(traceContext)).name(name);
            }
        };
    }

}
