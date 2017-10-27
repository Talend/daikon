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

/**
 * Accesses the current request context and provides the identifier of the current tenant.
 */
public interface TenantIdProvider {

    /**
     * @return the current tenant identifier or null if not in a multi-tenant context
     */
    String getTenantId();

}
