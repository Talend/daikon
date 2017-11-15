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
package org.talend.daikon.logging.spring;

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

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = { LoggingApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(LoggingContextTest.SampleRequestHandlerConfiguration.class)
@RunWith(SpringRunner.class)
public class LoggingContextTest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private SampleRequestHandlerConfiguration sampleRequestHandler;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        sampleRequestHandler.verifier = () -> {
        };
    }

    @Test
    public void testAuthenticationAndMultitenant() {
        final String tenantId = "myTenantId";
        final String userId = LoggingApplication.USER_ID;

        sampleRequestHandler.verifier = () -> {
            assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
            assertEquals(userId, MDC.get(MdcKeys.USER_ID));
        };
        String result = given().auth().basic(userId, LoggingApplication.PASSWORD)
                .header(LoggingApplication.TENANT_HTTP_HEADER, tenantId).get("/").then().statusCode(200).extract().asString();
        assertEquals(LoggingApplication.MESSAGE, result);
    }

    @Test
    public void testAuthenticationNoTenant() {
        final String userId = LoggingApplication.USER_ID;
        sampleRequestHandler.verifier = () -> {
            assertEquals(null, MDC.get(MdcKeys.ACCOUNT_ID));
            assertEquals(userId, MDC.get(MdcKeys.USER_ID));
        };
        String result = given().auth().basic(userId, LoggingApplication.PASSWORD).get("/").then().statusCode(200).extract()
                .asString();
        assertEquals(LoggingApplication.MESSAGE, result);
    }

    @Test
    public void testNoAuthenticationNoTenant() {
        sampleRequestHandler.verifier = () -> {
            assertEquals(null, MDC.get(MdcKeys.ACCOUNT_ID));
            assertEquals(null, MDC.get(MdcKeys.USER_ID));
        };
        String result = given().get("/public").then().statusCode(200).extract().asString();
        assertEquals(LoggingApplication.MESSAGE, result);
    }

    @Configuration
    public static class SampleRequestHandlerConfiguration {

        public Runnable verifier = () -> {
        };

        @Bean
        public LoggingApplication.SampleRequestHandler sampleRequestHandler() {

            return () -> verifier.run();

        }
    }

}
