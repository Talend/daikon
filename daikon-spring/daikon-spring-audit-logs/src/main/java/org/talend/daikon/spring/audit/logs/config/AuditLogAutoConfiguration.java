package org.talend.daikon.spring.audit.logs.config;

import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.NoOpAuditUserProvider;
import org.talend.daikon.spring.audit.logs.service.*;
import org.talend.logging.audit.AuditLoggerFactory;
import org.talend.logging.audit.LogAppenders;
import org.talend.logging.audit.impl.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties({ AuditProperties.class, AuditKafkaProperties.class })
@ConditionalOnProperty(value = "audit.enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.auditLogGeneratorInterceptor(null, null));
    }

    private Properties getProperties(AuditKafkaProperties auditKafkaProperties, String applicationName) {
        Properties properties = new Properties();
        properties.put("application.name", applicationName);
        properties.put("backend", Backends.KAFKA.name());
        properties.put("log.appender", LogAppenders.NONE.name());
        properties.put("kafka.bootstrap.servers", auditKafkaProperties.getBootstrapServers());
        properties.put("kafka.topic", auditKafkaProperties.getTopic());
        properties.put("kafka.partition.key.name", auditKafkaProperties.getPartitionKeyName());
        properties.put("kafka.block.timeout.ms", auditKafkaProperties.getBlockTimeoutMs());
        return properties;
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
            if (wrappedResponse instanceof ContentCachingResponseWrapper) {
                ((ContentCachingResponseWrapper) wrappedResponse).copyBodyToResponse();
            }
        };
    }

    @Bean
    public AuditLoggerBase auditLoggerBase(AuditKafkaProperties auditKafkaProperties,
            @Value("${spring.application.name}") String applicationName) {
        Properties properties = getProperties(auditKafkaProperties, applicationName);
        AuditConfigurationMap config = AuditConfiguration.loadFromProperties(properties);
        return new SimpleAuditLoggerBase(config);
    }

    @Bean
    public AuditLogSender auditLogSender(Optional<AuditUserProvider> auditUserProvider, AuditLoggerBase auditLoggerBase,
            AuditLogIpExtractor AuditLogIpExtractor) {
        AuditLogger auditLogger = AuditLoggerFactory.getEventAuditLogger(AuditLogger.class, auditLoggerBase);
        return new AuditLogSenderImpl(auditUserProvider.orElse(new NoOpAuditUserProvider()), auditLogger, AuditLogIpExtractor);
    }

    @Bean
    public AuditLogGeneratorAspect auditLogGeneratorAspect(AuditLogSender auditLogSender, ResponseExtractor responseExtractor) {
        return new AuditLogGeneratorAspect(auditLogSender, responseExtractor);
    }

    @Bean
    public AuditLogGeneratorInterceptor auditLogGeneratorInterceptor(AuditLogSender auditLogSender, ObjectMapper objectMapper) {
        return new AuditLogGeneratorInterceptor(auditLogSender, objectMapper);
    }

    @Bean
    public AuditLogIpExtractor auditLogIpExtractor(AuditProperties auditProperties) {
        return new AuditLogIpExtractorImpl(auditProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseExtractor responseExtractor() {
        return new ResponseExtractor() {

            // In case of ResponseEntity, body and status code can be retrieved directly
            @Override
            public int getStatusCode(Object responseObject) {
                return responseObject instanceof ResponseEntity ? ((ResponseEntity) responseObject).getStatusCode().value() : 0;
            }

            @Override
            public Object getResponseBody(Object responseObject) {
                return responseObject instanceof ResponseEntity ? ((ResponseEntity) responseObject).getBody() : null;
            }
        };
    }
}
