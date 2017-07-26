///*
// * ============================================================================
// *
// * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
// *
// * This source code is available under agreement available at
// * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
// *
// * You should have received a copy of the agreement
// * along with this program; if not, write to Talend SA
// * 9 rue Pages 92150 Suresnes, France
// *
// * ============================================================================
// */
//
//package org.talend.daikon.kafka.util;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.util.SocketUtils;
//
//import info.batey.kafka.unit.KafkaUnit;
//
///**
// * Kafka configuration for integration tests
// */
//@Configuration
//public class TestKafkaConfiguration {
//
//    public final static String KAFKA_TOPIC = "testTopic";
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(TestKafkaConfiguration.class);
//
//    @Bean
//    @Autowired
//    public KafkaConsumer<String, String> kafkaConsumer(KafkaUnitWrapper kafkaUnit) {
//        Map<String, Object> conf = new HashMap<>();
//        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + kafkaUnit.kafkaPort);
//        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
//        conf.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        conf.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(conf);
//        consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));
//        return consumer;
//    }
//
//    @Bean
//    @Autowired
//    public KafkaProducer<String, String> kafkaProducer(KafkaUnitWrapper kafkaUnit) {
//        Map<String, Object> conf = getKafkaProducerConf(kafkaUnit);
//        return new KafkaProducer<>(conf);
//    }
//
//    @Bean(name = "kafkaProducerClosed")
//    @Autowired
//    public KafkaProducer<String, String> kafkaProducerClosed(KafkaUnitWrapper kafkaUnit) {
//        Map<String, Object> conf = getKafkaProducerConf(kafkaUnit);
//        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(conf);
//        kafkaProducer.close();
//        return kafkaProducer;
//    }
//
//    private Map<String, Object> getKafkaProducerConf(KafkaUnitWrapper kafkaUnit) {
//        Map<String, Object> conf = new HashMap<>();
//        conf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + kafkaUnit.kafkaPort);
//        conf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        conf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        conf.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
//        conf.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 2000);
//        return conf;
//    }
//
//    @Component
//    public class KafkaUnitWrapper {
//
//        private KafkaUnit kafkaUnit;
//
//        private int kafkaPort = 0;
//
//        @PostConstruct
//        public void start() {
//            LOGGER.warn("Starting kafka unit " + this.hashCode());
//            int zookeeperPort = SocketUtils.findAvailableTcpPort(5000, 6000);
//            this.kafkaPort = SocketUtils.findAvailableTcpPort(zookeeperPort + 1, zookeeperPort + 1000);
//            kafkaUnit = new KafkaUnit("localhost:" + zookeeperPort, "localhost:" + kafkaPort);
//            kafkaUnit.setKafkaBrokerConfig("broker_id", String.valueOf(0));
//            kafkaUnit.startup();
//            kafkaUnit.createTopic(KAFKA_TOPIC);
//        }
//
//        @PreDestroy
//        public void stop() {
//            LOGGER.warn("Shutting down kafka unit " + this.hashCode());
//            kafkaUnit.shutdown();
//        }
//
//        public List<String> waitForMessages(String topic, int nbMessages) throws Exception {
//            return this.kafkaUnit.readMessages(topic, nbMessages);
//        }
//    }
//}
