package org.talend.daikon.crypto.digest;

import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.talend.daikon.crypto.EncodingUtils;
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
        assertEquals("aUh5A+XLT97dzHBZd5cRV7raB2ZiLNDvjwVlGzdeRlg=", digest);
    }

    @Test
    public void digestShouldBe32Bits() throws Exception {
        // Given
        final String data = "value to digest";

        // When
        final String digest = DigestSources.sha256().digest(data, KeySources.fixedKey("abcd1234").getKey());

        // Then
        byte[] decodedDigest = EncodingUtils.BASE64_DECODER.apply(digest.getBytes(EncodingUtils.ENCODING));
        assertEquals(32, decodedDigest.length);
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
    public void saltShouldBeUsedWithPasswordDigest() throws Exception {
        // Given
        final String data = "value to digest";

        // When
        final String digest = DigestSources.sha256().digest(data, KeySources.fixedKey("abcd1234").getKey());

        // Then
        byte[] decodedDigest = EncodingUtils.BASE64_DECODER.apply(digest.getBytes(EncodingUtils.ENCODING));
        byte[] passwordPart = new byte[decodedDigest.length / 2];
        System.arraycopy(decodedDigest, decodedDigest.length / 2, passwordPart, 0, decodedDigest.length / 2);
        assertFalse(Arrays.equals(passwordPart, DigestUtils.sha256(data)));
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