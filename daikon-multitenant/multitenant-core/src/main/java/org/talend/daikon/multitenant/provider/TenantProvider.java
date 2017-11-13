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
package org.talend.daikon.multitenant.provider;

import org.talend.daikon.multitenant.core.Tenant;

/**
 * A means of providing a tenant for any given tenant identity
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenantProvider {

    /**
     * Attempt to find a tenant based on a given tenant identity identity.
     * 
     * @param identity
     * the identify of a tenant, or null if there is none
     * @return a tenant for the corresponding identity, or <code>null</code> if the given identity has no corresponding
     * tenant.
     */
    Tenant findTenant(Object identity);
}
