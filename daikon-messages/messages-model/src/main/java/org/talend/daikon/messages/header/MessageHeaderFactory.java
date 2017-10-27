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

/**
 * A factory for normalized {@link MessageHeader}
 */
public interface MessageHeaderFactory {

    /**
     * Creates a new message header for a command with the provided name
     * 
     * @param commandName the name of the command
     * @return the header
     */
    MessageHeader createCommandHeader(String commandName);

    /**
     * Creates a new message header for an event with the provided name
     * @param eventName the name of the event
     * @return the header
     */
    MessageHeader createEventHeader(String eventName);

}
