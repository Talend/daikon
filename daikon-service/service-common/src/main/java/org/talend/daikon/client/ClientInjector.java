package org.talend.daikon.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.talend.daikon.annotation.Client;

/**
 * A {@link BeanPostProcessor} implementation that injects values in fields annotated with {@link Client}.
 * @see Client
 */
@Component
public class ClientInjector implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientInjector.class);

    @Autowired
    ClientService clientService;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) {
        ReflectionUtils.doWithFields(o.getClass(), field -> {
            final Client client = field.getAnnotation(Client.class);
            if (client != null) {
                LOGGER.debug("Injecting service client for field '{}' (in '{}')", field, o);
                ReflectionUtils.makeAccessible(field);
                field.set(o, clientService.of(field.getType(), client.access()));
                LOGGER.debug("Done injecting service client for field '{}' (in '{}')", field, o);
            }
        });
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) {
        return o;
    }
}
