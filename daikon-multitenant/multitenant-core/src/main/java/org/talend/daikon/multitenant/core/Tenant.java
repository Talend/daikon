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
