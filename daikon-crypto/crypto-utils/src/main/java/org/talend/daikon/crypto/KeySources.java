package org.talend.daikon.crypto;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Enumeration;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.io.IOUtils;

/**
 * A collection of {@link KeySource} helpers to ease use of {@link Encryption}.
 *
 * @see Encryption
 */
public class KeySources {

    // Private constructor to ensure static access to helpers.
    private KeySources() {
    }

    /**
     * @return A {@link KeySource} implementation that returns a empty key. This can be helpful to disable salt in
     * {@link org.talend.daikon.crypto.digest.Digester}.
     */
    public static KeySource empty() {
        return () -> new byte[0];
    }

    /**
     * <p>
     * Returns a {@link KeySource} that generates a random key using {@link SecureRandom#getInstanceStrong()}.
     * </p>
     * <p>
     * Please note that {@link KeySource#getKey()} always returns the same value when using the <b>same</b>
     * {@link KeySource} instance. Two different {@link KeySource} return <b>different</b> values.
     * </p>
     * <p>
     * When using this {@link KeySource}, you must save/keep the generated value if you plan on reusing it later on
     * (after a JVM restart for instance).
     * </p>
     * 
     * @param length The length of generated key.
     * @return A {@link KeySource} that uses a random key.
     * @see SecureRandom#getInstanceStrong()
     */
    public static KeySource random(int length) {
        return new KeySource() {

            private byte[] key;

            @Override
            public synchronized byte[] getKey() {
                if (key == null) {
                    key = new byte[length];
                    final SecureRandom random = new SecureRandom();
                    random.nextBytes(key);
                }
                return key;
            }
        };
    }

    /**
     * Returns a {@link KeySource} using {@link NetworkInterface} to generate a key specific to the machine MAC
     * addresses that executes this code.
     * 
     * @return A {@link KeySource} using the provided MAC addresses from {@link NetworkInterface} for key generation.
     * @see NetworkInterface#getNetworkInterfaces()
     */
    public static KeySource machineUID(int uidLength) {
        return () -> {
            byte[] key = new byte[uidLength];
            final Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            int keyPos = 0;
            while (networks.hasMoreElements()) {
                final NetworkInterface network = networks.nextElement();
                final byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        key[keyPos++ % key.length] += b;
                    }
                }
            }
            return key;
        };
    }

    /**
     * Returns a {@link KeySource} using the provided <code>passphrase</code>. Please note the value of
     * <code>passphrase</code> should be random, any constant value used here will cause security issues.
     * 
     * @param key The passphrase to use in {@link KeySource}.
     * @return A {@link KeySource} using the provided <code>passphrase</code> as key.
     * @deprecated
     */
    public static KeySource fixedKey(String key) {
        return key::getBytes;
    }

    /**
     * Returns a {@link KeySource} using the provided <code>systemProperty</code> to find key. Please note an exception
     * is thrown if system property is missing to prevent any hard coded fall back.
     * 
     * @param systemProperty The system property name that contains the key to be used.
     * @return A {@link KeySource} using the provided <code>systemProperty</code> to find key in system properties.
     * @see System#getProperty(String)
     */
    public static KeySource systemProperty(String systemProperty) {
        return () -> Optional.ofNullable(System.getProperty(systemProperty)) //
                .orElseThrow(() -> new IllegalArgumentException("System property '" + systemProperty + "' not found")) //
                .getBytes();
    }

    /**
     * <p>
     * Returns a {@link KeySource} using the provided password, salt and keyLength values to generate a SecretKey using
     * PBKDF2 (with Hmac SHA256) digester.
     * </p>
     * <p>
     * As recommendation, you may initialize a salt using {@link org.talend.daikon.crypto.KeySources#random(int)}.
     * </p>
     *
     * @return A {@link KeySource} implementation using PBKDF2 and provided <code>salt</code>
     */
    public static KeySource pbkDf2(String password, byte[] salt, int keyLength) {
        return () -> {
            if (salt == null || salt.length == 0) {
                throw new IllegalArgumentException("Cannot use pbkDf2 with empty or null salt.");
            }
            try {
                KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, keyLength);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                return factory.generateSecret(spec).getEncoded();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IllegalStateException("Unable to generate key.", e);
            }
        };
    }

    /**
     * Builds a {@link KeySource} that reads from the given <code>fileName</code>.
     * <ul>
     * <li>File is looked up using current thread's classloader (and using
     * {@link ClassLoader#getResourceAsStream(String)}.</li>
     * <li>If <code>fileName</code> is not found, a file is created and contains a default key (generated using
     * <code>defaultKeySource</code> parameter</li>
     * </ul>
     * 
     * @param fileName The file name that contains a key.
     * @param defaultKeySource A non-null {@link KeySource} to use if file name cannot be found.
     * @return A {@link KeySource} that reads key from file or silently generate a file name for next reads.
     * @throws IllegalArgumentException if <code>defaultKeySource</code> is null.
     */
    public static KeySource file(String fileName, KeySource defaultKeySource) {
        if (defaultKeySource == null) {
            throw new IllegalArgumentException("Default key source cannot be null.");
        }
        return () -> {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try (final InputStream resource = classLoader.getResourceAsStream(fileName)) {
                if (resource == null) {
                    final byte[] key = defaultKeySource.getKey();
                    try (FileOutputStream fos = new FileOutputStream(fileName)) {
                        fos.write(key);
                        fos.flush();
                    }
                    return key;
                } else {
                    return IOUtils.toByteArray(resource);
                }
            }
        };
    }
}
