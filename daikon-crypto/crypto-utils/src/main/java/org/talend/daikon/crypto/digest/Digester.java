package org.talend.daikon.crypto.digest;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.crypto.EncodingUtils;
import org.talend.daikon.crypto.KeySource;
import org.talend.daikon.crypto.KeySources;

import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static java.util.Arrays.stream;

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

    private static final int[] FORBIDDEN_DELIMITERS = { '/', '=' };

    /**
     * Creates a Digester using a 16 byte length random key (see {@link KeySources#random(int)}) and "-" as delimiter
     * for separating salt and digested value.
     * 
     * @param digestSource The {@link DigestSource} implementation to digest values.
     * @see DigestSources
     */
    public Digester(DigestSource digestSource) {
        this(KeySources.random(16), '-', digestSource);
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
        if (Character.isLetterOrDigit(delimiter) || stream(FORBIDDEN_DELIMITERS).anyMatch(c -> c == delimiter)) {
            final String forbiddenDelimiters = stream(FORBIDDEN_DELIMITERS) //
                    .mapToObj(i -> valueOf((char) i)) //
                    .collect(Collectors.joining());
            throw new IllegalArgumentException("Delimiter cannot be number, letter or '" + forbiddenDelimiters + "'.");
        }
        this.keySource = keySource;
        this.delimiter = delimiter;
        this.digestSource = digestSource;
    }

    private static byte[] decode(byte[] bytes) {
        return EncodingUtils.BASE64_DECODER.apply(bytes);
    }

    private static String encode(byte[] salt) {
        return EncodingUtils.BASE64_ENCODER.apply(salt);
    }

    private String saltValue(String value, byte[] salt) {
        if (delimiter == NO_DELIMITER) {
            return digestSource.digest(value, salt);
        } else {
            return encode(salt) + delimiter + digestSource.digest(value, salt);
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
        return saltValue(value, keySource.getKey());
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
            return (digestSource.digest(value, new byte[0])).equals(digest);
        }
        try {
            if (digest.indexOf(delimiter) < 0) {
                // Digest is expected to have delimiter in it: don't return what the expected delimiter is in the
                // exception message to prevent giving information about expected delimiter to caller.
                throw new IllegalArgumentException("No delimiter found in digest.");
            }
            final String saltBase64 = StringUtils.substringBefore(digest, valueOf(delimiter));
            final byte[] salt = decode(saltBase64.getBytes(EncodingUtils.ENCODING));
            return (saltBase64 + delimiter + digestSource.digest(value, salt)).equals(digest);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
