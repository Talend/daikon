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

import org.talend.daikon.messages.MessageKey;

/**
 * A {@link MessageKey} factory - used to abstract and ease message keys creation.
 *
 * A message key can be used to partition messages in Kafka.
 *
 * When there is no functional constraints on messages
 * partitioning, the {@link #createMessageKey()} method can be used, it will integrate current tenant information
 * and add a random string in it.
 *
 * When messages partitioning must be controlled, the {@link #buildMessageKey()}} should be used to build a custom
 * message key using a {@link MessageKeyBuilder} implementation.
 *
 * In both cases, if the message key was created in a multi-tenant context, the key will contain the tenant identifier
 */
public interface MessageKeyFactory {

    /**
     * @return a message key without partitioning keys. Messages will be randomly distributed across partitions.
     */
    MessageKey createMessageKey();

    /**
     * @return a {@link MessageKeyBuilder} that can be used to create a message key with specific partitioning keys.
     * As a result, messages distribution across partitions will be predictable.
     */
    MessageKeyBuilder buildMessageKey();

    /**
     * Allows to build a message key with custom partitioning key / value pairs.
     */
    interface MessageKeyBuilder {

        /**
         * adds a partitioning key to the message key
         * 
         * @param key the name of the partitioning key
         * @param value the value of the partitioning key
         * @return this builder
         */
        MessageKeyBuilder withKey(String key, String value);

        /**
         * @return the resulting message key.
         */
        MessageKey build();

    }

}
