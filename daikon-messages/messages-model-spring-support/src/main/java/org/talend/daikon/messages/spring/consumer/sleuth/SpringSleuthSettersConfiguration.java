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

import static java.util.Optional.ofNullable;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.messages.header.consumer.CorrelationIdSetter;
import org.talend.daikon.messages.spring.consumer.DefaultConsumerSettersConfiguration;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

@Configuration
@ConditionalOnClass({ Tracer.class })
@AutoConfigureBefore({ DefaultConsumerSettersConfiguration.class })
public class SpringSleuthSettersConfiguration {

    @Bean
    public CorrelationIdSetter correlationIdSetter(Tracer tracer) {
        return new CorrelationIdSetter() {

            @Override
            public void setCurrentCorrelationId(long traceId) {
                final Span currentSpan = ofNullable(tracer.currentSpan()).orElse(tracer.newTrace());
                final TraceContext context = currentSpan.context().toBuilder().shared(true).build();
                final TraceContextOrSamplingFlags content = TraceContextOrSamplingFlags.create(context);
                tracer.nextSpan(content);
            }
        };
    }

}
