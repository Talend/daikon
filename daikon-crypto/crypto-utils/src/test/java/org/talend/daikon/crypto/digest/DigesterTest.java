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
        Digester digester = new Digester(KeySources.random(8), "-", DigestSources.sha256());
        final String digest = digester.digest(value);

        // then
        assertTrue(digest.contains("-"));
    }

    @Test
    public void shouldValidateValue() throws Exception {
        // given
        String value = "myPassword";

        // when
        Digester digester = new Digester(KeySources.random(8), "-", DigestSources.sha256());
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
        Digester digester1 = new Digester(KeySources.random(8), "-", DigestSources.sha256());
        Digester digester2 = new Digester(KeySources.random(8), "-", DigestSources.sha256());
        final String digest = digester1.digest(value);

        // then
        assertTrue(digester2.validate("myPassword", digest));
        assertFalse(digester2.validate("MyPassword", digest));
    }
}