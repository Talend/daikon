package org.talend.daikon.spring.audit.logs.config;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.talend.daikon.spring.audit.logs.service.AuditLogGeneratorInterceptor;

@Configuration
@ConditionalOnBean(AuditLogGeneratorInterceptor.class)
public class AuditLogWebAutoConfiguration implements WebMvcConfigurer {

    private final AuditLogGeneratorInterceptor auditLogGeneratorInterceptor;

    public AuditLogWebAutoConfiguration(AuditLogGeneratorInterceptor auditLogGeneratorInterceptor) {
        this.auditLogGeneratorInterceptor = auditLogGeneratorInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLogGeneratorInterceptor);
    }

    @Bean
    public Filter auditLogCachingFilter() {
        return (servletRequest, servletResponse, filterChain) -> {
            HttpServletRequest currentRequest = (HttpServletRequest) servletRequest;
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(currentRequest);
            filterChain.doFilter(wrappedRequest, servletResponse);
        };
    }
}
