package org.talend.daikon.crypto.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.daikon.crypto.CipherSources;
import org.talend.daikon.crypto.Encryption;
import org.talend.daikon.crypto.KeySources;

public class EncryptionMigrationTest {

    @Test
    public void shouldReencrypt() throws Exception {
        // given
        final Encryption source = new Encryption(KeySources.fixedKey("DataPrepIsSoCool"), CipherSources.aes());
        final Encryption target = new Encryption(KeySources.random(16), CipherSources.getDefault());
        final EncryptionMigration migration = EncryptionMigration.migrate(source, target);
        final String originalEncrypted = "JP6lC6hVeu3wRZA1Tzigyg==";

        // when
        final String reencrypt = migration.migrate(originalEncrypted);
        final String decrypted = target.decrypt(reencrypt);

        // then
        assertEquals("5ecr3t", decrypted);
        assertNotEquals(originalEncrypted, reencrypt);
    }

    @Test
    public void shouldRememberEncryptedValues() throws Exception {
        // given
        final Encryption source = new Encryption(KeySources.fixedKey("DataPrepIsSoCool"), CipherSources.aes());
        final Encryption target = new Encryption(KeySources.random(16), CipherSources.getDefault());
        final EncryptionMigration migration = EncryptionMigration.migrate(source, target);
        final String originalEncrypted = "JP6lC6hVeu3wRZA1Tzigyg==";

        // when
        final String encryptAndMigrated = migration.migrate(originalEncrypted);

        // then
        assertTrue(migration.isMigrated(encryptAndMigrated));
        assertFalse(migration.isMigrated(originalEncrypted));
    }

    @Test
    public void shouldMigrateEncryptedString() throws Exception {
        // given
        final Encryption source = new Encryption(KeySources.fixedKey("DataPrepIsSoCool"), CipherSources.aes());
        final Encryption target = new Encryption(KeySources.random(16), CipherSources.getDefault());
        final EncryptionMigration migration = EncryptionMigration.migrate(source, target);
        final String originalEncrypted = "JP6lC6hVeu3wRZA1Tzigyg==";

        // when
        final String decrypt = source.decrypt(originalEncrypted);
        final String encryptAndMigrated1 = migration.migrate(originalEncrypted);
        final String encryptAndMigrated2 = migration.migrate(originalEncrypted);
        final String decryptAfterMigrated1 = target.decrypt(encryptAndMigrated1);
        final String decryptAfterMigrated2 = target.decrypt(encryptAndMigrated2);

        // then
        assertEquals("5ecr3t", decrypt);
        assertEquals("5ecr3t", decryptAfterMigrated1);
        assertEquals("5ecr3t", decryptAfterMigrated2);
        assertNotEquals(originalEncrypted, encryptAndMigrated1);
        assertNotEquals(encryptAndMigrated1, encryptAndMigrated2);
    }
}