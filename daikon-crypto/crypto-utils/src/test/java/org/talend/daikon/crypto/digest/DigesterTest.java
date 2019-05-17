package org.talend.daikon.crypto.digest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.talend.daikon.crypto.EncodingUtils;
import org.talend.daikon.crypto.KeySources;

public class DigesterTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInvalidDelimiter() {
        new Digester(KeySources.empty(), '1', DigestSources.sha256());
    }

    @Test
    public void shouldDigestValueWithNoSalt() throws Exception {
        // given
        final String value = "myPassword";

        // when
        Digester digester = new Digester(KeySources.empty(), Digester.NO_DELIMITER, DigestSources.sha256());
        final String digest = digester.digest(value);
        final String directDigest = DigestSources.sha256().digest(value, new byte[0]);

        // then
        assertArrayEquals(digest.getBytes(), directDigest.getBytes());
    }

    @Test
    public void shouldDigestValue() throws Exception {
        // given
        final String value = "myPassword";
        final int keyLength = 16;

        // when
        Digester digester = new Digester(KeySources.random(keyLength), DigestSources.sha256());
        final String digest = digester.digest(value);

        // then
        assertTrue(digest.contains("-"));
        final String encodedSalt = StringUtils.substringBefore(digest, "-");
        final byte[] decodedSalt = EncodingUtils.BASE64_DECODER.apply(encodedSalt.getBytes(EncodingUtils.ENCODING));
        assertEquals(keyLength, decodedSalt.length);
    }

    @Test
    public void shouldValidateValue() throws Exception {
        // given
        String value = "myPassword";

        // when
        Digester digester = new Digester(KeySources.random(16), DigestSources.sha256());
        final String digest = digester.digest(value);

        // then
        System.out.println(digest);
        assertTrue(digester.validate("myPassword", digest));
        assertFalse(digester.validate("MyPassword", digest));
    }

    @Test
    public void shouldValidateValueWithDifferentDigester() throws Exception {
        // given
        String value = "myPassword";

        // when
        Digester digester1 = new Digester(KeySources.random(16), DigestSources.pbkDf2());
        Digester digester2 = new Digester(KeySources.random(16), DigestSources.pbkDf2());
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        assertTrue(digester2.validate("myPassword", digest1));
        assertFalse(digester2.validate("MyPassword", digest1));
        assertNotEquals(digest1, digest2);
    }

    @Test
    public void shouldFailValidateWithSaltTampering() throws Exception {
        // given
        final String value = "myPassword";
        final char delimiter = '-';

        // when
        Digester digester = new Digester(KeySources.random(16), delimiter, DigestSources.pbkDf2());
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
        Digester digester = new Digester(KeySources.random(16), delimiter, DigestSources.pbkDf2());
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

    @Test
    public void shouldFailValidateWithSaltRemoval() throws Exception {
        // given
        final String value = "myPassword";
        final char delimiter = '-';

        // when
        Digester digester = new Digester(KeySources.random(16), delimiter, DigestSources.sha256());
        final String digest = digester.digest(value);
        final String tampered = delimiter + StringUtils.substringAfter(digest, String.valueOf(delimiter));

        // then
        assertTrue(digester.validate(value, digest));
        assertFalse(digester.validate(value, tampered));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckDelimiterInDigestOnValidate() {
        // when
        Digester digester = new Digester(KeySources.fixedKey("abcd1234"), '-', DigestSources.sha256());
        digester.validate("myPassword", "digest that do not contain delimiter");
    }
}