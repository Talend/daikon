package org.talend.daikon.logging.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.logging.TalendKafkaConsumerInterceptor;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaGenericConsumer {

    public static final Logger log = LoggerFactory.getLogger(KafkaGenericConsumer.class);

    private final ConsumerConnector consumer;

    private final String topic;

    private ExecutorService executor;

    private ConcurrentLinkedQueue<String> resultQueue = new ConcurrentLinkedQueue<>();

    public KafkaGenericConsumer(String zookeeper, String groupId, String topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
        this.topic = topic;
    }

    public Queue<String> getResultQueue() {
        return resultQueue;
    }

    public void shutdown() {
        if (consumer != null)
            consumer.shutdown();
        if (executor != null)
            executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                log.info("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            log.info("Interrupted during shutdown, exiting uncleanly");
        }
    }

    public class ConsumerTest implements Runnable {

        private KafkaStream stream;

        private int threadNumber;

        public ConsumerTest(KafkaStream stream, int threadNumber) {
            this.threadNumber = threadNumber;
            this.stream = stream;
        }

        @Override
        public void run() {
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (it.hasNext()) {
                //out of key and value only value is being used.
                String data = new String(it.next().message());
                resultQueue.add(data);
                log.info("READ DATA in Thread {} : {}", threadNumber, data);
            }
            log.info("Shutting down Thread {} ", threadNumber);
        }
    }

    public void run(int numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, numThreads);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        // now launch all the threads //
        executor = Executors.newFixedThreadPool(numThreads);
        // now create an object to consume the messages //
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new ConsumerTest(stream, threadNumber));
            threadNumber++;
        }
    }

    private static ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        props.put("interceptor.classes", TalendKafkaConsumerInterceptor.class.getName());
        return new ConsumerConfig(props);
    }

}
