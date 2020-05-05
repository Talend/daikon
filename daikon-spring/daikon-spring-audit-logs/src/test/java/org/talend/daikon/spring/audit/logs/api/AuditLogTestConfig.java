package org.talend.daikon.spring.audit.logs.api;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.talend.daikon.spring.audit.logs.service.AuditLogGeneratorAspect;
import org.talend.daikon.spring.audit.logs.service.AuditLogGeneratorInterceptor;
import org.talend.daikon.spring.audit.logs.service.AuditLogSender;
import org.talend.daikon.spring.audit.logs.service.AuditLogger;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AuditLogTestConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().anyRequest().permitAll().and().exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder().username(AuditLogTestApp.USERNAME).password(RandomStringUtils.random(10)).roles()
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public AuditUserProvider auditUserProvider() {
        return new AuditUserProvider() {

            @Override
            public String getUsername() {
                return Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication)
                        .map(Authentication::getPrincipal).filter(principal -> principal instanceof UserDetails)
                        .map(UserDetails.class::cast).map(UserDetails::getUsername).orElse(null);
            }

            @Override
            public String getUserId() {
                return StringUtils.isBlank(getUsername()) ? null : AuditLogTestApp.USER_ID;
            }

            @Override
            public String getUserEmail() {
                return StringUtils.isBlank(getUsername()) ? null : AuditLogTestApp.USER_EMAIL;
            }

            @Override
            public String getAccountId() {
                return StringUtils.isBlank(getUsername()) ? null : AuditLogTestApp.ACCOUNT_ID;
            }
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AuditLogger auditLogger() {
        return Mockito.spy(AuditLogger.class);
    }

    @Bean
    public AuditLogSender auditLogSender(ObjectMapper objectMapper, AuditUserProvider auditUserProvider,
            AuditLogger auditLogger) {
        return new AuditLogSender(objectMapper, auditUserProvider, auditLogger);
    }

    @Bean
    public AuditLogGeneratorAspect auditLogGeneratorAspect(AuditLogSender auditLogSender) {
        return new AuditLogGeneratorAspect(auditLogSender);
    }

    @Bean
    public AuditLogGeneratorInterceptor auditLogGeneratorInterceptor(AuditLogSender auditLogSender) {
        return new AuditLogGeneratorInterceptor(auditLogSender);
    }
}
