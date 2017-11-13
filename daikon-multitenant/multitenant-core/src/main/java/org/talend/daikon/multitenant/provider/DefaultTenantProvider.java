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
 * Basic implementation of tenant provider which provides a {@link DefaultTenant} with the given identity. This is
 * useful when only the identity of the tenant is needed to be stored.
 * 
 * @author clint.morgan (Tasktop Technologies Inc.)
 * 
 */
public class DefaultTenantProvider implements TenantProvider {

    @Override
    public Tenant findTenant(Object identity) {
        DefaultTenant result = new DefaultTenant();
        result.setIdentity(identity);
        return result;
    }

}
