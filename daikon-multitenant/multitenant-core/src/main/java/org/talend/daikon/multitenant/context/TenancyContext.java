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

import org.talend.daikon.multitenant.core.Tenant;

import java.io.Serializable;
import java.util.Optional;

/**
 * a context in which tenancy can be defined
 *
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenancyContext extends Serializable {

    /**
     * Obtains the current tenant.
     *
     * @return the <code>Tenant</code> in the current running context (will never be null).
     * @throws NoCurrentTenantException if no tenancy information is available.
     */
    Tenant getTenant();

    /**
     * Obtains the current tenant.
     *
     * @return the <code>Tenant</code> in the current running context.
     */
    Optional<Tenant> getOptionalTenant();

    /**
     * Changes the current tenant, or removes the tenancy information.
     *
     * @param tenant the new <code>Tenant</code>, or <code>null</code> if no further tenancy information should be stored
     */
    void setTenant(Tenant tenant);
}
