package org.talend.daikon.crypto.digest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.talend.daikon.crypto.EncodingUtils;

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
                final byte[] digestedSalt = DigestUtils.sha256(salt);
                final byte[] result = new byte[digestedSalt.length + dataDigest.length];
                System.arraycopy(digestedSalt, 0, result, 0, digestedSalt.length);
                System.arraycopy(dataDigest, 0, result, digestedSalt.length, dataDigest.length);
                return EncodingUtils.BASE64_ENCODER.apply(result);
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
            if (salt == null || salt.length == 0) {
                throw new IllegalArgumentException("Cannot use pbkDf2 with empty or null salt.");
            }
            try {
                KeySpec spec = new PBEKeySpec(value.toCharArray(), salt, 65536, 256);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                final byte[] bytes = factory.generateSecret(spec).getEncoded();
                return EncodingUtils.BASE64_ENCODER.apply(bytes);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IllegalStateException("Unable to digest value.", e);
            }
        };
    }

}
