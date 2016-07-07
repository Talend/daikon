package org.talend.daikon.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.ServiceBaseTests;
import org.talend.daikon.services.GatewayService;
import org.talend.daikon.services.TestService;

public class GatewayServiceTest extends ServiceBaseTests {

    @Autowired
    ClientService clients;

    @Test
    public void gatewayService() throws Exception {
        final String result = clients.of(GatewayService.class).say();
        assertEquals(TestService.I_SAY_HI, result);
    }

    @Test
    public void sayMyName() throws Exception {
        final String result = clients.of(GatewayService.class).sayMyName("World");
        assertEquals("Hi World", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingOperation() throws Exception {
        clients.of(GatewayService.class).missingOperation();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingService() throws Exception {
        clients.of(GatewayService.class).missingService();
    }

    @Test
    public void custom() throws Exception {
        final String response = clients.of(GatewayService.class).custom();
        assertEquals("custom", response);
    }
}
