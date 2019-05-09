package org.talend.daikon.crypto;

import javax.crypto.Cipher;

/**
 * Represents encryption/decryption operations done with a {@link Cipher}.
 * 
 * @see Encryption
 * @see KeySources
 * @see CipherSources
 */
public interface CipherSource {

    /**
     * Encrypt provided <code>data</code> using {@link KeySource source} for encryption algorithm initialization.
     * 
     * @param source The {@link KeySource} that provides the key to initialize encryption algorithm.
     * @param data The raw data to encrypt.
     * @return An encrypted version of the data.
     * @throws Exception In case of any exception related to algorithm initialization.
     */
    String encrypt(KeySource source, String data) throws Exception;

    /**
     *
     * @param source The {@link KeySource} that provides the key to initialize encryption algorithm.
     * @param data The encrypted data to decrypt.
     * @return A decrypted version of the data.
     * @throws Exception In case of any exception related to algorithm initialization or value decryption.
     */
    String decrypt(KeySource source, String data) throws Exception;
}
