package org.talend.daikon.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;

import info.batey.kafka.unit.KafkaUnit;
import kafka.common.TopicExistsException;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@ContextConfiguration
@TestPropertySource(properties = { "daikon.kafka.record.sending.enable = true" })
@ComponentScan(basePackages = { "org.talend.daikon.kafka", "org.talend.daikon.kafka.mongo" })
@Ignore
public class FullIntegrationTest {

    @Autowired
    private KafkaReliableProducer<String, String> producer;

    @Autowired
    private KafkaUnitWrapper kafkaUnitWrapper;

    @Autowired
    private PendingRecordRepository pendingRecordRepository;

    @Autowired
    private KafkaMessageScheduler kafkaMessageScheduler;

    @Before
    public void setUp() throws Exception {
        if (!kafkaUnitWrapper.isStarted()) {
            kafkaUnitWrapper.start();
        }
    }

    @Test
    public void testKafkaIsAvailable() throws Exception {
        String value = "This message just flows through Kafka";
        Future<RecordMetadata> sync = producer.send(new ProducerRecord<>("topic1", "key", value));
        sync.get();
        List<String> receivedMessages = kafkaUnitWrapper.waitForMessages(1);
        Assert.assertEquals(1, receivedMessages.size());
        Assert.assertEquals(value, receivedMessages.get(0));
    }

    @Test
    // TODO make this test more stable
    public void testKafkaShutdown() throws Exception {
        String value = "This message was saved by a KafkaReliableProducer";
        kafkaUnitWrapper.stop();
        Future<RecordMetadata> sync = producer.send(new ProducerRecord<>("topic1", "key", value));
        sync.get();
        List<PendingRecord> pendingRecords = pendingRecordRepository.findPendingRecordByApplication("producer");
        Assert.assertEquals(1, pendingRecords.size());
        kafkaUnitWrapper.start();
        kafkaMessageScheduler.sendPendingRecords();
        List<String> receivedMessages = kafkaUnitWrapper.waitForMessages(1);
        Assert.assertEquals(1, receivedMessages.size());
        Assert.assertEquals(value, receivedMessages.get(0));
    }

    @Configuration
    public static class MongoConfiguration extends AbstractMongoConfiguration {

        private static final String MONGO_DB = "reliableKafkaProducer";

        @Override
        protected String getDatabaseName() {
            return MONGO_DB;
        }

        @Override
        public Mongo mongo() throws Exception {
            return new Fongo(MONGO_DB).getMongo();
        }
    }

    @Configuration
    public static class KafkaConfiguration {

        @Bean(initMethod = "start", destroyMethod = "stop")
        public KafkaUnitWrapper kafka() {
            return new KafkaUnitWrapper("topic1");
        }

        @Bean
        @Autowired
        public KafkaProducer<String, String> producer(KafkaUnitWrapper kafka) {
            Map<String, Object> conf = new HashMap<>();
            conf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + kafka.getKafkaPort());
            conf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            conf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            conf.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 50);
            return new KafkaProducer<>(conf);
        }

    }

    public static class KafkaUnitWrapper {

        private KafkaUnit kafkaUnit;

        private int kafkaPort = 0;

        private int zookeeperPort = 0;

        private final String kafkaTopic;

        private boolean started = false;

        public KafkaUnitWrapper(String topic) {
            this.kafkaTopic = topic;
        }

        public void start() {
            if (zookeeperPort == 0) {
                zookeeperPort = SocketUtils.findAvailableTcpPort(5000, 6000);
            }
            if (kafkaPort == 0) {
                kafkaPort = SocketUtils.findAvailableTcpPort(zookeeperPort + 1, zookeeperPort + 1000);
            }
            if (kafkaUnit == null) {
                kafkaUnit = new KafkaUnit("localhost:" + zookeeperPort, "localhost:" + kafkaPort);
                kafkaUnit.setKafkaBrokerConfig("broker_id", String.valueOf(0));
            }
            kafkaUnit.startup();
            try {
                kafkaUnit.createTopic(this.kafkaTopic);
            } catch (TopicExistsException e) {

            }
            started = true;
        }

        public void stop() throws Exception {
            kafkaUnit.shutdown();
            started = false;
        }

        public List<String> waitForMessages(int nbMessages) throws Exception {
            return this.kafkaUnit.readMessages(kafkaTopic, nbMessages);
        }

        public boolean isStarted() {
            return started;
        }

        public int getKafkaPort() {
            return this.kafkaPort;
        }
    }
}
