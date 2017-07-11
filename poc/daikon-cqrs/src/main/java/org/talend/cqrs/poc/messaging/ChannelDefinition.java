package org.talend.cqrs.poc.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * defines the spring-cloud-stream output channel to be used. see @EnableBinding in
 * {@link AxonEventsToSpringMessageAdatapter}
 */
public interface ChannelDefinition {

    @Output("output")
    MessageChannel output();
}
