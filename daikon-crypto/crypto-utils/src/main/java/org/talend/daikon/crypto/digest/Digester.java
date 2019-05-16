package org.talend.daikon.crypto.digest;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.crypto.EncodingUtils;
import org.talend.daikon.crypto.KeySource;
import org.talend.daikon.crypto.KeySources;

/**
 * This class provides a helper class to:
 * <ul>
 * <li>digest a given string.</li>
 * <li>validate (compare) a plain value with a previously generated digest.</li>
 * </ul>
 */
public class Digester {

    public static final char NO_DELIMITER = '\0';

    private final DigestSource digestSource;

    private final KeySource keySource;

    private final char delimiter;

    /**
     * Creates a Digester using a {@link KeySources#random(int)} and "-" as delimiter for separating salt and digested
     * value.
     * 
     * @param digestSource The {@link DigestSource} implementation to digest values.
     * @see DigestSources
     */
    public Digester(DigestSource digestSource) {
        this(KeySources.empty(), NO_DELIMITER, digestSource);
    }

    /**
     * Creates a Digester using provided {@link KeySource} for salt, "-" as salt/value delimiter, and provided
     * {@link DigestSource}.
     * 
     * @param keySource The {@link KeySource} to add salt to digested values.
     * @param digestSource The {@link DigestSource} implementation to digest values.
     */
    public Digester(KeySource keySource, DigestSource digestSource) {
        this(keySource, '-', digestSource);
    }

    public Digester(KeySource keySource, char delimiter, DigestSource digestSource) {
        if (Character.isLetterOrDigit(delimiter) || delimiter == '=') {
            throw new IllegalArgumentException("Delimiter cannot be number, letter or '='.");
        }
        this.keySource = keySource;
        this.delimiter = delimiter;
        this.digestSource = digestSource;
    }

    private String saltValue(String value, String salt) {
        if (delimiter == NO_DELIMITER) {
            return digestSource.digest(value);
        } else {
            return salt + delimiter + digestSource.digest(salt + value);
        }
    }

    /**
     * Digest a plain text value and returns a digested value.
     *
     * @param value The value to digest.
     * @return A digested value using salt, delimiter and digested value.
     * @throws Exception In any digest issue (depends on the implementation of {@link DigestSource} used).
     */
    public String digest(String value) throws Exception {
        return saltValue(value, EncodingUtils.BASE64_ENCODER.apply(keySource.getKey()));
    }

    /**
     * Allow to compare a plain text <code>value</code> with a digest.
     * 
     * @param value The plain text value.
     * @param digest The digest to compare with.
     * @return <code>true</code> if value matches digest, <code>false</code> otherwise.
     */
    public boolean validate(String value, String digest) {
        if (delimiter == NO_DELIMITER) {
            return (digestSource.digest(value)).equals(digest);
        }
        String salt = StringUtils.substringBefore(digest, String.valueOf(delimiter));
        return (salt + delimiter + digestSource.digest(salt + value)).equals(digest);
    }
}
