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
 * A default implementation of a {@link Tenant}. In addition to the tenant identity, it provides a link to arbitrary
 * data for the tenant.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public class DefaultTenant implements Tenant {

    private static final long serialVersionUID = 1L;

    private Object identity;

    private Object data;

    public DefaultTenant() {

    }

    public DefaultTenant(Object identity, Object data) {
        this.identity = identity;
        this.data = data;
    }

    @Override
    public Object getIdentity() {
        return identity;
    }

    public void setIdentity(Object identity) {
        this.identity = identity;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.identity == null) {
            return super.equals(obj);
        }

        if (!(obj instanceof DefaultTenant)) {
            return false;
        }
        DefaultTenant otherTenant = (DefaultTenant) obj;
        return this.identity.equals(otherTenant.identity);
    }

    @Override
    public int hashCode() {
        if (identity == null) {
            return super.hashCode();
        }
        return identity.hashCode();
    }

    @Override
    public String toString() {
        return "DefaultTenant [identity=" + identity + ", data=" + data + "]";
    }
}
