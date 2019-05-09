package org.talend.daikon.crypto;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Optional;

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
}
