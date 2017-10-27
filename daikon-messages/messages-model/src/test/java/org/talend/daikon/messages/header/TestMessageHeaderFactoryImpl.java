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

import org.apache.avro.AvroRuntimeException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.daikon.messages.MessageHeader;
import org.talend.daikon.messages.MessageTypes;

public class TestMessageHeaderFactoryImpl {

    private String messageIdMock = "messageIdMock";

    private String serviceMock = "serviceMock";

    private String versionMock = "versionMock";

    private long timestampMock = 1234L;

    private String userIdMock = "userIdMock";

    private String tenantIdMock = "tenantIdMock";

    private String correlationIdMock = "correlationIdMock";

    private String securityTokenMock = "securityTokenMock";

    @Before
    public void setup() {
        messageIdMock = "messageIdMock";
    }

    @Test
    public void testCommandCreation() {
        String name = "commandName";
        MessageHeaderFactory factory = createFactory();
        MessageHeader commandHeader = factory.createMessageHeader(MessageTypes.COMMAND, name);
        assertMessageHeader(commandHeader, MessageTypes.COMMAND, name);
    }

    @Test
    public void testEventCreation() {
        String name = "eventName";
        MessageHeaderFactory factory = createFactory();
        MessageHeader eventHeader = factory.createMessageHeader(MessageTypes.EVENT, name);
        assertMessageHeader(eventHeader, MessageTypes.EVENT, name);
    }

    @Test(expected = AvroRuntimeException.class)
    public void testInvalidHeaderCreation() {
        String name = "eventName";
        messageIdMock = null;
        MessageHeaderFactory factory = createFactory();
        factory.createMessageHeader(MessageTypes.EVENT, name);
    }

    private void assertMessageHeader(MessageHeader header, MessageTypes type, String name) {
        Assert.assertEquals(type, header.getType());
        Assert.assertEquals(name, header.getName());
        Assert.assertEquals(messageIdMock, header.getId());
        Assert.assertEquals(serviceMock, header.getIssuer().getService());
        Assert.assertEquals(versionMock, header.getIssuer().getVersion());
        Assert.assertEquals(timestampMock, header.getTimestamp(), 0);
        Assert.assertEquals(userIdMock, header.getUserId());
        Assert.assertEquals(tenantIdMock, header.getTenantId());
        Assert.assertEquals(correlationIdMock, header.getCorrelationId());
        Assert.assertEquals(securityTokenMock, header.getSecurityToken());
    }

    private MessageHeaderFactory createFactory() {
        return new MessageHeaderFactoryImpl(idGenerator(), serviceInfoProvider(), timestampProvider(), userProvider(),
                tenantIdProvider(), correlationIdProvider(), securityTokenProvider());
    }

    private IdGenerator idGenerator() {
        IdGenerator idGenerator = Mockito.mock(IdGenerator.class);
        Mockito.when(idGenerator.generateEventId()).thenReturn(messageIdMock);
        return idGenerator;
    }

    private ServiceInfoProvider serviceInfoProvider() {
        ServiceInfoProvider serviceInfoProvider = Mockito.mock(ServiceInfoProvider.class);
        Mockito.when(serviceInfoProvider.getServiceName()).thenReturn(serviceMock);
        Mockito.when(serviceInfoProvider.getServiceVersion()).thenReturn(versionMock);
        return serviceInfoProvider;
    }

    private TimestampProvider timestampProvider() {
        TimestampProvider timestampProvider = Mockito.mock(TimestampProvider.class);
        Mockito.when(timestampProvider.getCurrentTimestamp()).thenReturn(timestampMock);
        return timestampProvider;
    }

    private UserProvider userProvider() {
        UserProvider userProvider = Mockito.mock(UserProvider.class);
        Mockito.when(userProvider.getUserId()).thenReturn(userIdMock);
        return userProvider;
    }

    private TenantIdProvider tenantIdProvider() {
        TenantIdProvider tenantIdProvider = Mockito.mock(TenantIdProvider.class);
        Mockito.when(tenantIdProvider.getTenantId()).thenReturn(tenantIdMock);
        return tenantIdProvider;
    }

    private CorrelationIdProvider correlationIdProvider() {
        CorrelationIdProvider correlationIdProvider = Mockito.mock(CorrelationIdProvider.class);
        Mockito.when(correlationIdProvider.getCorrelationId()).thenReturn(correlationIdMock);
        return correlationIdProvider;
    }

    private SecurityTokenProvider securityTokenProvider() {
        SecurityTokenProvider securityTokenProvider = Mockito.mock(SecurityTokenProvider.class);
        Mockito.when(securityTokenProvider.getSecurityToken()).thenReturn(securityTokenMock);
        return securityTokenProvider;
    }

}
