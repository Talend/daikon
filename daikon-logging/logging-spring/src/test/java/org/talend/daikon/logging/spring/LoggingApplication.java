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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.daikon.multitenant.provider.DefaultTenantProvider;
import org.talend.daikon.multitenant.provider.TenantProvider;
import org.talend.daikon.multitenant.web.HeaderTenantIdentificationStrategy;
import org.talend.daikon.multitenant.web.TenantIdentificationStrategy;

@SpringBootApplication
public class LoggingApplication {

    public static final String TENANT_HTTP_HEADER = "X-Test-TenantId";

    public static final String MESSAGE = "Hello, World!";

    public static final String USER_ID = "user";

    public static final String PASSWORD = "password";

    public static void main(String[] args) { //NOSONAR
        SpringApplication.run(LoggingApplication.class, args); //NOSONAR
    }

    @Configuration
    @EnableWebSecurity
    public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(USER_ID).password(PASSWORD).authorities("ROLE_USER");
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/public/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
        }
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
    public static class SampleEndpoint {

        private final SampleRequestHandler sampleRequestHandler;

        public SampleEndpoint(SampleRequestHandler sampleRequestHandler) {
            this.sampleRequestHandler = sampleRequestHandler;
        }

        @RequestMapping
        public String sampleGet() {
            this.sampleRequestHandler.onSampleRequestCalled();
            return MESSAGE;
        }

        @RequestMapping(path = "/public")
        public String publicSampleGet() {
            this.sampleRequestHandler.onSampleRequestCalled();
            return MESSAGE;
        }
    }

    public interface SampleRequestHandler {

        void onSampleRequestCalled();

    }

}
