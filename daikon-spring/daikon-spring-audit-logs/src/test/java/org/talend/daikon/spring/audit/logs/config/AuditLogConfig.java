package org.talend.daikon.spring.audit.logs.config;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.NoOpAuditUserProvider;
import org.talend.daikon.spring.audit.logs.service.AuditLogGenerationFilter;
import org.talend.daikon.spring.audit.logs.service.AuditLogGenerationFilterImpl;
import org.talend.daikon.spring.audit.logs.service.AuditLogger;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties(AuditKafkaProperties.class)
@ConditionalOnProperty(value = "audit.enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogConfig {

    @Primary
    @Bean
    public AuditLogGenerationFilter auditLogAspect(ObjectMapper objectMapper, Optional<AuditUserProvider> auditUserProvider,
            AuditLogger auditLogger) {
        return new AuditLogGenerationFilterImpl(objectMapper, auditUserProvider.orElse(new NoOpAuditUserProvider()), auditLogger);
    }
}
