package org.talend.daikon.security;

import org.junit.Test;
import org.talend.daikon.crypto.KeySources;

import static org.junit.Assert.*;

public class CryptoHelperTest {

    @Test
    public void testCryptoHelperLegacyPassphrase() {
        assertEquals("99ZwBDt1L9yMX2ApJx fnv94o99OeHbCGuIHTy22 V9O6cZ2i374fVjdV76VX9g49DG1r3n90hT5c1", CryptoHelper.PASSPHRASE);
    }

    @Test
    public void testCryptoHelperLegacySalt() throws Exception {
        byte[] expected = new byte[] { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3,
                (byte) 0x03 };
        assertArrayEquals(expected, KeySources.file("system.encryption.salt").getKey());
    }
}