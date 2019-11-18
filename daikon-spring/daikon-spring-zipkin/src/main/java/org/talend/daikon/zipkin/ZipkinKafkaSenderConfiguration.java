package org.talend.daikon.zipkin;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka.KafkaSender;

/**
 * Configures the zipkin kafka sender for another kafka broker than the functional broker.
 * 
 * @see ZipkinKafkaProperties
 * @see KafkaSender
 */
@Configuration
@ConditionalOnClass(ByteArraySerializer.class)
@ConditionalOnProperty(value = "spring.zipkin.enabled", havingValue = "true")
@EnableConfigurationProperties(ZipkinKafkaProperties.class)
public class ZipkinKafkaSenderConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ZipkinKafkaSenderConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public Sender kafkaSender(ZipkinKafkaProperties zipkinKafkaProperties, KafkaProperties kafkaProperties) {
        String bootstrapServers = zipkinKafkaProperties.getBootstrapServers();

        if (bootstrapServers == null) {
            bootstrapServers = String.join(",", kafkaProperties.getBootstrapServers());
        }

        LOG.info("building zipkin kafka sender with brokers [{}]", bootstrapServers);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put("key.serializer", ByteArraySerializer.class.getName());
        properties.put("value.serializer", ByteArraySerializer.class.getName());

        return KafkaSender.newBuilder() //
                .topic(zipkinKafkaProperties.getTopic()) //
                .bootstrapServers(bootstrapServers) //
                .overrides(properties) //
                .build();
    }
}
