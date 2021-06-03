package org.talend.daikon.crypto.digest;

import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.crypto.EncodingUtils;
import org.talend.daikon.crypto.KeySource;
import org.talend.daikon.crypto.KeySources;

import static java.lang.String.valueOf;
import static java.util.Arrays.stream;

/**
 * An implementation of PasswordDigester that uses PBKDF2 with HMAC-SHA256.
 * By default it generates a 16 byte salt, although this can be configured by passing a KeySource object.
 * It base-64 encodes the salt, followed by the delimiter ('-' by default), and then the base-64 encoded digest
 * using PBKDF2.
 */
public class PBKDF2PasswordDigester implements PasswordDigester {

    private final KeySource keySource;

    private final int keyLength;

    private final int iterations;

    private final char delimiter;

    private static final int[] FORBIDDEN_DELIMITERS = { '/', '=' };

    private static final int DEFAULT_ITERATIONS = 310000;

    public PBKDF2PasswordDigester() {
        this(KeySources.random(16), 256, 310000);
    }

    public PBKDF2PasswordDigester(int keyLength, int iterations) {
        this(KeySources.random(16), keyLength, iterations);
    }

    public PBKDF2PasswordDigester(KeySource keySource, int keyLength, int iterations) {
        this(keySource, keyLength, iterations, '-');
    }

    public PBKDF2PasswordDigester(KeySource keySource, int keyLength, int iterations, char delimiter) {
        if (Character.isLetterOrDigit(delimiter) || stream(FORBIDDEN_DELIMITERS).anyMatch(c -> c == delimiter)) {
            final String forbiddenDelimiters = stream(FORBIDDEN_DELIMITERS) //
                    .mapToObj(i -> valueOf((char) i)) //
                    .collect(Collectors.joining());
            throw new IllegalArgumentException("Delimiter cannot be number, letter or '" + forbiddenDelimiters + "'.");
        }

        this.keySource = keySource;
        this.keyLength = keyLength;
        this.iterations = iterations;
        this.delimiter = delimiter;
    }

    public String digest(String value) throws Exception {
        return digest(keySource.getKey(), value);
    }

    private String digest(byte[] salt, String value) {
        KeySource keySource = KeySources.pbkDf2(value, salt, keyLength, iterations);
        try {
            return encode(salt) + delimiter + EncodingUtils.BASE64_ENCODER.apply(keySource.getKey());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to digest value.", e);
        }
    }

    private static String encode(byte[] salt) {
        return EncodingUtils.BASE64_ENCODER.apply(salt);
    }

    private static byte[] decode(byte[] bytes) {
        return EncodingUtils.BASE64_DECODER.apply(bytes);
    }

    public boolean validate(String value, String digest) {
        try {
            if (digest.indexOf(delimiter) < 0) {
                // Digest is expected to have delimiter in it: don't return what the expected delimiter is in the
                // exception message to prevent giving information about expected delimiter to caller.
                throw new IllegalArgumentException("No delimiter found in digest.");
            }
            final String saltBase64 = StringUtils.substringBefore(digest, valueOf(delimiter));
            final byte[] salt = decode(saltBase64.getBytes(EncodingUtils.ENCODING));
            return digest(salt, value).equals(digest);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
