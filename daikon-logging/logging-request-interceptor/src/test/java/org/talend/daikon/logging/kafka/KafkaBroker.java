package org.talend.daikon.logging.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Properties;

public class KafkaBroker {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaBroker.class);

    private static final String LOCALHOSTSTRING = InetAddress.getLoopbackAddress().getHostAddress() + ":";

    private KafkaServerStartable kafka;

    // these are being set inside this class
    private BrokerConfig config;

    public KafkaBroker(BrokerConfig config) {

        this.config = config;

        Properties props = new Properties();
        props.setProperty("host.name", InetAddress.getLoopbackAddress().getHostAddress());
        props.setProperty("log.dir", config.getPath() + "/" + config.getBrokerId());
        props.setProperty("port", String.valueOf(config.getKafkaPort()));
        props.setProperty("broker.id", String.valueOf(config.getBrokerId()));
        props.setProperty("zookeeper.connect", config.getZkString());
        props.setProperty("zookeeper.connection.timeout.ms", "1000000");
        props.setProperty("controlled.shutdown.enable", Boolean.TRUE.toString());
        //props.setProperty("num.partitions", String.valueOf(1));
        props.setProperty("delete.topic.enable", "true");

        KafkaConfig kafkaConfig = new KafkaConfig(props);
        LOG.info("Instantiating Embedded broker with id {} at port {} ", config.getBrokerId(), config.getKafkaPort());
        this.kafka = new KafkaServerStartable(kafkaConfig);
        LOG.info("Starting Embedded kafka with id {} at port {} ", config.getBrokerId(), config.getKafkaPort());
        this.kafka.startup();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info("Embedded kafka with id {} Up and Running", config.getBrokerId());
    }

    public void stop() {
        if (null != kafka) {
            LOG.info("Embedded kafka with id {} being ShutDown", config.getBrokerId());
            kafka.shutdown();
        }

    }

    // Get the config For the Broker
    public BrokerConfig getConfig() {
        return config;
    }

    // Config to instantiate the broker
    public static class BrokerConfig {

        private int kafkaPort;

        private String path;

        private String zkString;

        private int brokerId;

        private String kafkaBrokerString;

        public BrokerConfig(int kafkaPort, String path, String zkString, int brokerId) {
            this.kafkaPort = kafkaPort;
            this.path = path;
            this.zkString = zkString;
            this.brokerId = brokerId;

            kafkaBrokerString = LOCALHOSTSTRING + kafkaPort;
        }

        public int getKafkaPort() {
            return kafkaPort;
        }

        public String getPath() {
            return path;
        }

        public String getZkString() {
            return zkString;
        }

        public int getBrokerId() {
            return brokerId;
        }

        public String getKafkaBrokerString() {
            return kafkaBrokerString;
        }

    }

}
