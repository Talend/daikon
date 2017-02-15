package org.talend.daikon;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.talend.daikon.annotation.Service;

import com.webcohesion.enunciate.EnunciateContext;
import com.webcohesion.enunciate.api.ApiRegistry;
import com.webcohesion.enunciate.module.ApiRegistryProviderModule;
import com.webcohesion.enunciate.modules.spring_web.EnunciateSpringWebContext;
import com.webcohesion.enunciate.modules.spring_web.SpringWebModule;
import com.webcohesion.enunciate.modules.spring_web.model.RequestMapping;
import com.webcohesion.enunciate.modules.spring_web.model.SpringController;

import static com.webcohesion.enunciate.module.ApiRegistryProviderModule.DataTypeDetectionStrategy.*;

/**
 * This Enunciate module extends the Spring default ones and allow to take into account classes annotated with
 * {@link org.talend.daikon.annotation.Service} annotation.
 */
public class ServiceDocumentation extends SpringWebModule {

    private ApiRegistry registry;

    @Override
    public String getName() {
        return "service-docs";
    }

    @Override
    public void setApiRegistry(ApiRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void call(EnunciateContext context) {
        EnunciateSpringWebContext springContext = new EnunciateSpringWebContext(context, true);

        ApiRegistryProviderModule.DataTypeDetectionStrategy detectionStrategy = getDataTypeDetectionStrategy();
        if (detectionStrategy != passive) {
            Set<? extends Element> elements = detectionStrategy == local
                    ? context.getLocalApiElements() : context.getApiElements();
            // add root resource.
            elements.stream() //
                    .filter(declaration -> declaration instanceof TypeElement && declaration.getAnnotation(Service.class) != null) //
                    .forEach(service -> {
                        // add root resource.
                        SpringController springController = new SpringController((TypeElement) service, springContext);
                        LinkedList<Element> contextStack = new LinkedList<>();
                        contextStack.push(springController);
                        try {
                            List<RequestMapping> requestMappings = springController.getRequestMappings();
                            if (!requestMappings.isEmpty()) {
                                springContext.add(springController);
                                for (RequestMapping requestMapping : requestMappings) {
                                    addReferencedDataTypeDefinitions(requestMapping, contextStack);
                                }
                            }
                        } finally {
                            contextStack.pop();
                        }
                    });
        }
        // tidy up the application path.
        String relativeContextPath = this.config.getString("application[@path]", "");
        while (relativeContextPath.startsWith("/")) {
            relativeContextPath = relativeContextPath.substring(1);
        }
        while (relativeContextPath.endsWith("/")) {
            // trim off any leading slashes
            relativeContextPath = relativeContextPath.substring(0, relativeContextPath.length() - 1);
        }
        springContext.setRelativeContextPath(relativeContextPath);
        springContext.setGroupingStrategy(getGroupingStrategy());
        springContext.setPathSortStrategy(getPathSortStrategy());
        if (!springContext.getControllers().isEmpty()) {
            this.registry.getResourceApis().add(springContext);
        }
    }
}
