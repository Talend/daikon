package org.talend.daikon.crypto;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

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

    @Test
    public void shouldGenerateFromPBKDF2() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 128));
    }

    @Test
    public void shouldGenerateFromPBKDF2Iterations() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 256, 310000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithNegativeKeyLength() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), -1, 310000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithZeroKeyLength() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 0, 310000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithNegativeIterations() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 256, -1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithLowIterations() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 256, 50000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithZeroIterations() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 256, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithNullSalt() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", null, 256, 310000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromPBKDF2WithEmptySalt() throws Exception {
        assertSource(KeySources.pbkDf2("DataPrepIsSoCool", new byte[0], 256, 0));
    }

    @Test
    public void shouldGenerateSameKeyFromPBKDF2() throws Exception {
        byte[] salt = KeySources.random(16).getKey();
        KeySource key1 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 310000);
        KeySource key2 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 310000);
        assertTrue(Arrays.equals(key1.getKey(), key2.getKey()));
    }

    @Test
    public void shouldNotGenerateSameKeyFromPBKDF2WithDifferentIterationCount() throws Exception {
        byte[] salt = KeySources.random(16).getKey();
        KeySource key1 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 310000);
        KeySource key2 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 300000);
        assertFalse(Arrays.equals(key1.getKey(), key2.getKey()));
    }

    @Test
    public void shouldNotGenerateSameKeyFromPBKDF2WithDifferentKeyLength() throws Exception {
        byte[] salt = KeySources.random(16).getKey();
        KeySource key1 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 128, 310000);
        KeySource key2 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 310000);
        assertFalse(Arrays.equals(key1.getKey(), key2.getKey()));
    }

    @Test
    public void shouldNotGenerateSameKeyFromPBKDF2WithDifferentSalt() throws Exception {
        byte[] salt = KeySources.random(16).getKey();
        KeySource key1 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 310000);
        salt = KeySources.random(16).getKey();
        KeySource key2 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 256, 310000);
        assertFalse(Arrays.equals(key1.getKey(), key2.getKey()));
    }

    @Test
    public void shouldGenerateKeyWithGivenKeyLengthFromPBKDF2() throws Exception {
        byte[] salt = KeySources.random(16).getKey();
        KeySource key1 = KeySources.pbkDf2("DataPrepIsSoCool", salt, 128, 310000);
        assertEquals(128 / 8, key1.getKey().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGenerateFromMissingSystemProperty() throws Exception {
        assertSource(KeySources.systemProperty("missingSystemProperty"));
    }

    @Test
    public void shouldGenerateFromSystemProperty() throws Exception {
        try {
            System.setProperty("aes.encryption.key", "DataPrepIsSoCool");
            assertSource(KeySources.systemProperty("aes.encryption.key"));
        } finally {
            System.clearProperty("aes.encryption.key");
        }
    }

    @Test
    public void testDefaultFileKeys() throws Exception {
        final String systemKey = new String(KeySources.file("system.encryption.key").getKey(), EncodingUtils.ENCODING);
        assertEquals("99ZwBDt1L9yMX2ApJx fnv94o99OeHbCGuIHTy22 V9O6cZ2i374fVjdV76VX9g49DG1r3n90hT5c1", systemKey);

        final String propertiesKey = new String(KeySources.file("properties.encryption.key").getKey(), EncodingUtils.ENCODING);
        assertEquals("Il faudrait trouver une passphrase plus originale que celle-ci!", propertiesKey);
    }

    @Test
    public void testFileKeyOverride() throws Exception {
        try {
            final File override = new File(KeySourcesTest.class.getResource("/key_override.dat").toURI());
            System.setProperty("encryption.keys.file", override.getAbsolutePath());

            final String systemKey = new String(KeySources.file("system.encryption.key").getKey(), EncodingUtils.ENCODING);
            assertEquals("99ZwBDt1L9yMX2ApJx fnv94o99OeHbCGuIHTy22 V9O6cZ2i374fVjdV76VX9g49DG1r3n90hT5c1", systemKey);

            final String propertiesKey = new String(KeySources.file("properties.encryption.key").getKey(),
                    EncodingUtils.ENCODING);
            assertEquals("it's base64 encoded", propertiesKey);
        } finally {
            System.setProperty("encryption.keys.file", "");
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