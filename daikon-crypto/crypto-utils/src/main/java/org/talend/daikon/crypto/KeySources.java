package org.talend.daikon.crypto;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
     * An implementation of {@link KeySource} that reads keys from a property file. Default values are read from
     * <code>key.dat</code>, you may override default values by specifying an external file using
     * <code>encryption.keys.file</code> system property.
     *
     * @param propertyName The property name to look up for in properties file.
     * @return The key as read from key file or <code>null</code> if not found.
     */
    public static KeySource file(String propertyName) {
        return new FileSource(propertyName);
    }

    private static class FileSource implements KeySource {

        private static final Logger LOGGER = Logger.getLogger(FileSource.class.getCanonicalName());

        private final Properties properties = new Properties();

        private final String propertyName;

        private FileSource(String propertyName) {
            init();
            this.propertyName = propertyName;
        }

        private void init() {
            try {
                try (InputStream standardKeyFile = KeySources.class.getResourceAsStream("key.dat")) {
                    properties.load(standardKeyFile);
                }
                final String filePath = System.getProperty("encryption.keys.file");
                if (StringUtils.isNotEmpty(filePath)) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        try (InputStream in = new FileInputStream(file)) {
                            properties.load(in);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to load key file.", e);
            }
        }

        @Override
        public byte[] getKey() throws Exception {
            final Object propertyObject = properties.get(propertyName);
            if (propertyObject == null) {
                throw new IllegalArgumentException("Property '" + propertyName + "' does not exist.");
            }
            final String o = String.valueOf(propertyObject);
            return EncodingUtils.BASE64_DECODER.apply(o.getBytes(EncodingUtils.ENCODING));
        }
    }
}
