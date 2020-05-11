package org.talend.daikon.spring.audit.logs.config;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.talend.daikon.spring.audit.logs.service.AuditLogGeneratorInterceptor;

import java.util.Optional;

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
    // The purpose of this filter is to cache request content
    // And then be able to read it from the interceptor
    public Filter auditLogCachingFilter() {
        return (servletRequest, servletResponse, filterChain) -> {
            ServletRequest wrappedRequest = Optional.of(servletRequest).filter(r -> r instanceof HttpServletRequest)
                    .map(HttpServletRequest.class::cast).map(ContentCachingRequestWrapper::new).get();
            ServletResponse wrappedResponse = Optional.of(servletResponse).filter(r -> r instanceof HttpServletResponse)
                    .map(HttpServletResponse.class::cast).map(ContentCachingResponseWrapper::new).get();
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        };
    }
}
