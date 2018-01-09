package org.talend.daikon.logging.kafka.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.logging.TalendKafkaProducerInterceptor;
import org.talend.daikon.logging.kafka.KafkaGenericConsumer;
import org.talend.daikon.logging.kafka.KafkaUnit;
import org.talend.daikon.logging.kafka.KafkaUnitAdmin;

import java.util.Properties;

public class KafkaUnitTest {

    public static final Logger log = LoggerFactory.getLogger(KafkaUnitTest.class);

    public static final String TOPIC = "TOPIC";

    private KafkaUnitAdmin admin;

    @ClassRule
    public static KafkaUnit cluster = new KafkaUnit(1);
    // KakaUnit(1)  number of Broker in the cluster

    @Before
    public void testKafkaUnit() throws Throwable {
        admin = new KafkaUnitAdmin(cluster);
        admin.createTopic(TOPIC, 1, 1, new Properties());
    }

    public void producerTest() {
        long events = 10;
        Properties props = new Properties();
        log.info("Broker list is : " + cluster.getConfig().getKafkaBrokerString());

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.getConfig().getKafkaBrokerString());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 2);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, TalendKafkaProducerInterceptor.class.getName()); // for kafka version from 0.10.xxx

        Producer<String, String> producer = new KafkaProducer<>(props);

        for (long nEvents = 0; nEvents < events; nEvents++) {
            String ip = "192.168.2." + nEvents;
            String msg = "www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>(TOPIC, ip, msg);
            producer.send(data);
            producer.flush();
        }
        producer.close();
    }

    @Test
    public void consumerTest() {
        producerTest();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }

        KafkaGenericConsumer consumer = new KafkaGenericConsumer(cluster.getConfig().getZkString(), "1", TOPIC);
        consumer.run(1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }
        Assert.assertEquals(consumer.getResultQueue().size(), 10);
        Assert.assertEquals(consumer.getResultQueue().contains("www.example.com,192.168.2.0"), true);
        Assert.assertEquals(consumer.getResultQueue().contains("www.example.com,192.168.2.9"), true);
        Assert.assertEquals(consumer.getResultQueue().contains("www.example.com,192.168.2.5"), true);
        consumer.shutdown();
    }

    @After
    public void deleteTopic() {
        admin.deleteTopic(TOPIC);
    }

}
