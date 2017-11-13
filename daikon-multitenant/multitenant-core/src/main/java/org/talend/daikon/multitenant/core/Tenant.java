// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.core;

import java.io.Serializable;

/**
 * Identifies a tenant in a multi-tenanted architecture, where a tenant is used to identify a logical partition of
 * application data and/or configuration.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface Tenant extends Serializable {

    /**
     * Get the identity of the tenant.
     * 
     * @return tenant identity
     */
    Object getIdentity();
}
