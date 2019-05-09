package org.talend.daikon.crypto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CipherSourcesTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInvalidIVLength() throws Exception {
        assertRoundTrip(CipherSources.aesGcm(33));
    }

    @Test
    public void shouldRoundtripWithDefault() throws Exception {
        assertRoundTrip(CipherSources.getDefault());
    }

    @Test
    public void shouldGenerateDifferentValuesWithDefault() throws Exception {
        final CipherSource source = CipherSources.getDefault();
        final String encrypt1 = source.encrypt(KeySources.machineUID(16), "String");
        final String encrypt2 = source.encrypt(KeySources.machineUID(16), "String");

        assertNotEquals(encrypt1, encrypt2);
    }

    @Test
    public void shouldRoundtripWithAES() throws Exception {
        assertRoundTrip(CipherSources.aes());
    }

    @Test
    public void shouldGenerateSameValuesWithAES() throws Exception {
        final CipherSource source = CipherSources.aes();
        final String encrypt1 = source.encrypt(KeySources.machineUID(16), "String");
        final String encrypt2 = source.encrypt(KeySources.machineUID(16), "String");

        assertEquals(encrypt1, encrypt2);
    }

    private void assertRoundTrip(CipherSource cipherSource) throws Exception {
        final Encryption encryption = new Encryption(KeySources.machineUID(16), cipherSource);

        // when
        final String roundTrip = encryption.decrypt(encryption.encrypt("MyPlainText"));

        // then
        assertEquals(roundTrip, "MyPlainText");
    }

}