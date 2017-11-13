package org.talend.daikon.multitenant.datasource;

/**
 * A data source where the database name can be set for the context of the current thread.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public class ThreadlocalDatabaseSwitchingDataSource extends AbstractDatabaseSwitchingDataSource {

    private final ThreadLocal<String> databaseName = new ThreadLocal<String>();

    public void clearDatabaseName() {
        this.databaseName.set(null);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName.get();
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName.set(databaseName);
    }

}
