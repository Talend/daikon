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
package org.talend.daikon.multitenant.web;

import com.jayway.restassured.RestAssured;
import org.apache.log4j.MDC;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.talend.daikon.logging.event.field.MdcKeys;
import org.talend.daikon.multitenant.context.TenancyContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.talend.daikon.multitenant.web.MultiTenantApplication.MESSAGE;

@RunWith(SpringRunner.class)
@Import(MultiTenantApplicationTest.SampleRequestHandlerConfiguration.class)
@SpringBootTest(classes = { MultiTenantApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultiTenantApplicationTest {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private SampleRequestHandlerConfiguration handlerConfiguration;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        handlerConfiguration.verifier = () -> {
        };
    }

    @Test
    public void testSyncWithoutTenant() {
        handlerConfiguration.verifier = () -> {
            Assert.assertFalse(TenancyContextHolder.getContext().getOptionalTenant().isPresent());
            Assert.assertNull(MDC.get(MdcKeys.ACCOUNT_ID));
        };
        given().get("/sync").then().statusCode(200).body(Matchers.equalTo(MESSAGE));
    }

    @Test
    public void testSyncWithTenantHeader() {
        String tenantId = "MyTestTenantId";
        handlerConfiguration.verifier = () -> {
            Assert.assertEquals(tenantId, TenancyContextHolder.getContext().getTenant().getIdentity());
            Assert.assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
        };
        given().header(MultiTenantApplication.TENANT_HTTP_HEADER, tenantId).get("/sync").then().statusCode(200)
                .body(Matchers.equalTo(MESSAGE));
    }

    @Test
    public void testAsyncWithoutTenant() {
        handlerConfiguration.verifier = () -> {
            Assert.assertFalse(TenancyContextHolder.getContext().getOptionalTenant().isPresent());
            Assert.assertNull(MDC.get(MdcKeys.ACCOUNT_ID));
        };
        given().get("/async").then().statusCode(200).body(Matchers.equalTo(MESSAGE));
    }

    @Test
    public void testAsyncWithTenantHeader() {
        String tenantId = "MyAsyncTestTenantId";
        handlerConfiguration.verifier = () -> {
            Assert.assertEquals(tenantId, TenancyContextHolder.getContext().getTenant().getIdentity());
            Assert.assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
        };
        given().header(MultiTenantApplication.TENANT_HTTP_HEADER, tenantId).get("/async").then().statusCode(200)
                .body(Matchers.equalTo(MESSAGE));
    }

    @Test
    public void testSyncWithTenantAndError() {
        String errorMessage = "Expected error message";
        String tenantId = "MyTestTenantId";
        handlerConfiguration.verifier = () -> {
            Assert.assertEquals(tenantId, TenancyContextHolder.getContext().getTenant().getIdentity());
            Assert.assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
            throw new RuntimeException(errorMessage);
        };
        given().header(MultiTenantApplication.TENANT_HTTP_HEADER, tenantId).get("/sync").then().statusCode(500);
    }

    @Test
    public void testAsyncWithTenantAndError() {
        String errorMessage = "Expected error message";
        String tenantId = "MyTestTenantId";
        handlerConfiguration.verifier = () -> {
            Assert.assertEquals(tenantId, TenancyContextHolder.getContext().getTenant().getIdentity());
            Assert.assertEquals(tenantId, MDC.get(MdcKeys.ACCOUNT_ID));
            throw new RuntimeException(errorMessage);
        };
        given().header(MultiTenantApplication.TENANT_HTTP_HEADER, tenantId).get("/async").then().statusCode(500);
    }

    @Configuration
    public static class SampleRequestHandlerConfiguration {

        public Runnable verifier = () -> {
        };

        @Bean
        public MultiTenantApplication.SampleRequestHandler sampleRequestHandler() {
            return () -> verifier.run();
        }
    }

}
