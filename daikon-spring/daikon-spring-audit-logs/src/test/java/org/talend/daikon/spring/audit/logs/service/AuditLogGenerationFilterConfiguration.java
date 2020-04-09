package org.talend.daikon.spring.audit.logs.service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan("org.talend.daikon.spring.audit.logs.config")
public class AuditLogGenerationFilterConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AuditLogger auditLogger() {
        return Mockito.spy(AuditLogger.class);
    }

    @Bean
    public AuditUserProvider auditUserProvider() {
        return Mockito.mock(AuditUserProvider.class);
    }
}
