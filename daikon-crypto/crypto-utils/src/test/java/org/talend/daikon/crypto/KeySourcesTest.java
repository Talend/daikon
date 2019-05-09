package org.talend.daikon.crypto;

import org.junit.Test;

import static org.junit.Assert.*;

public class KeySourcesTest {

    @Test
    public void shouldGenerateKeyUsingLength() throws Exception {
        final KeySource keySource = KeySources.machineUID(32);
        assertEquals(32, keySource.getKey().length);
    }

    @Test
    public void shouldGenerateFromRandom() throws Exception {
        assertSource(KeySources.random(16));
    }

    @Test
    public void shouldGenerateFromMachineUID() throws Exception {
        assertSource(KeySources.machineUID(16));
    }

    @Test
    public void shouldGenerateFromFixed() throws Exception {
        assertSource(KeySources.fixedKey("DataPrepIsSoCool"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromMissingSystemProperty() throws Exception {
        assertSource(KeySources.systemProperty("missingSystemProperty"));
    }

    @Test()
    public void shouldGenerateFromSystemProperty() throws Exception {
        try {
            System.setProperty("aes.encryption.key", "DataPrepIsSoCool");
            assertSource(KeySources.systemProperty("aes.encryption.key"));
        } finally {
            System.clearProperty("aes.encryption.key");
        }
    }

    private void assertSource(KeySource keySource) throws Exception {
        assertNotNull(keySource.getKey());

        // Ensure key remain the same between 2 calls
        final byte[] key1 = keySource.getKey();
        final byte[] key2 = keySource.getKey();
        assertArrayEquals(key1, key2);

        // Ensure key source can be used in Encryption
        final Encryption encryption = new Encryption(keySource, CipherSources.aes());
        final String original = "toEncrypt";
        assertEquals(original, encryption.decrypt(encryption.encrypt(original)));
    }
}