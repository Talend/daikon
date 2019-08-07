package org.talend.daikon.crypto;

/**
 * A interface to represent a source for key used in {@link Encryption}. Interface can also be implemented as a
 * {@link java.util.function.Supplier} of byte[].
 *
 * @see KeySources For a few helpers.
 */
@FunctionalInterface
public interface KeySource {

    /**
     * @return The key to use to encrypt/decrypt data in {@link Encryption}.
     */
    byte[] getKey() throws Exception;
}
