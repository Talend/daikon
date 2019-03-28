package org.talend.daikon.token;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to generate tokens.
 */
public class TokenGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenGenerator.class);

    private TokenGenerator() {
    }

    /**
     * Compute a unique token for the machine that runs this code. Information includes:
     * <ul>
     * <li>MAC Address of the localhost interface (as returned by {@link InetAddress#getLocalHost()}).</li>
     * <li>OS Name and version</li>
     * <li>Local host name</li>
     * </ul>
     * Should any of the 3 steps above fails, code falls back for a random UUID.
     *
     * @param cryptoFunction A non-null {@link Function function} used to encrypt generated machine token.
     * @return A non-empty, non-null and encrypted token computed based on information of the running machine.
     * @see InetAddress#getLocalHost()
     * @see UUID#randomUUID()
     */
    public static String generateMachineToken(Function<String, String> cryptoFunction) {
        if (cryptoFunction == null) {
            throw new IllegalArgumentException("Crypto function cannot be null."); //$NON-NLS-1$
        }
        final StringBuilder sb = new StringBuilder();
        try {
            final InetAddress loopBackAddress = InetAddress.getLocalHost();
            // Add all machine's MAC addresses ...
            final Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                final NetworkInterface network = networks.nextElement();
                final byte[] mac = network.getHardwareAddress();
                if (mac != null) { // null mac address can be returned by getHardwareAddress().
                    StringBuilder macAddress = new StringBuilder();
                    for (byte macByte : mac) {
                        macAddress.append(String.format("%02X", macByte)); //$NON-NLS-1$
                    }
                    // Skip empty mac address (if any)
                    if (!"0000000000E0".equals(macAddress.toString())) { //$NON-NLS-1$
                        sb.append(macAddress.toString());
                    }
                }
            }
            sb.append('-');
            // ... then OS ...
            sb.append(System.getProperty("os.name")); //$NON-NLS-1$
            sb.append(System.getProperty("os.version")); //$NON-NLS-1$
            // ... host name ...
            sb.append('-').append(loopBackAddress.getHostName());
        } catch (SocketException | UnknownHostException e) {
            LOGGER.debug("Unable to get local MAC address, fall back to UUID.", e); //$NON-NLS-1$
            sb.append(UUID.randomUUID().toString());
        }

        // ... and encode the result with a static pass phrase.
        final String machineId = cryptoFunction.apply(sb.toString());
        LOGGER.debug("Generated machine token: {}", machineId);
        return machineId;
    }

}
