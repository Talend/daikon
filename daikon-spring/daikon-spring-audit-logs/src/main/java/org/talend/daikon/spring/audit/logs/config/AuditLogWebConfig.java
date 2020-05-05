package org.talend.daikon.spring.audit.logs.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.talend.daikon.spring.audit.logs.service.AuditLogGeneratorInterceptor;

@Configuration
@ConditionalOnProperty(value = "audit.enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogWebConfig implements WebMvcConfigurer {

    private final AuditLogGeneratorInterceptor auditLogGeneratorInterceptor;

    public AuditLogWebConfig(AuditLogGeneratorInterceptor auditLogGeneratorInterceptor) {
        this.auditLogGeneratorInterceptor = auditLogGeneratorInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLogGeneratorInterceptor);
    }
}
