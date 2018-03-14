package org.talend.daikon.logging.kafka;

import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class KafkaUnit extends ExternalResource {

    private List<KafkaBroker> brokerList;

    private Integer[] kafkaPorts;

    private String path;

    private final EmbeddedZookeeper zookeeper;

    kafkaUnitConfig config;

    public KafkaUnit(int clusterSize) {

        zookeeper = new EmbeddedZookeeper();

        brokerList = new ArrayList<>();
        kafkaPorts = new Integer[clusterSize];

        for (int i = 0; i < clusterSize; i++) {
            try {
                kafkaPorts[i] = FreeRandomPort.generateRandomPort();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            path = Files.createTempDirectory("kafka-").toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void before() throws Throwable {

        zookeeper.before();
        Thread.sleep(100);

        //start the broker
        for (int i = 0; i < kafkaPorts.length; i++) {
            KafkaBroker.BrokerConfig config = new KafkaBroker.BrokerConfig(kafkaPorts[i], path,
                    zookeeper.getConfig().getZkstring(), i);
            KafkaBroker broker = new KafkaBroker(config);
            brokerList.add(broker);

        }
        config = new kafkaUnitConfig(zookeeper.getConfig().getZkstring(), brokerList);
    }

    @Override
    protected void after() {

        //Shutdown each broker
        brokerList.stream().forEach(KafkaBroker::stop);

        if (null != zookeeper) {
            zookeeper.after();
        }

        if (null != path) {
            new File(path).deleteOnExit();
        }
    }

    public kafkaUnitConfig getConfig() {
        return config;
    }

    public static class kafkaUnitConfig {

        private String zkString;

        private String kafkaBrokerString;

        public kafkaUnitConfig(String zkString, List<KafkaBroker> brokerList) {

            this.zkString = zkString;
            // Form
            StringBuilder kafkaBrokerStringBuilder = new StringBuilder();
            String delim = ",";
            for (KafkaBroker broker : brokerList) {
                if (kafkaBrokerStringBuilder.length() > 0) {
                    kafkaBrokerStringBuilder.append(delim);
                }
                kafkaBrokerStringBuilder.append(broker.getConfig().getKafkaBrokerString());
            }

            this.kafkaBrokerString = kafkaBrokerStringBuilder.toString();
        }

        public String getZkString() {
            return zkString;
        }

        public String getKafkaBrokerString() {
            return kafkaBrokerString;
        }

    }

}
