package org.talend.daikon.spring.metrics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import brave.Tracer;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackageClasses = MetricsConfiguration.class)
public class MetricsConfiguration {

    @Bean
    public Aspects metricAspect(@Autowired Tracer tracer, @Autowired MeterRegistry repository) {
        return new Aspects(tracer, repository);
    }
}
