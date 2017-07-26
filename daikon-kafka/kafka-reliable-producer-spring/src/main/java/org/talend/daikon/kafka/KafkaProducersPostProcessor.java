package org.talend.daikon.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "daikon.kafka.record.sending.enable")
public class KafkaProducersPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageScheduler.class);

    @Autowired
    private PendingRecordRepository pendingRecordRepository;

    @Autowired
    private KafkaMessageScheduler pendingMessageScheduler;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Producer) {
            Producer<?, ?> producer = (Producer) bean;
            KafkaReliableProducer<?, ?> reliableProducer = new KafkaReliableProducer<>(beanName, producer,
                    pendingRecordRepository);
            pendingMessageScheduler.addKafkaReliableProducer(reliableProducer);
            return reliableProducer;

        }
        return bean;
    }
}
