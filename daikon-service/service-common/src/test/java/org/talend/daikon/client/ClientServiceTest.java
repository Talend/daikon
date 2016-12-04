package org.talend.daikon.client;

import static org.junit.Assert.assertEquals;
import static org.talend.daikon.client.ClientServiceTestUtils.assertServiceClientClass;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.ServiceBaseTests;
import org.talend.daikon.services.TestService;

import java.io.Serializable;

public class ClientServiceTest extends ServiceBaseTests {

    @Autowired
    ClientService clients;

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalServiceClass() throws Exception {
        // of(...) takes only interfaces
        clients.of(Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingService() throws Exception {
        clients.of(Serializable.class);
    }

    @Test
    public void testAutoMode() throws Exception {
        final TestService client = clients.of(TestService.class);
        final String sayAuto = client.sayHi();
        assertServiceClientClass(client, Access.LOCAL);
        assertEquals(TestService.I_SAY_HI, sayAuto);
        assertEquals(sayAuto, sayAuto);
    }

    @Test
    public void testLocalMode() throws Exception {
        final TestService client = clients.of(TestService.class, Access.LOCAL);
        final String sayLocal = client.sayHi();
        assertServiceClientClass(client, Access.LOCAL);
        assertEquals(TestService.I_SAY_HI, sayLocal);
    }

    @Test
    public void testRemoteMode() throws Exception {
        final TestService client = clients.of(TestService.class, Access.REMOTE);
        final String sayRemote = client.sayHi();
        assertServiceClientClass(client, Access.REMOTE);
        assertEquals(TestService.I_SAY_HI, sayRemote);
    }

    @Test
    public void testRemoteModeWithPathVariable() throws Exception {
        final TestService client = clients.of(TestService.class, Access.REMOTE);
        final String sayRemote = client.sayHiWithMyName("myself");
        assertServiceClientClass(client, Access.REMOTE);
        assertEquals("Hi myself", sayRemote);
    }

    @Test
    public void testRemoteModeWithPathVariableOnImplementation() throws Exception {
        final TestService client = clients.of(TestService.class, Access.REMOTE);
        final String sayRemote = client.sayHiWithMyNameInImplementation("myself");
        assertServiceClientClass(client, Access.REMOTE);
        assertEquals("Hi from implementation: myself", sayRemote);
    }

    @Test
    public void testRemoteModeWithPathVariablesAndRequestBody() throws Exception {
        final TestService client = clients.of(TestService.class, Access.REMOTE);
        final String sayRemote = client.sayHiWithMyNameAndValue("myself", "value", "body");
        assertServiceClientClass(client, Access.REMOTE);
        assertEquals("Hi myself value body", sayRemote);
    }
}
