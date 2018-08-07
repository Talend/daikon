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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bguillon on 14/12/2017.
 */
public class MessageConverterRegistryImpl implements MessageConverterRegistry {

    private final Map<String, MessageConverter> messageConverters = new HashMap<>();

    public void registerConverter(String format, MessageConverter converter) {
        this.messageConverters.put(format, converter);
    }

    @Override
    public MessageConverter getMessageConverter(String format) {
        return messageConverters.get(format);
    }
}
