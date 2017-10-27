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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.daikon.messages.MessageEnvelope;
import org.talend.daikon.messages.MessageHeader;
import org.talend.daikon.messages.MessagePayload;
import org.talend.daikon.messages.MessageTypes;
import org.talend.daikon.messages.header.MessageHeaderFactory;

public class TestMessageEnvelopeHandlerImpl {

    private static final String FORMAT = "testFormat";

    private static final String MESSAGE_NAME = "messageName";

    private MessageConverterRegistry messageConverterRegistry = Mockito.mock(MessageConverterRegistry.class);

    private MessageHeaderFactory messageHeaderFactory = Mockito.mock(MessageHeaderFactory.class);

    private MessageHeader messageHeader = new MessageHeader();

    @Before
    public void setup() {

        Mockito.when(messageConverterRegistry.getMessageConverter(FORMAT)).thenReturn(new TestMessageConverter());

        Mockito.when(messageHeaderFactory.createCommandHeader(MESSAGE_NAME)).thenReturn(messageHeader);
    }

    @Test
    public void testWrapMessage() {
        AnyMessage anyMessage = new AnyMessage();
        anyMessage.setProperty1("value1");
        anyMessage.setProperty2("value2");

        MessageEnvelopeHandler handler = new MessageEnvelopeHandlerImpl(messageConverterRegistry, messageHeaderFactory);

        MessageEnvelope envelope = handler.wrap(MessageTypes.COMMAND, MESSAGE_NAME, anyMessage, FORMAT);

        Assert.assertNotNull(envelope);

        Assert.assertEquals(FORMAT, envelope.getPayload().getFormat());
        Assert.assertEquals(new TestMessageConverter().serialize(anyMessage), envelope.getPayload().getContent());
        Assert.assertEquals(messageHeader, envelope.getHeader());
    }

    @Test
    public void testUnwrapMessage() {
        AnyMessage anyMessage = new AnyMessage();
        anyMessage.setProperty1("value1");
        anyMessage.setProperty2("value2");

        MessageEnvelope envelope = MessageEnvelope.newBuilder().setPayload(MessagePayload.newBuilder().setFormat(FORMAT)
                .setContent(new TestMessageConverter().serialize(anyMessage)).build()).setHeader(messageHeader).build();

        MessageEnvelopeHandler handler = new MessageEnvelopeHandlerImpl(messageConverterRegistry, messageHeaderFactory);

        Object result = handler.unwrap(envelope);

        Assert.assertEquals(anyMessage, result);

    }

    private static class TestMessageConverter implements MessageConverter {

        @Override
        public <T> T deserialize(String content) {
            String[] props = content.split(";");
            AnyMessage message = new AnyMessage();
            message.setProperty1(props[0]);
            message.setProperty2(props[1]);
            return (T) message;
        }

        @Override
        public <T> String serialize(T content) {
            AnyMessage message = (AnyMessage) content;
            return message.getProperty1() + ";" + message.getProperty2();
        }
    }

    private static class AnyMessage {

        private String property1;

        private String property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AnyMessage that = (AnyMessage) o;

            if (property1 != null ? !property1.equals(that.property1) : that.property1 != null)
                return false;
            return property2 != null ? property2.equals(that.property2) : that.property2 == null;
        }

        @Override
        public int hashCode() {
            int result = property1 != null ? property1.hashCode() : 0;
            result = 31 * result + (property2 != null ? property2.hashCode() : 0);
            return result;
        }
    }

}
