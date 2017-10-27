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
import org.talend.daikon.messages.MessageIssuer;
import org.talend.daikon.messages.MessageTypes;

/**
 * Default implementation of {@link MessageHeaderFactory} that will delegate
 * retrieval of most information from different providers.
 *
 * All providers are passed as constructor parameters.
 */
public class MessageHeaderFactoryImpl implements MessageHeaderFactory {

    private final IdGenerator idGenerator;

    private final ServiceInfoProvider serviceInfoProvider;

    private final TimestampProvider timestampProvider;

    private final UserProvider userProvider;

    private final TenantIdProvider tenantIdProvider;

    private final CorrelationIdProvider correlationIdProvider;

    private final SecurityTokenProvider securityTokenProvider;

    public MessageHeaderFactoryImpl(IdGenerator idGenerator, ServiceInfoProvider serviceInfoProvider,
            TimestampProvider timestampProvider, UserProvider userProvider, TenantIdProvider tenantIdProvider,
            CorrelationIdProvider correlationIdProvider, SecurityTokenProvider securityTokenProvider) {
        this.idGenerator = idGenerator;
        this.serviceInfoProvider = serviceInfoProvider;
        this.timestampProvider = timestampProvider;
        this.userProvider = userProvider;
        this.tenantIdProvider = tenantIdProvider;
        this.correlationIdProvider = correlationIdProvider;
        this.securityTokenProvider = securityTokenProvider;
    }

    @Override
    public MessageHeader createCommandHeader(String commandName) {
        return createMessageHeader(MessageTypes.COMMAND, commandName);
    }

    @Override
    public MessageHeader createEventHeader(String eventName) {
        return createMessageHeader(MessageTypes.EVENT, eventName);
    }

    protected MessageHeader createMessageHeader(MessageTypes type, String name) {
        return MessageHeader.newBuilder().setId(idGenerator.generateEventId())
                .setTimestamp(timestampProvider.getCurrentTimestamp()).setIssuer(createMessageIssuer()).setType(type)
                .setName(name).setTenantId(tenantIdProvider.getTenantId()).setUserId(userProvider.getUserId())
                .setCorrelationId(correlationIdProvider.getCorrelationId())
                .setSecurityToken(securityTokenProvider.getSecurityToken()).build();
    }

    private MessageIssuer createMessageIssuer() {
        return MessageIssuer.newBuilder().setService(serviceInfoProvider.getServiceName())
                .setVersion(serviceInfoProvider.getServiceVersion()).build();
    }
}
