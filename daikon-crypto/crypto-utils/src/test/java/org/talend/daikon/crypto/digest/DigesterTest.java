package org.talend.daikon.crypto.digest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.daikon.crypto.KeySources;

public class DigesterTest {

    @Test
    public void shouldDigestValue() throws Exception {
        // given
        String value = "myPassword";

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
        final String digest = digester1.digest(value);

        // then
        assertTrue(digester2.validate("myPassword", digest));
        assertFalse(digester2.validate("MyPassword", digest));
    }
}