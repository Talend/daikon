package org.talend.daikon.crypto.digest;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import org.talend.daikon.crypto.EncodingUtils;
import org.talend.daikon.crypto.KeySource;
import org.talend.daikon.crypto.KeySources;

public class PBKDF2PasswordDigesterTest {

    @Test
    public void shouldValidateDigest() throws Exception {
        PasswordDigester digester = new PBKDF2PasswordDigester();

        final String digest = digester.digest("tiger");

        assertTrue(digester.validate("tiger", digest));
        assertFalse(digester.validate("password", digest));
    }

    @Test
    public void shouldHave16ByteSaltByDefault() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester = new PBKDF2PasswordDigester(256, 310000);
        final String digest = digester.digest(value);

        // then
        final byte[] salt = StringUtils.substringBefore(digest, String.valueOf('-')).getBytes(EncodingUtils.ENCODING);
        assertEquals(16, Base64.decode(salt).length);
    }

    @Test
    public void shouldHave12SaltLengthWhenSpecified() throws Exception {
        // given
        String value = "myPassword";
        KeySource keySource = KeySources.random(12);

        // when
        PasswordDigester digester = new PBKDF2PasswordDigester(keySource, 256, 310000);
        final String digest = digester.digest(value);

        // then
        final byte[] salt = StringUtils.substringBefore(digest, String.valueOf('-')).getBytes(EncodingUtils.ENCODING);
        assertEquals(12, Base64.decode(salt).length);
    }

    @Test
    public void shouldHave256BitDigestLength() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester = new PBKDF2PasswordDigester(256, 310000);
        final String digest = digester.digest(value);

        // then
        final byte[] digestBytes = StringUtils.substringAfter(digest, String.valueOf('-')).getBytes(EncodingUtils.ENCODING);
        assertEquals(32, Base64.decode(digestBytes).length);
    }

    @Test
    public void shouldHaveDifferentSalts() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester1 = new PBKDF2PasswordDigester(256, 310000);
        PasswordDigester digester2 = new PBKDF2PasswordDigester(256, 310000);
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        final byte[] salt1 = StringUtils.substringBefore(digest1, String.valueOf('-')).getBytes(EncodingUtils.ENCODING);
        final byte[] salt2 = StringUtils.substringBefore(digest2, String.valueOf('-')).getBytes(EncodingUtils.ENCODING);
        assertFalse(Arrays.equals(salt1, salt2));
    }

    @Test
    public void shouldHaveDifferentOutputsWithDifferentIterationCount() throws Exception {
        // given
        String value = "myPassword";
        KeySource keySource = KeySources.random(16);

        // when
        PasswordDigester digester1 = new PBKDF2PasswordDigester(keySource, 256, 200000);
        PasswordDigester digester2 = new PBKDF2PasswordDigester(keySource, 256, 310000);
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        assertTrue(digester1.validate("myPassword", digest1));
        assertTrue(digester2.validate("myPassword", digest2));
        assertNotEquals(digest1, digest2);
    }

    @Test
    public void shouldValidateValue() throws Exception {
        // given
        String value = "myPassword";
        KeySource keySource = KeySources.random(16);

        // when
        PasswordDigester digester1 = new PBKDF2PasswordDigester(256, 310000);
        PasswordDigester digester2 = new PBKDF2PasswordDigester(256, 310000);
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        assertTrue(digester2.validate("myPassword", digest1));
        assertFalse(digester2.validate("MyPassword", digest1));
        assertNotEquals(digest1, digest2);
    }

    @Test
    public void shouldValidateValueWithDifferentDelimiter() throws Exception {
        // given
        String value = "myPassword";
        KeySource keySource = KeySources.random(16);

        // when
        PasswordDigester digester1 = new PBKDF2PasswordDigester(keySource, 256, 310000, '_');
        final String digest1 = digester1.digest(value);

        // then
        assertTrue(digest1.contains("_"));
        assertTrue(digester1.validate("myPassword", digest1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithoutDelimiterInDigest() throws Exception {
        // given
        String value = "myPassword";

        // when
        PasswordDigester digester1 = new PBKDF2PasswordDigester(256, 310000);
        final String digest1 = digester1.digest(value);
        String newDigest = digest1.replace('-', '_');

        // then
        digester1.validate(value, newDigest);
    }

    @Test
    public void shouldFailValidateWithSaltTampering() throws Exception {
        // given
        final String value = "myPassword";
        final char delimiter = '-';

        // when
        PasswordDigester digester = new PBKDF2PasswordDigester(256, 310000);
        final String digest = digester.digest(value);

        // Modify the 'salt' part
        final byte[] tamperedSalt = StringUtils.substringBefore(digest, String.valueOf(delimiter))
                .getBytes(EncodingUtils.ENCODING);
        for (int i = 0; i < tamperedSalt.length; i++) {
            tamperedSalt[i]++;
        }
        final String tampered = EncodingUtils.BASE64_ENCODER.apply(tamperedSalt) //
                + delimiter //
                + StringUtils.substringAfter(digest, String.valueOf(delimiter));

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
        PasswordDigester digester = new PBKDF2PasswordDigester(256, 310000);
        final String digest = digester.digest(value);

        // Modify the 'salted digest' part
        final byte[] tamperedDigest = StringUtils.substringAfter(digest, String.valueOf(delimiter))
                .getBytes(EncodingUtils.ENCODING);
        for (int i = 0; i < tamperedDigest.length; i++) {
            tamperedDigest[i]++;
        }
        final String tampered = StringUtils.substringBefore(digest, String.valueOf(delimiter)) //
                + delimiter //
                + EncodingUtils.BASE64_ENCODER.apply(tamperedDigest);

        // then
        assertTrue(digester.validate(value, digest));
        assertFalse(digester.validate(value, tampered));
    }

}