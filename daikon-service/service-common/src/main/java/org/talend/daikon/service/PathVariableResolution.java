package org.talend.daikon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class PathVariableResolution extends WebMvcConfigurationSupport {

    @Autowired
    ApplicationContext context;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new InheritedAnnotationArgumentResolver(context));
    }

}
