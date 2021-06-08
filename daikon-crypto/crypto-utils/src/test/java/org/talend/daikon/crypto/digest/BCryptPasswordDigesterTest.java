package org.talend.daikon.crypto.digest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BCryptPasswordDigesterTest {

    @Test
    public void shouldValidateDigest() throws Exception {
        PasswordDigester digester = new BCryptPasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digester.validate("tiger", digest));
        assertFalse(digester.validate("password", digest));
    }

    @Test
    public void shouldHave10AsDefaultStrength() throws Exception {
        int strength = 10;
        PasswordDigester digester = new BCryptPasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digester.validate("tiger", digest));
        assertEquals(String.valueOf(strength), digest.substring(4, 6));
    }

    @Test
    public void shouldBeAbleToConfigureStrength() throws Exception {
        int strength = 15;
        PasswordDigester digester = new BCryptPasswordDigester(strength);

        final String digest = digester.digest("tiger");

        assertTrue(digester.validate("tiger", digest));
        assertEquals(String.valueOf(strength), digest.substring(4, 6));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNegativeStrength() throws Exception {
        new BCryptPasswordDigester(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowPasswordsGreaterThan72Bytes() throws Exception {
        PasswordDigester digester = new BCryptPasswordDigester();
        digester.digest(StringUtils.repeat("x", 100));
    }

    @Test
    public void shouldHaveDifferentSalts() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester1 = new BCryptPasswordDigester();
        PasswordDigester digester2 = new BCryptPasswordDigester();
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        // BCrypt has a 22 character salt
        final String salt1 = digest1.substring(7, 7 + 22);
        final String salt2 = digest2.substring(7, 7 + 22);
        assertFalse(salt1.equals(salt2));
    }

    @Test
    public void shouldHaveDifferentHashes() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester1 = new BCryptPasswordDigester();
        PasswordDigester digester2 = new BCryptPasswordDigester();
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        // BCrypt has a 22 character salt, and a 31 character hash
        final String hash1 = digest1.substring(7 + 22);
        final String hash2 = digest2.substring(7 + 22);
        assertFalse(hash1.equals(hash2));
    }

    @Test
    public void shouldFailValidateWithSaltTampering() throws Exception {
        // given
        final String value = "myPassword";
        final char delimiter = '-';

        // when
        PasswordDigester digester = new BCryptPasswordDigester();
        final String digest = digester.digest(value);

        // Modify the 'salt' part
        String salt = digest.substring(7, 7 + 22);
        char[] saltChar = salt.toCharArray();
        saltChar[0] = Character.reverseBytes(saltChar[0]);
        String tamperedSalt = new String(saltChar);
        final String tampered = digest.replace(salt, tamperedSalt);

        // then
        assertTrue(digester.validate(value, digest));
        assertFalse(digester.validate(value, tampered));
    }

    @Test
    public void shouldFailValidateWithDigestTampering() throws Exception {
        // given
        final String value = "myPassword";
        final char delimiter = '-';

        // when
        PasswordDigester digester = new BCryptPasswordDigester();
        final String digest = digester.digest(value);

        // Modify the 'digest' part
        String digestPart = digest.substring(7 + 22);
        char[] digestChar = digestPart.toCharArray();
        digestChar[0] = Character.reverseBytes(digestChar[0]);
        String tamperedDigest = new String(digestChar);
        final String tampered = digest.replace(digestPart, tamperedDigest);

        // then
        assertTrue(digester.validate(value, digest));
        assertFalse(digester.validate(value, tampered));
    }

    @Test
    public void checkDefaultAlgorithmIs2a() throws Exception {
        PasswordDigester digester = new BCryptPasswordDigester();

        final String digest = digester.digest("tiger");

        assertEquals(digest.substring(1, 3), "2a");
    }

    @Test
    public void shouldBeAbleToConfigureAlgorithm() throws Exception {
        PasswordDigester digester = new BCryptPasswordDigester(10, "$2y");

        final String digest = digester.digest("tiger");

        assertEquals(digest.substring(1, 3), "2y");
    }

    @Test
    public void shouldValidateDigestWithDifferentAlgorithm() throws Exception {
        PasswordDigester digester = new BCryptPasswordDigester(19, "$2b");

        final String digest = digester.digest("tiger");

        assertTrue(digester.validate("tiger", digest));
        assertFalse(digester.validate("password", digest));
    }
}