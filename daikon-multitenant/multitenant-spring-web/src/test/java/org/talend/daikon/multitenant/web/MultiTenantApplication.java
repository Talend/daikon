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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.talend.daikon.multitenant.provider.DefaultTenantProvider;
import org.talend.daikon.multitenant.provider.TenantProvider;

import java.util.concurrent.Callable;

@SpringBootApplication
@Import({ MultiTenantApplication.TenancyConfiguration.class })
public class MultiTenantApplication {

    public static final String TENANT_HTTP_HEADER = "X-Test-TenantId";

    public static final String MESSAGE = "Hello, World!";

    public static void main(String[] args) { //NOSONAR
        SpringApplication.run(MultiTenantApplication.class, args); //NOSONAR
    }

    @Configuration
    public static class TenancyConfiguration {

        @Bean
        public TenantProvider tenantProvider() {
            return new DefaultTenantProvider();
        }

        @Bean
        public TenantIdentificationStrategy tenantIdentificationStrategy() {
            HeaderTenantIdentificationStrategy strategy = new HeaderTenantIdentificationStrategy();
            strategy.setHeaderName(TENANT_HTTP_HEADER);
            return strategy;
        }
    }

    @RestController
    public static class TestRestController {

        @Autowired
        private final SampleRequestHandler sampleRequestHandler;

        public TestRestController(SampleRequestHandler handler) {
            this.sampleRequestHandler = handler;
        }

        @RequestMapping(path = "/sync", method = RequestMethod.GET)
        public String sayHelloSync() throws Exception {
            return sayHello();
        }

        @RequestMapping(path = "/async", method = RequestMethod.GET)
        public Callable<String> sayHelloAsync() throws Exception {
            return this::sayHello;
        }

        private String sayHello() {
            this.sampleRequestHandler.onSampleRequestCalled();
            return MESSAGE;
        }
    }

    public interface SampleRequestHandler {

        void onSampleRequestCalled();

    }

}
