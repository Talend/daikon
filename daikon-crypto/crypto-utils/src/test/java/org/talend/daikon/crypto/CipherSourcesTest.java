package org.talend.daikon.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;

import org.junit.Test;

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

    @Test
    public void shouldGenerateDifferentValuesWithBlowfish() throws Exception {
        final CipherSource source = CipherSources.blowfish();
        final String encrypt1 = source.encrypt(KeySources.machineUID(16), "String");
        final String encrypt2 = source.encrypt(KeySources.machineUID(16), "String");

        assertNotEquals(encrypt1, encrypt2);
    }

    @Test(expected = BadPaddingException.class)
    public void blowfishUnableToDecrypt() throws Exception {
        String aWonderfulString = "aWonderfulString";

        final Encryption encryptionAES = new Encryption(KeySources.machineUID(16), CipherSources.getDefault());
        String encryptedAESString = encryptionAES.encrypt(aWonderfulString);

        final Encryption encryptionBlowfish = new Encryption(KeySources.machineUID(16), CipherSources.blowfish());

        final String decryptedString = encryptionBlowfish.decrypt(encryptedAESString);
    }

    @Test
    public void shouldRoundtripWithBlowfish() throws Exception {
        assertRoundTrip(CipherSources.blowfish());
    }

    @Test
    public void changeIVEncryptionStringBlowfish() throws Exception {
        String expectedString = "aStringWithBlowfish";
        String badEncryptedString = changeIVEncryptionString(expectedString, CipherSources.blowfish());
        assertNotEquals(expectedString, badEncryptedString);
    }

    @Test(expected = AEADBadTagException.class)
    public void changeIVEncryptionStringAESGCM() throws Exception {
        changeIVEncryptionString("aWonderfulString", CipherSources.aesGcm(16));
    }

    private String changeIVEncryptionString(String expectedString, CipherSource cipherSource) throws Exception {
        final Encryption encryption = new Encryption(KeySources.machineUID(16), cipherSource);

        String encryptedResult = encryption.encrypt(expectedString);

        // modify encrypted String
        char[] encryptedChar = encryptedResult.toCharArray();
        encryptedChar[0] = (char) (encryptedChar[0] + 1);
        encryptedResult = String.valueOf(encryptedChar);

        // check that decryption
        return encryption.decrypt(encryptedResult);
    }

    @Test
    public void changeEncryptedPayloadStringBlowfish() throws Exception {
        String expectedString = "changePayloadStringWithBlowfish";
        String badEncryptedResult = changeEncryptedPayloadString(expectedString, CipherSources.blowfish());
        assertNotEquals(expectedString, badEncryptedResult);
    }

    @Test(expected = AEADBadTagException.class)
    public void changeEncryptedPayloadStringAESGCM() throws Exception {
        changeEncryptedPayloadString("changePayloadStringWithAES", CipherSources.aesGcm(16));
    }

    private String changeEncryptedPayloadString(String expectedString, CipherSource cipherSource) throws Exception {
        final Encryption encryption = new Encryption(KeySources.machineUID(16), cipherSource);

        String encryptedResult = encryption.encrypt(expectedString);

        // modify encrypted String
        char[] encryptedChar = encryptedResult.toCharArray();
        encryptedChar[10] = (char) (encryptedChar[10] + 1);
        encryptedResult = String.valueOf(encryptedChar);

        // check that decryption
        return encryption.decrypt(encryptedResult);
    }

    private void assertRoundTrip(CipherSource cipherSource) throws Exception {
        final Encryption encryption = new Encryption(KeySources.machineUID(16), cipherSource);

        // when
        final String roundTrip = encryption.decrypt(encryption.encrypt("MyPlainText"));

        // then
        assertEquals(roundTrip, "MyPlainText");
    }

}