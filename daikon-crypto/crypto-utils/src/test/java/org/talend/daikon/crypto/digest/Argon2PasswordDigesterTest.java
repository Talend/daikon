package org.talend.daikon.crypto.digest;

import java.util.Base64;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Argon2PasswordDigesterTest {

    @Test
    public void shouldValidateDigest() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digester.validate("tiger", digest));
        assertFalse(digester.validate("password", digest));
    }

    @Test
    public void shouldUseArgon2IdAsAlgorithm() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digest.startsWith("$argon2id$"));
    }

    @Test
    public void shouldUseADefaultIterationCountOf3() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digest.contains("t=3"));
    }

    @Test
    public void shouldUseADefaultMemorySizeOf4096() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digest.contains("m=4096"));
    }

    @Test
    public void shouldUseADefaultParallismOf1() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digest.contains("p=1"));
    }

    @Test
    public void shouldUseADefaultSaltLengthOf16() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        String salt = digest.substring(prefix.length(), digest.indexOf("$", prefix.length()));
        assertEquals(16, Base64.getDecoder().decode(salt).length);
    }

    @Test
    public void shouldUseADefaultHashLengthOf32() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester();

        final String digest = digester.digest("tiger");

        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        String hash = digest.substring(digest.indexOf("$", prefix.length()) + 1);
        assertEquals(32, Base64.getDecoder().decode(hash).length);
    }

    @Test
    public void shouldHaveDifferentSalts() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester1 = new Argon2PasswordDigester();
        PasswordDigester digester2 = new Argon2PasswordDigester();
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        final String salt1 = digest1.substring(prefix.length(), digest1.indexOf("$", prefix.length()));
        final String salt2 = digest2.substring(prefix.length(), digest2.indexOf("$", prefix.length()));
        assertFalse(salt1.equals(salt2));
    }

    @Test
    public void shouldHaveDifferentHashes() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester1 = new Argon2PasswordDigester();
        PasswordDigester digester2 = new Argon2PasswordDigester();
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        final String hash1 = digest1.substring(digest1.indexOf("$", prefix.length()) + 1);
        final String hash2 = digest2.substring(digest2.indexOf("$", prefix.length()) + 1);
        assertFalse(hash1.equals(hash2));
    }

    @Test
    public void shouldFailValidateWithSaltTampering() throws Exception {
        // given
        final String value = "myPassword";
        final char delimiter = '-';

        // when
        PasswordDigester digester = new Argon2PasswordDigester();
        final String digest = digester.digest(value);

        // Modify the 'salt' part
        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        final String salt = digest.substring(prefix.length(), digest.indexOf("$", prefix.length()));
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        saltBytes[0]++;
        String tamperedSalt = new String(Base64.getEncoder().encode(saltBytes));
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
        PasswordDigester digester = new Argon2PasswordDigester();
        final String digest = digester.digest(value);

        // Modify the 'digest' part
        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        String digestPart = digest.substring(digest.indexOf("$", prefix.length()) + 1);
        byte[] digestBytes = Base64.getDecoder().decode(digestPart);
        digestBytes[0]++;
        String tamperedDigest = new String(Base64.getEncoder().encode(digestBytes));
        final String tampered = digest.replace(digestPart, tamperedDigest);

        // then
        assertTrue(digester.validate(value, digest));
        assertFalse(digester.validate(value, tampered));
    }

    @Test
    public void shouldBeAbleToConfigureIteration() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester(16, 32, 1, 4096, 5);

        final String digest = digester.digest("tiger");

        assertTrue(digest.contains("t=5"));
    }

    @Test
    public void shouldBeAbleToConfigureParallelism() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester(16, 32, 2, 4096, 3);

        final String digest = digester.digest("tiger");

        assertTrue(digest.contains("p=2"));
    }

    @Test
    public void shouldBeAbleToConfigureMemory() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester(16, 32, 1, 8192, 3);

        final String digest = digester.digest("tiger");

        assertTrue(digest.contains("m=8192"));
    }

    @Test
    public void shouldBeAbleToConfigureSaltLength() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester(18, 32, 1, 4096, 3);

        final String digest = digester.digest("tiger");

        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        String salt = digest.substring(prefix.length(), digest.indexOf("$", prefix.length()));
        assertEquals(18, Base64.getDecoder().decode(salt).length);
    }

    @Test
    public void shouldBeAbleToConfigureHashLength() throws Exception {
        PasswordDigester digester = new Argon2PasswordDigester(16, 34, 1, 4096, 3);

        final String digest = digester.digest("tiger");

        String prefix = "$argon2id$v=19$m=4096,t=3,p=1$";
        String hash = digest.substring(digest.indexOf("$", prefix.length()) + 1);
        assertEquals(34, Base64.getDecoder().decode(hash).length);
    }
}