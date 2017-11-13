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
