package org.talend.daikon.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;

import org.talend.daikon.services.TestServiceImpl;

public class ClientServiceTestUtils {
    public static void assertServiceClientClass(Object client, Access access) {
        switch (access) {
            case LOCAL:
                assertEquals(TestServiceImpl.class, client.getClass());
                break;
            case REMOTE:
                assertTrue(Proxy.isProxyClass(client.getClass()));
                break;
            default:
                throw new IllegalArgumentException("Not supported in test: " + access);
        }
    }
}
