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
package org.talend.daikon.messages.header;

import org.talend.daikon.messages.MessageHeader;
import org.talend.daikon.messages.MessageTypes;

/**
 * A factory for normalized {@link MessageHeader}
 */
public interface MessageHeaderFactory {

    /**
     * Creates a new message header for the provided message type and name
     * 
     * @param type the message type
     * @param messageName the message name
     * @return the newly created message header
     */
    MessageHeader createMessageHeader(MessageTypes type, String messageName);

}
