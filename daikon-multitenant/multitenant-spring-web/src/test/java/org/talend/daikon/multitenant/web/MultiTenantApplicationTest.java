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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.talend.daikon.multitenant.web.MultiTenantApplication.TestRestController.UNKNOWN_TENANT;
import static org.talend.daikon.multitenant.web.MultiTenantApplication.TestRestController.formatMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MultiTenantApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultiTenantApplicationTest {

    @Value("${local.server.port}")
    public int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
    }

    @Test
    public void testSyncWithoutTenant() {
        String result = given().get("/sync").asString();
        assertEquals(formatMessage(UNKNOWN_TENANT), result);
    }

    @Test
    public void testSyncWithTenantHeader() {
        String tenantId = "MyTestTenantId";
        String result = given().header(MultiTenantApplication.TENANT_HTTP_HEADER, tenantId).get("/sync").asString();
        assertEquals(formatMessage(tenantId), result);
    }

    @Test
    public void testAsyncWithoutTenant() {
        String result = given().get("/async").asString();
        assertEquals(formatMessage(UNKNOWN_TENANT), result);
    }

    @Test
    public void testAsyncWithTenantHeader() {
        String tenantId = "MyTestTenantId";
        String result = given().header(MultiTenantApplication.TENANT_HTTP_HEADER, tenantId).get("/async").asString();
        assertEquals(formatMessage(tenantId), result);
    }

}
