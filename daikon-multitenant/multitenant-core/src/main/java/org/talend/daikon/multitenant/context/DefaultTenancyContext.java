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

import java.util.Optional;

/**
 * Default implementation of {@link TenancyContext}.
 *
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public class DefaultTenancyContext implements TenancyContext {

    private static final long serialVersionUID = 1L;

    private Tenant tenant;

    @Override
    public Tenant getTenant() {
        if (tenant == null) {
            throw new NoCurrentTenantException("No tenant in running context");
        }
        return tenant;
    }

    @Override
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public Optional<Tenant> getOptionalTenant() {
        return Optional.ofNullable(tenant);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DefaultTenancyContext other = (DefaultTenancyContext) obj;
        if (tenant == null) {
            if (other.tenant != null) {
                return false;
            }
        } else if (!tenant.equals(other.tenant)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultTenancyContext [tenant=" + tenant + "]";
    }

}
