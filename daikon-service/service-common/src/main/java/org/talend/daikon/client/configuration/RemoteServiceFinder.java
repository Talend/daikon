package org.talend.daikon.client.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.talend.daikon.annotation.Service;
import org.talend.daikon.client.Access;
import org.talend.daikon.client.ServiceFinder;

/**
 * A {@link ServiceFinder} implementation that leverages Feign to create a remote client.
 */
@Component
class RemoteServiceFinder implements ServiceFinder {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public <T> T find(Class<T> serviceClass) {
        final Service annotation = AnnotationUtils.findAnnotation(serviceClass, Service.class);
        if (annotation != null) {
            final String name = annotation.name();
            return applicationContext.getBean(name + "FeignClient", serviceClass);
        } else {
            return null;
        }
    }

    @Override
    public boolean allow(Access access) {
        return access == Access.REMOTE;
    }
}
