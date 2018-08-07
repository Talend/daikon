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
package org.talend.daikon.messages.header.consumer;

import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.messages.MessageHeader;

public class ExecutionContextUpdaterImpl implements ExecutionContextUpdater {

    private final CorrelationIdSetter correlationIdSetter;

    private final TenantIdSetter tenantIdSetter;

    private final UserIdSetter userIdSetter;

    private final SecurityTokenSetter securityTokenSetter;

    private final MessageHeaderExtractor messageHeaderExtractor = new MessageHeaderExtractor();

    public ExecutionContextUpdaterImpl(CorrelationIdSetter correlationIdSetter, TenantIdSetter tenantIdSetter,
            UserIdSetter userIdSetter, SecurityTokenSetter securityTokenSetter) {
        this.correlationIdSetter = correlationIdSetter;
        this.tenantIdSetter = tenantIdSetter;
        this.userIdSetter = userIdSetter;
        this.securityTokenSetter = securityTokenSetter;
    }

    @Override
    public void updateExecutionContext(IndexedRecord indexedRecord) {
        final MessageHeader messageHeader = this.messageHeaderExtractor.extractHeader(indexedRecord);
        if (this.correlationIdSetter != null) {
            this.correlationIdSetter.setCurrentCorrelationId(messageHeader.getCorrelationId());
        }
        if (this.tenantIdSetter != null) {
            this.tenantIdSetter.setCurrentTenantId(messageHeader.getTenantId());
        }
        if (this.userIdSetter != null) {
            this.userIdSetter.setCurrentUserId(messageHeader.getUserId());
        }
        if (this.securityTokenSetter != null) {
            this.securityTokenSetter.setCurrentSecurityToken(messageHeader.getSecurityToken());
        }
    }
}
