package org.talend.daikon.crypto;

import java.security.Key;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A helper class to provide common {@link CipherSource} implementations.
 */
public class CipherSources {

    // Here to ensure helper-style access to methods
    private CipherSources() {
    }

    /**
     * @return A default {@link CipherSource} implementation
     * @see #aesGcm(int)
     */
    public static CipherSource getDefault() {
        return aesGcm(16);
    }

    /**
     * @return A {@link CipherSource} using AES encryption.
     */
    public static CipherSource aes() {
        return new CipherSource() {

            private Cipher get(KeySource source, int mode) throws Exception {
                final Cipher c = Cipher.getInstance("AES");
                final Key keySpec = new SecretKeySpec(source.getKey(), "AES");
                c.init(mode, keySpec);
                return c;
            }

            @Override
            public String encrypt(KeySource source, String data) throws Exception {
                final byte[] encryptedBytes = get(source, Cipher.ENCRYPT_MODE).doFinal(data.getBytes(EncodingUtils.ENCODING));
                return EncodingUtils.BASE64_ENCODER.apply(encryptedBytes);
            }

            @Override
            public String decrypt(KeySource source, String data) throws Exception {
                final byte[] bytes = EncodingUtils.BASE64_DECODER.apply(data.getBytes());
                return new String(get(source, Cipher.DECRYPT_MODE).doFinal(bytes), EncodingUtils.ENCODING);
            }
        };
    }

    /**
     * @return A {@link CipherSource} using AES/GCM/NoPadding encryption.
     */
    public static CipherSource aesGcm(int ivLength) {
        if ((ivLength & 7) != 0 && Stream.of(128, 120, 112, 104, 96).noneMatch(i -> i == ivLength)) {
            throw new IllegalArgumentException("Invalid IV length");
        }

        return new CipherSource() {

            private Cipher get(KeySource source, int mode, byte[] iv) throws Exception {
                final Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
                final byte[] sourceKey = source.getKey();
                final Key key = new SecretKeySpec(sourceKey, "AES");
                final GCMParameterSpec spec = new GCMParameterSpec(ivLength * 8, iv);
                c.init(mode, key, spec);
                return c;
            }

            @Override
            public String encrypt(KeySource source, String data) throws Exception {
                final byte[] dataBytes = data.getBytes(EncodingUtils.ENCODING);
                final byte[] iv = KeySources.random(ivLength).getKey();

                final Cipher cipher = get(source, Cipher.ENCRYPT_MODE, iv);

                final byte[] encryptedData = cipher.doFinal(dataBytes);
                final byte[] encryptedBytes = new byte[encryptedData.length + ivLength];
                System.arraycopy(iv, 0, encryptedBytes, 0, ivLength);
                System.arraycopy(encryptedData, 0, encryptedBytes, ivLength, encryptedData.length);

                return EncodingUtils.BASE64_ENCODER.apply(encryptedBytes);
            }

            @Override
            public String decrypt(KeySource source, String data) throws Exception {
                final byte[] encryptedBytes = EncodingUtils.BASE64_DECODER.apply(data.getBytes(EncodingUtils.ENCODING));

                final byte[] iv = new byte[ivLength];
                System.arraycopy(encryptedBytes, 0, iv, 0, ivLength);

                final Cipher cipher = get(source, Cipher.DECRYPT_MODE, iv);
                return new String(cipher.doFinal(encryptedBytes, ivLength, encryptedBytes.length - ivLength), EncodingUtils.ENCODING);
            }
        };
    }
}
