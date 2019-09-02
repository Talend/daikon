package org.talend.daikon.crypto.migration;

import org.talend.daikon.crypto.Encryption;

/**
 * A class to help migrations from one {@link Encryption} to an other.
 *
 * @see #build(Encryption, Encryption)
 */
public class EncryptionMigration {

    private final Encryption source;

    private final Encryption target;

    private EncryptionMigration(Encryption source, Encryption target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Builds a {@link EncryptionMigration} object to help migrating values encrypted with <code>source</code> to values
     * encrypted with <code>target</code>.
     * 
     * @param source The <code>source</code> {@link Encryption} (cannot be null).
     * @param target The <code>target</code> {@link Encryption} (cannot be null).
     * @return A {@link EncryptionMigration} object.
     * @throws IllegalArgumentException If <code>source</code> or <code>target</code> is null.
     */
    public static EncryptionMigration build(Encryption source, Encryption target) {
        if (source == null) {
            throw new IllegalArgumentException("Source encryption cannot be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target encryption cannot be null.");
        }
        return new EncryptionMigration(source, target);
    }

    /**
     * <p>
     * This method uses <code>source</code> encryption to decrypt value and immediately encrypts value using
     * <code>target</code>.
     * </p>
     * 
     * @param originalEncrypted A value encrypted with <code>source</code>
     * @return An encrypted value with <code>target</code> to represent same decrypted value as original's.
     * @throws Exception In case of any unexpected exception during {@link Encryption#encrypt(String)} or
     * {@link Encryption#decrypt(String)}.
     */
    public String migrate(String originalEncrypted) throws Exception {
        return target.encrypt(source.decrypt(originalEncrypted));
    }

    public Encryption getTarget() {
        return target;
    }
}
