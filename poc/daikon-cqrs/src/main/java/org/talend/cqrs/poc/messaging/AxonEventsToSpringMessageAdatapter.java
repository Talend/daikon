package org.talend.cqrs.poc.messaging;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.SubscribableMessageSource;
import org.axonframework.spring.messaging.OutboundEventMessageChannelAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(name = "kafka.Kafka")
@EnableBinding({ ChannelDefinition.class }) // bing channels to Broker Binders(Kafka or else)
public class AxonEventsToSpringMessageAdatapter extends OutboundEventMessageChannelAdapter {

    // plugin Axon event bus to the spring output message channel (eventually bound to kafka)
    public AxonEventsToSpringMessageAdatapter(SubscribableMessageSource<EventMessage<?>> sms,
            @Qualifier("output") MessageChannel output) {
        super(sms, output);
    }

}
