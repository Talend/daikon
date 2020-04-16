package org.talend.daikon.spring.audit.logs.config;

import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.NoOpAuditUserProvider;
import org.talend.daikon.spring.audit.logs.service.AuditLogGenerationFilter;
import org.talend.daikon.spring.audit.logs.service.AuditLogGenerationFilterImpl;
import org.talend.daikon.spring.audit.logs.service.AuditLogger;
import org.talend.logging.audit.AuditLoggerFactory;
import org.talend.logging.audit.LogAppenders;
import org.talend.logging.audit.impl.AuditConfiguration;
import org.talend.logging.audit.impl.AuditConfigurationMap;
import org.talend.logging.audit.impl.Backends;
import org.talend.logging.audit.impl.SimpleAuditLoggerBase;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties(AuditKafkaProperties.class)
@ConditionalOnProperty(value = "audit.enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogConfig {

    private Properties getProperties(AuditKafkaProperties auditKafkaProperties, String applicationName) {
        Properties properties = new Properties();
        properties.put("application.name", applicationName);
        properties.put("backend", Backends.KAFKA.name());
        properties.put("log.appender", LogAppenders.NONE.name());
        properties.put("kafka.bootstrap.servers", auditKafkaProperties.getBootstrapServers());
        properties.put("kafka.topic", auditKafkaProperties.getTopic());
        properties.put("kafka.partition.key.name", auditKafkaProperties.getPartitionKeyName());
        return properties;
    }

    @Bean
    public AuditLogGenerationFilterImpl auditLogAspect(ObjectMapper objectMapper, Optional<AuditUserProvider> auditUserProvider,
            AuditKafkaProperties auditKafkaProperties, @Value("${spring.application.name}") String applicationName) {
        Properties properties = getProperties(auditKafkaProperties, applicationName);
        AuditConfigurationMap config = AuditConfiguration.loadFromProperties(properties);
        AuditLogger auditLogger = AuditLoggerFactory.getEventAuditLogger(AuditLogger.class, new SimpleAuditLoggerBase(config));
        return new AuditLogGenerationFilterImpl(objectMapper, auditUserProvider.orElse(new NoOpAuditUserProvider()), auditLogger);
    }
}
