package org.talend.daikon.logging.kafka;

import java.io.IOException;
import java.net.ServerSocket;

public final class FreeRandomPort {

    public static int generateRandomPort() throws Exception {
        ServerSocket s = null;
        try {
            // ServerSocket(0) results in availability of a free random port
            s = new ServerSocket(0);
            return s.getLocalPort();
        } catch (Exception e) {
            throw new Exception();
        } finally {
            assert s != null;
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
