package org.talend.daikon.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class CryptoHelperTest {

    @Test
    public void testCryptoHelperLegacyPassphrase() {
        assertEquals("99ZwBDt1L9yMX2ApJx fnv94o99OeHbCGuIHTy22 V9O6cZ2i374fVjdV76VX9g49DG1r3n90hT5c1", CryptoHelper.PASSPHRASE);
    }
}