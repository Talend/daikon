package org.talend.daikon.token;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.security.CryptoHelper;

public class TokenGeneratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testArguments() throws Exception {
        TokenGenerator.generateMachineToken(null);
    }

    @Test
    public void testTokenGeneration() throws Exception {
        // When
        final CryptoHelper cryptoHelper = new CryptoHelper("passphrase");
        final String token = TokenGenerator.generateMachineToken(cryptoHelper);

        // Then
        Assert.assertNotNull(token);
        final String decryptedToken = cryptoHelper.decrypt(token);
        final String hostName = InetAddress.getLocalHost().getHostName();
        final String osPart = "-" + System.getProperty("os.name") + System.getProperty("os.version");
        final String hostNamePart = "-" + hostName;
        // Contains host name
        assertTrue(decryptedToken.contains(hostNamePart));
        // Contains OS information
        assertTrue(decryptedToken.contains(osPart));
        // Remaining has length of one or more MAC addresses.
        assertTrue((decryptedToken.length() - hostNamePart.length() - osPart.length()) % 12 == 0);
    }
}
