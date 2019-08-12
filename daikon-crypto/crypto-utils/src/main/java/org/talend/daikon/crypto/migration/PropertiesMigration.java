package org.talend.daikon.crypto.migration;

import java.util.Set;

import org.talend.daikon.crypto.PropertiesEncryption;

public class PropertiesMigration {

    private final EncryptionMigration migration;

    private final String input;

    private final Set<String> propertyNames;

    private final PropertiesEncryption propertiesEncryption;

    public PropertiesMigration(EncryptionMigration migration, String input, Set<String> propertyNames) {
        this.migration = migration;
        this.input = input;
        this.propertyNames = propertyNames;

        propertiesEncryption = new PropertiesEncryption(migration.getTarget());
    }

    public void migrate() {
        propertiesEncryption.encryptAndSave(this.input, this.propertyNames, s -> {
            try {
                return migration.migrate(s);
            } catch (Exception e) {
                return s;
            }
        });
    }
}
