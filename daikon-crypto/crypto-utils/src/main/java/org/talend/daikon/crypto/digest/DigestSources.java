package org.talend.daikon.crypto.digest;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.talend.daikon.crypto.EncodingUtils;
import org.talend.daikon.crypto.KeySource;
import org.talend.daikon.crypto.KeySources;

/**
 * A collection of {@link DigestSource} helpers to ease use of {@link Digester}.
 *
 * @see Digester
 */
public class DigestSources {

    /**
     * @return A simple SHA256 digest for simplistic use cases (not recommended, see {@link #pbkDf2()}).
     */
    public static DigestSource sha256() {
        return (data, salt) -> {
            final byte[] dataDigest = DigestUtils.sha256(data);
            if (salt == null || salt.length == 0) {
                return EncodingUtils.BASE64_ENCODER.apply(dataDigest);
            } else {
                try {
                    final byte[] dataBytes = data.getBytes(EncodingUtils.ENCODING);
                    final byte[] toDigest = new byte[salt.length + dataBytes.length];
                    System.arraycopy(salt, 0, toDigest, 0, salt.length);
                    System.arraycopy(dataBytes, 0, toDigest, salt.length, dataBytes.length);
                    final byte[] result = DigestUtils.sha256(toDigest);
                    return EncodingUtils.BASE64_ENCODER.apply(result);
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Unable to digest value.", e);
                }
            }
        };
    }

    /**
     * <p>
     * Returns a PBKDF2 (with Hmac SHA256) digester. Please note <code>salt</code> must remain the same if you plan on
     * having multiple {@link Digester} in your code.
     * </p>
     * <p>
     * As recommendation, you may initialize a salt using {@link org.talend.daikon.crypto.KeySources#random(int)}.
     * </p>
     *
     * @return A {@link DigestSource} implementation using PBKDF2 and provided <code>salt</code>
     */
    public static DigestSource pbkDf2() {
        return (value, salt) -> {
            KeySource keySource = KeySources.pbkDf2(value, salt, 256);
            try {
                return EncodingUtils.BASE64_ENCODER.apply(keySource.getKey());
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to digest value.", e);
            }
        };
    }

}
