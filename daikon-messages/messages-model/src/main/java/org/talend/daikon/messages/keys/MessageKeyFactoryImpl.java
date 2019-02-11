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
package org.talend.daikon.messages.keys;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.talend.daikon.messages.MessageKey;
import org.talend.daikon.messages.header.producer.TenantIdProvider;

/**
 * Default {@link MessageKeyFactory} implementation.
 *
 * For random messages partitioning, a {@link RandomGenerator} can be provided to configure how
 * randomness is generated. If no random generator is provided, the default one uses the Java UUID generator.
 *
 * For tenant information, a {@link TenantIdProvider} must be provided to access the current execution context's
 * tenant identifier.
 */
public class MessageKeyFactoryImpl implements MessageKeyFactory {

    private final TenantIdProvider tenantIdProvider;

    private final RandomGenerator randomGenerator;

    /**
     * Creates a new message key factory that will use the provided {@link TenantIdProvider} and the default
     * {@link RandomGenerator} to create message keys
     *
     * @param tenantIdProvider how to access current tenant id
     */
    public MessageKeyFactoryImpl(TenantIdProvider tenantIdProvider) {
        this(tenantIdProvider, new UUIDRandomGenerator());
    }

    /**
     * Creates a new message key factory that will use the provided {@link TenantIdProvider} and {@link RandomGenerator}
     * to create message keys
     * 
     * @param tenantIdProvider how to access current tenant id
     * @param randomGenerator how to generate random values
     */
    public MessageKeyFactoryImpl(TenantIdProvider tenantIdProvider, RandomGenerator randomGenerator) {
        this.tenantIdProvider = tenantIdProvider;
        this.randomGenerator = randomGenerator;
    }

    @Override
    public MessageKey createMessageKey() {
        return buildMessageKey().build();
    }

    @Override
    public MessageKeyBuilder buildMessageKey() {
        return new MessageKeyBuilderImpl();
    }

    /**
     * Defines how a random value can be generated for a message key
     */
    public interface RandomGenerator {

        /**
         * @return a random String value
         */
        String generateRandom();
    }

    private class MessageKeyBuilderImpl implements MessageKeyBuilder {

        private final MessageKey.Builder builder;

        private final Map<String, String> keys = new HashMap<>();

        MessageKeyBuilderImpl() {
            builder = MessageKey.newBuilder().setTenantId(MessageKeyFactoryImpl.this.tenantIdProvider.getTenantId());
        }

        public MessageKeyBuilder withKey(String key, String value) {
            keys.put(key, value);
            return this;
        }

        public MessageKey build() {
            if (keys.isEmpty()) {
                builder.setRandom(MessageKeyFactoryImpl.this.randomGenerator.generateRandom());
            } else {
                builder.setKeys(new HashMap<>(keys));
            }

            return builder.setKeys(new HashMap<>(keys)).build();
        }

    }

    private static class UUIDRandomGenerator implements RandomGenerator {

        @Override
        public String generateRandom() {
            return UUID.randomUUID().toString();
        }
    }

}
