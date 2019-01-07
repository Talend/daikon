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

import java.util.concurrent.Callable;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class LoggingApplication {

    public static final String MESSAGE = "Hello, World!";

    public static final String USER_ID = "user";

    public static final String PASSWORD = "password";

    public static void main(String[] args) { // NOSONAR
        SpringApplication.run(LoggingApplication.class, args); // NOSONAR
    }

    @Configuration
    @EnableWebSecurity
    public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            String password = passwordEncoder().encode(PASSWORD);
            auth.inMemoryAuthentication() //
                    .passwordEncoder(passwordEncoder()) //
                    .withUser(USER_ID).password(password).authorities("ROLE_USER");
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

        @RequestMapping(path = "/async")
        public Callable<String> asyncGet() {
            return () -> {
                this.sampleRequestHandler.onSampleRequestCalled();
                return MESSAGE;
            };
        }

        @RequestMapping(path = "/public/async")
        public Callable<String> publicAsyncGet() {
            return () -> {
                this.sampleRequestHandler.onSampleRequestCalled();
                return MESSAGE;
            };
        }
    }

    public interface SampleRequestHandler {

        void onSampleRequestCalled();

    }

}
