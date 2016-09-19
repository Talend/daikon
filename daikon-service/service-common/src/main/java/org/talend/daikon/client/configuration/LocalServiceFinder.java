package org.talend.daikon.client.configuration;

import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.talend.daikon.annotation.Service;
import org.talend.daikon.client.Access;
import org.talend.daikon.client.ServiceFinder;

/**
 * A {@link ServiceFinder} implementation that leverages local application context in order to find service bean.
 */
@Component
class LocalServiceFinder implements ServiceFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServiceFinder.class);

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public <T> T find(Class<T> serviceClass) {
        final String[] eligibleLocalServices = applicationContext.getBeanNamesForAnnotation(Service.class);
        List<T> localServiceInstances = new LinkedList<>();
        for (String eligibleService : eligibleLocalServices) {
            final Object bean = applicationContext.getBean(eligibleService);
            if (serviceClass.isInstance(bean) && !Proxy.isProxyClass(bean.getClass())) {
                localServiceInstances.add((T) bean);
            }
        }
        if (localServiceInstances.isEmpty()) {
            LOGGER.info("No local service found for {}", serviceClass);
            return null;
        } else if (localServiceInstances.size() == 1) {
            LOGGER.info("One local service found for {}", serviceClass);
            return localServiceInstances.get(0);
        } else {
            LOGGER.warn("{} local services found for {}", localServiceInstances.size(), serviceClass);
            return null;
        }
    }

    @Override
    public boolean allow(Access access) {
        return access == Access.LOCAL;
    }
}
