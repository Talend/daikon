package org.talend.daikon.logging.kafka;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.util.Properties;

// import kafka.admin.RackAwareMode;

/**
 * Created by kunalgautam on 17.02.17.
 */
public class KafkaUnitAdmin {

    public static final int tickTime = 5000;

    public static final int sessionTimeout = 60000;

    public static final int waitTime = 5000;

    private ZkUtils zkUtils;

    private static final ZkSerializer zkSerializer = new ZkSerializer() {

        public byte[] serialize(Object data) throws ZkMarshallingError {
            return ZKStringSerializer.serialize(data);
        }

        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            return ZKStringSerializer.deserialize(bytes);
        }
    };

    public KafkaUnitAdmin(KafkaUnit unit) throws Exception {
        ZkClient zkClient = new ZkClient(unit.getConfig().getZkString(), sessionTimeout, waitTime, zkSerializer);
        zkUtils = new ZkUtils(zkClient, new ZkConnection(unit.getConfig().getZkString()), false);
    }

    public void createTopic(String topicName, int noOfPartitions, int noOfReplication, Properties topicConfiguration) {
        AdminUtils.createTopic(zkUtils, topicName, noOfPartitions, noOfReplication, topicConfiguration, null);
    }

    public void deleteTopic(String topicName) {
        AdminUtils.deleteTopic(zkUtils, topicName);
    }

}
