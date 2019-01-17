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
import org.springframework.util.Assert;
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
        return traceId -> {
            final Span currentSpan = ofNullable(tracer.currentSpan()).orElse(tracer.newTrace());
            final TraceContext context = currentSpan.context().toBuilder().traceId(hexToId(traceId)).build();
            final TraceContextOrSamplingFlags content = TraceContextOrSamplingFlags.create(context);
            tracer.nextSpan(content);
        };
    }

    // Copied from previous Sleuth version
    private long hexToId(String hexString) {
        Assert.hasText(hexString, "Can't convert empty hex string to long");
        int length = hexString.length();
        if (length >= 1 && length <= 32) {
            int beginIndex = length > 16 ? length - 16 : 0;
            return hexToId(hexString, beginIndex);
        } else {
            throw new IllegalArgumentException("Malformed id: " + hexString);
        }
    }

    // Copied from previous Sleuth version
    private long hexToId(String lowerHex, int beginIndex) {
        long result = 0L;
        int index = beginIndex;
        for (int endIndex = Math.min(index + 16, lowerHex.length()); index < endIndex; ++index) {
            char c = lowerHex.charAt(index);
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= (long) (c - 48);
            } else {
                if (c < 'a' || c > 'f') {
                    throw new IllegalArgumentException("Malformed id: " + lowerHex);
                }

                result |= (long) (c - 97 + 10);
            }
        }

        return result;
    }

}
