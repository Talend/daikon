package org.talend.daikon.client;

import static org.junit.Assert.assertEquals;

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
        final String sayAuto = clients.of(TestService.class).sayHi();
        assertEquals(TestService.I_SAY_HI, sayAuto);
        assertEquals(sayAuto, sayAuto);
    }

    @Test
    public void testLocalMode() throws Exception {
        final String sayLocal = clients.of(TestService.class, Access.LOCAL).sayHi();
        assertEquals(TestService.I_SAY_HI, sayLocal);
    }

    @Test
    public void testRemoteMode() throws Exception {
        final String sayRemote = clients.of(TestService.class, Access.REMOTE).sayHi();
        assertEquals(TestService.I_SAY_HI, sayRemote);
    }

}
