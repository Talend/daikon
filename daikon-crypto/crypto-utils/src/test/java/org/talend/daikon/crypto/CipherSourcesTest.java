package org.talend.daikon.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.util.Base64;

public class CipherSourcesTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInvalidTagLength() throws Exception {
        assertRoundTrip(CipherSources.aesGcm(12, 4, null));
    }

    @Test
    public void shouldRoundtripWithDefault() throws Exception {
        assertRoundTrip(CipherSources.getDefault());
    }

    @Test
    public void shouldRoundtripWithAESGCMAndBouncyCastle() throws Exception {
        assertRoundTrip(CipherSources.aesGcm(12, 16, new BouncyCastleProvider()));
    }

    @Test
    public void shouldRoundtripPBKDF2() throws Exception {
        assertRoundTrip(CipherSources.getDefault(), KeySources.pbkDf2("DataPrepIsSoCool", KeySources.random(16).getKey(), 256));
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
    public void shouldRoundtripWithAESAndDifferentTagLength() throws Exception {
        assertRoundTrip(CipherSources.aesGcm(16, 16, null));
    }

    @Test
    public void shouldRoundtripWithAESAndBouncyCastle() throws Exception {
        assertRoundTrip(CipherSources.aes(new BouncyCastleProvider()));
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

        final Encryption encryptionAES = new Encryption(KeySources.machineUID(16), CipherSources.aes());
        String encryptedAESString = encryptionAES.encrypt(aWonderfulString);

        final Encryption encryptionBlowfish = new Encryption(KeySources.machineUID(16), CipherSources.blowfish());

        encryptionBlowfish.decrypt(encryptedAESString);
    }

    @Test
    public void shouldRoundtripWithBlowfish() throws Exception {
        assertRoundTrip(CipherSources.blowfish());
    }

    @Test
    public void shouldRoundtripWithBlowfishAndBouncyCastle() throws Exception {
        assertRoundTrip(CipherSources.blowfish(new BouncyCastleProvider()));
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
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedResult.getBytes());
        encryptedBytes[0] = (byte) ~encryptedBytes[0];

        // check that decryption
        return encryption.decrypt(Base64.getEncoder().encodeToString(encryptedBytes));
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
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedResult.getBytes());
        encryptedBytes[10] = (byte) ~encryptedBytes[10];

        // check that decryption
        return encryption.decrypt(Base64.getEncoder().encodeToString(encryptedBytes));
    }

    private void assertRoundTrip(CipherSource cipherSource) throws Exception {
        assertRoundTrip(cipherSource, KeySources.machineUID(16));
    }

    private void assertRoundTrip(CipherSource cipherSource, KeySource keySource) throws Exception {
        final Encryption encryption = new Encryption(KeySources.machineUID(16), cipherSource);

        // when
        final String roundTrip = encryption.decrypt(encryption.encrypt("MyPlainText"));

        // then
        assertEquals(roundTrip, "MyPlainText");
    }

}