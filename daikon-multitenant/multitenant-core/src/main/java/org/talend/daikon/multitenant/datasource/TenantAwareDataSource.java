package org.talend.daikon.multitenant.datasource;

import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;

/**
 * A data source that is tenant aware, that switches the database name based on the current tenant.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * @author David Green (Tasktop Technologies Inc.)
 */
public class TenantAwareDataSource extends AbstractDatabaseSwitchingDataSource {

    @Override
    protected String getDatabaseName() {
        String dbName = null;
        TenancyContext tenancyContext = TenancyContextHolder.getContext();
        if (tenancyContext.getTenant() != null) {
            dbName = computeDatabaseName(tenancyContext.getTenant());
        }
        return dbName;
    }

    /**
     * Compute the database name based on a tenant. The default implementation uses the {@link Tenant#getIdentity() tenant
     * identity}
     * as the database name.
     * 
     * @param tenant the tenant, must not be null
     * @return the database name
     */
    protected String computeDatabaseName(Tenant tenant) {
        return tenant.getIdentity().toString();
    }
}
