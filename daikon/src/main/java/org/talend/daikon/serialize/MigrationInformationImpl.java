package org.talend.daikon.serialize;

public class MigrationInformationImpl implements MigrationInformation {

    boolean migrated;

    @Override
    public boolean isMigrated() {
        return migrated;
    }
}
