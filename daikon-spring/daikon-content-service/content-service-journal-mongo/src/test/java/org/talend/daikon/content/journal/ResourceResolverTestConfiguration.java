package org.talend.daikon.content.journal;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.talend.daikon.content.ResourceResolver;

@Profile("mock")
@Configuration
public class ResourceResolverTestConfiguration {

    @Bean
    @Primary
    public ResourceResolver resourceResolver() {
        return Mockito.mock(ResourceResolver.class);
    }
}
