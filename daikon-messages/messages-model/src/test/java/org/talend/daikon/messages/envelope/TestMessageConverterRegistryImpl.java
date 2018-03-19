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

import org.junit.Assert;
import org.junit.Test;

public class TestMessageConverterRegistryImpl {

    private MessageConverterRegistryImpl messageConverterRegistry = new MessageConverterRegistryImpl();

    @Test
    public void testGetMessageConverter() {
        messageConverterRegistry.registerConverter("format1", new Message1Converter());
        messageConverterRegistry.registerConverter("format2", new Message2Converter());

        Assert.assertEquals(Message1Converter.class, messageConverterRegistry.getMessageConverter("format1").getClass());
        Assert.assertEquals(Message2Converter.class, messageConverterRegistry.getMessageConverter("format2").getClass());

    }

    private static class Message1Converter implements MessageConverter {

        @Override
        public <T> T deserialize(String content, Class<T> clazz) {
            return null;
        }

        @Override
        public <T> String serialize(T content) {
            return "";
        }
    }

    private static class Message2Converter implements MessageConverter {

        @Override
        public <T> T deserialize(String content, Class<T> clazz) {
            return null;
        }

        @Override
        public <T> String serialize(T content) {
            return "";
        }
    }

}
