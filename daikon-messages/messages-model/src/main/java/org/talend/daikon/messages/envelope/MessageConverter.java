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
package org.talend.daikon.messages.envelope;

/**
 * Converts a message payload to string and the other way around.
 */
public interface MessageConverter {

    /**
     * Deserializes the provided message payload
     *
     * @param content the provided content
     * @param <T> the expected type
     * @return the deserialized message payload
     */
    <T> T deserialize(String content);

    /**
     * Serializes the provided message payload to string
     *
     * @param content the message payload
     * @param <T> the message payload type
     * @return the serialized form of the payload.
     */
    <T> String serialize(T content);

}
