package org.talend.daikon.crypto.digest;

import org.junit.Test;
import org.talend.daikon.crypto.KeySources;

import static org.junit.Assert.*;

public class DigestSourcesTest {

    @Test
    public void shouldUseSaltInSha256() throws Exception {
        // Given
        final String data = "value to digest";

        // When
        final String digest = DigestSources.sha256().digest(data, KeySources.fixedKey("abcd1234").getKey());

        // Then
        assertEquals("6c7nGrky/ehjM40Ivk3p3+OeoEm9r7NCzmWexUULaa7IG36TBiw+3zjgPRtgPlyVMhJAo7BDDZ6176QavT7/Eg==", digest);
    }

    @Test
    public void shouldNotUseSaltInSha256() {
        // Given
        final String data = "value to digest";

        // When
        final String digest1 = DigestSources.sha256().digest(data, null);
        final String digest2 = DigestSources.sha256().digest(data, new byte[0]);

        // Then
        assertEquals(digest1, digest2);
        assertEquals("yBt+kwYsPt844D0bYD5clTISQKOwQw2ete+kGr0+/xI=", digest1);
    }

    @Test
    public void cannotUsePbkDf2WithoutSalt() {
        // When
        try {
            DigestSources.pbkDf2().digest("value", null);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            DigestSources.pbkDf2().digest("value", new byte[0]);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void shouldUseSaltInPbkDf2() throws Exception {
        // Given
        final String data = "value to digest";

        // When
        final String digest = DigestSources.pbkDf2().digest(data, KeySources.fixedKey("abcd1234").getKey());

        // Then
        assertEquals("jAPuCXK/9TVMcpzqkS32je+dTE6FnTVAVH1d6FnMoy4=", digest);
    }
}