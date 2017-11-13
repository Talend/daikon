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
package org.talend.daikon.multitenant.context;

import java.io.Serializable;

import org.talend.daikon.multitenant.core.Tenant;

/**
 * a context in which tenancy can be defined
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenancyContext extends Serializable {

    /**
     * Obtains the current tenant.
     * 
     * @return the <code>Tenant</code> or <code>null</code> if no tenancy information is available.
     */
    Tenant getTenant();

    /**
     * Changes the current tenant, or removes the tenancy information.
     * 
     * @param tenant
     * the new <code>Tenant</code>, or <code>null</code> if no further tenancy information should be stored
     */
    void setTenant(Tenant tenant);
}
