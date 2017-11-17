// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.async;

import com.jayway.restassured.RestAssured;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.logging.event.field.MdcKeys;
import org.talend.daikon.multitenant.context.DefaultTenancyContext;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.provider.DefaultTenant;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Import(AsyncContextPropagationTest.MessagePublicationHandlerConfiguration.class)
@SpringBootTest(classes = { MultiTenantApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AsyncContextPropagationTest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private MessagePublicationHandlerConfiguration handlerConfiguration;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        handlerConfiguration.verifier = () -> {
        };
    }

    @Autowired
    private MultiTenantApplication.MessagingService messagingService;

    @Test
    public void testTenantPropagation() throws Exception {
        String content = "test";
        String tenantId = "tenantId";

        handlerConfiguration.verifier = () -> {
            assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
        };

        TenancyContext context = new DefaultTenancyContext();
        context.setTenant(new DefaultTenant(tenantId, null));
        TenancyContextHolder.setContext(context);

        messagingService.sendMessage(content);
        MultiTenantApplication.Message message = messagingService.receiveMessage();
        assertEquals(content, message.getContent());
        assertEquals(tenantId, message.getTenantId());
        assertEquals(null, message.getPriority());
        assertEquals(null, message.getUserId());
    }

    @Test
    public void testRequestAttributesPropagation() throws Exception {
        String content = "test";
        String tenantId = "tenantId";
        String priority = "urgent";
        given().content(content).post("/public/{tenant}?priority={priority}", tenantId, priority).then().statusCode(200);

        MultiTenantApplication.Message message = messagingService.receiveMessage();
        assertEquals(content, message.getContent());
        assertEquals(tenantId, message.getTenantId());
        assertEquals(priority, message.getPriority());
        assertEquals(null, message.getUserId());
    }

    @Test
    public void testSecurityPropagation() throws Exception {
        String content = "test";
        String tenantId = "tenantId";
        String user = "user";

        handlerConfiguration.verifier = () -> {
            assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
            assertEquals(user, MDC.get(MdcKeys.USER_ID));
        };

        given().content(content).auth().basic(user, "password").post("/private/{tenant}", tenantId).then().statusCode(200);

        MultiTenantApplication.Message message = messagingService.receiveMessage();
        assertEquals(content, message.getContent());
        assertEquals(tenantId, message.getTenantId());
        assertEquals(null, message.getPriority());
        assertEquals(user, message.getUserId());
    }

    @Configuration
    public static class MessagePublicationHandlerConfiguration {

        public Runnable verifier = () -> {
        };

        @Bean
        public MultiTenantApplication.MessagePublicationHandler messagePublicationHandler() {

            return () -> verifier.run();

        }
    }

}
