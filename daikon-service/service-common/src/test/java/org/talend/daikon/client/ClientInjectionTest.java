package org.talend.daikon.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.talend.daikon.ServiceBaseTests;
import org.talend.daikon.services.TestService;
import org.talend.daikon.annotation.Client;

public class ClientInjectionTest extends ServiceBaseTests {

    @Client(access = Access.LOCAL)
    private TestService localService;

    @Client(access = Access.REMOTE)
    private TestService remoteService;

    @Client
    private TestService defaultService;

    @Test
    public void testLocalService() throws Exception {
        assertInjectedService(localService);
    }

    @Test
    public void testRemoteService() throws Exception {
        assertInjectedService(remoteService);
    }

    @Test
    public void testDefaultService() throws Exception {
        assertInjectedService(defaultService);
    }

    private void assertInjectedService(TestService service) {
        assertNotNull(service);
        assertEquals(TestService.I_SAY_HI, service.sayHi());
    }

}
