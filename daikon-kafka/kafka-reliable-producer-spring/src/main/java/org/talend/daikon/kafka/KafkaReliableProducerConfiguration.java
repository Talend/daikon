package org.talend.daikon.kafka;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = { "org.talend.daikon.kafka" })
@Configuration
@EnableScheduling
@EnableAutoConfiguration
public class KafkaReliableProducerConfiguration {

}
