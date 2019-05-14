package org.talend.daikon.crypto.digest;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
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

    private final DigestSource digestSource;

    private final KeySource keySource;

    private final String delimiter;

    /**
     * Creates a Digester using a {@link KeySources#random(int)} and "-" as delimiter for separating salt and digested
     * value.
     * 
     * @param digestSource The {@link DigestSource} implementation to digest values.
     * @see DigestSources
     */
    public Digester(DigestSource digestSource) {
        this(KeySources.random(16), "-", digestSource);
    }

    /**
     * Creates a Digester using provided {@link KeySource} for salt, "-" as salt/value delimiter, and provided
     * {@link DigestSource}.
     * 
     * @param keySource The {@link KeySource} to add salt to digested values.
     * @param digestSource The {@link DigestSource} implementation to digest values.
     */
    public Digester(KeySource keySource, DigestSource digestSource) {
        this(keySource, "-", digestSource);
    }

    public Digester(KeySource keySource, String delimiter, DigestSource digestSource) {
        this.keySource = keySource;
        this.delimiter = delimiter;
        this.digestSource = digestSource;
    }

    private String saltValue(String value, String salt) {
        return salt + delimiter + digestSource.digest(salt + value);
    }

    /**
     * Digest a plain text value and returns a digested value.
     *
     * @param value The value to digest.
     * @return A digested value using salt, delimiter and digested value.
     * @throws Exception In any digest issue (depends on the implementation of {@link DigestSource} used).
     */
    public String digest(String value) throws Exception {
        return saltValue(value, digestSource.digest(new String(keySource.getKey(), StandardCharsets.UTF_8)));
    }

    /**
     * Allow to compare a plain text <code>value</code> with a digest.
     * 
     * @param value The plain text value.
     * @param digest The digest to compare with.
     * @return <code>true</code> if value matches digest, <code>false</code> otherwise.
     */
    public boolean validate(String value, String digest) {
        String salt = StringUtils.substringBefore(digest, delimiter);
        return (salt + delimiter + digestSource.digest(salt + value)).equals(digest);
    }
}
