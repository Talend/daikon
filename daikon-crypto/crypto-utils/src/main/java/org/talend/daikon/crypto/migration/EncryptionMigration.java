package org.talend.daikon.crypto.migration;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.crypto.Encryption;
import org.talend.daikon.crypto.digest.DigestSources;
import org.talend.daikon.crypto.digest.Digester;

/**
 * A class to help migrations from one {@link Encryption} to an other.
 *
 * @see #build(Encryption, Encryption)
 */
public class EncryptionMigration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionMigration.class);

    private final Encryption source;

    private final Encryption target;

    private final Digester digester = new Digester(DigestSources.pbkDf2());

    private Set<String> migratedValues = new HashSet<>();

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
     * Given a <code>encrypted</code>, decrypts it using <code>source</code> encryption (if
     * <code>encrypted</code> is not known to this instance). If <code>encrypted</code> is known as a value
     * encrypted with <code>target</code>, uses <code>target</code> to decrypt provided value.
     * 
     * @param encrypted A encrypted value either generated with <code>source</code> or <code>target</code>
     * {@link Encryption}.
     * @return A decrypted value.
     * @throws Exception Thrown in case of unexpected exception
     * @see Encryption#decrypt(String)
     * @see #isMigrated(String)
     */
    public String decrypt(String encrypted) throws Exception {
        if (isMigrated(encrypted)) {
            return target.decrypt(encrypted);
        } else {
            return source.decrypt(encrypted);
        }
    }

    /**
     * Returns <code>true</code> is <code>encrypted</code> was created using {@link #migrate(String)} method,
     * <code>false</code> otherwise.
     * 
     * @param encrypted A encrypted value either generated with a {@link Encryption}.
     * @return <code>true</code> if encrypted value was generated by a call to {@link #migrate(String)}
     * @see #migrate(String)
     */
    public boolean isMigrated(String encrypted) {
        try {
            return migratedValues.contains(digester.digest(encrypted));
        } catch (Exception e) {
            LOGGER.error("Unable to compute digest.", e);
            return false;
        }
    }

    /**
     * <p>
     * This method uses <code>source</code> encryption to decrypt value and immediately encrypts value using
     * <code>target</code>.
     * </p>
     * <p>
     * It also remembers the values that were generated (keeps only a digest of the migration result, not the result
     * itself).
     * </p>
     * 
     * @param originalEncrypted A value encrypted with <code>source</code>
     * @return An encrypted value with <code>target</code> to represent same decrypted value as original's.
     * @throws Exception In case of any unexpected exception during {@link Encryption#encrypt(String)} or
     * {@link Encryption#decrypt(String)}.
     */
    public String migrate(String originalEncrypted) throws Exception {
        final String migrated = target.encrypt(source.decrypt(originalEncrypted));
        migratedValues.add(digester.digest(migrated));
        return migrated;
    }

    public Encryption getTarget() {
        return target;
    }
}
