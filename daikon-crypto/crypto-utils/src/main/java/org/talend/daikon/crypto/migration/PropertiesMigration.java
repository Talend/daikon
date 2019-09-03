package org.talend.daikon.crypto.migration;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.crypto.PropertiesEncryption;

/**
 * Provide a migration helper for property files.
 *
 * @see PropertiesEncryption
 */
public class PropertiesMigration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesMigration.class);

    private final EncryptionMigration migration;

    private final String input;

    private final Set<String> propertyNames;

    private final PropertiesEncryption propertiesEncryption;

    /**
     * Builds a migration helper for property files.
     *
     * @param migration The {@link EncryptionMigration} to handle value migrations.
     * @param input The property file name (with an absolute path).
     * @param propertyNames The names of the properties to migrate.
     */
    public PropertiesMigration(EncryptionMigration migration, String input, Set<String> propertyNames) {
        this.migration = migration;
        this.input = input;
        this.propertyNames = propertyNames;

        propertiesEncryption = new PropertiesEncryption(migration.getTarget());
    }

    /**
     * Performs the migration of the <code>input</code> property file.
     *
     * @see EncryptionMigration#migrate(String)
     */
    public void migrate() {
        propertiesEncryption.encryptAndSave(input, propertyNames, s -> {
            try {
                return migration.migrate(s);
            } catch (Exception e) {
                LOGGER.error("Unable to migrate input '{}' (keep previous value).", s);
                return s;
            }
        });
    }
}
