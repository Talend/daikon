package org.talend.daikon.crypto.digest;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * A collection of {@link DigestSource} helpers to ease use of {@link Digester}.
 *
 * @see Digester
 */
public class DigestSources {

    /**
     * @return A simple SHA256 digest for simplistic use cases (not recommended, see {@link #pbkDf2(byte[])}).
     */
    public static DigestSource sha256() {
        return DigestUtils::sha256Hex;
    }

    /**
     * <p>
     * Returns a PBKDF2 (with Hmac SHA256) digester. Please note <code>salt</code> must remain the same if you plan on
     * having multiple {@link Digester} in your code.
     * </p>
     * <p>
     * As recommandation, you may initialize a salt using {@link org.talend.daikon.crypto.KeySources#random(int)}.
     * </p>
     *
     * @param salt The salt to be used to digest value.
     * @return A {@link DigestSource} implementation using PBKDF2 and provided <code>salt</code>
     */
    public static DigestSource pbkDf2(byte[] salt) {
        return value -> {
            try {
                KeySpec spec = new PBEKeySpec(value.toCharArray(), salt, 65536, 256);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                return new String(factory.generateSecret(spec).getEncoded(), StandardCharsets.UTF_8);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IllegalStateException("Unable digest value.", e);
            }
        };
    }

}
