package org.talend.daikon.crypto.digest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
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
        final String directDigest = DigestSources.sha256().digest(value);

        // then
        assertArrayEquals(digest.getBytes(), directDigest.getBytes());
    }

    @Test
    public void shouldDigestValue() throws Exception {
        // given
        final String value = "myPassword";

        // when
        Digester digester = new Digester(KeySources.random(16), DigestSources.sha256());
        final String digest = digester.digest(value);

        // then
        assertTrue(digest.contains("-"));
    }

    @Test
    public void shouldValidateValue() throws Exception {
        // given
        String value = "myPassword";

        // when
        Digester digester = new Digester(KeySources.random(16), DigestSources.sha256());
        final String digest = digester.digest(value);

        // then
        assertTrue(digester.validate("myPassword", digest));
        assertFalse(digester.validate("MyPassword", digest));
    }

    @Test
    public void shouldValidateValueWithDifferentDigester() throws Exception {
        // given
        String value = "myPassword";

        // when
        final byte[] salt = KeySources.random(16).getKey();
        Digester digester1 = new Digester(KeySources.random(16), DigestSources.pbkDf2(salt));
        Digester digester2 = new Digester(KeySources.random(16), DigestSources.pbkDf2(salt));
        final String digest1 = digester1.digest(value);
        final String digest2 = digester2.digest(value);

        // then
        assertTrue(digester2.validate("myPassword", digest1));
        assertFalse(digester2.validate("MyPassword", digest1));
        assertNotEquals(digest1, digest2);
    }
}