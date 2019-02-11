package org.talend.daikon.content.local;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.talend.daikon.content.ContextualPatternResolver;
import org.talend.daikon.content.ResourceResolver;

@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
@ConditionalOnProperty(name = "content-service.store", havingValue = "local")
public class LocalContentServiceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalContentServiceConfiguration.class);

    @Bean
    public ResourceResolver localResourceResolver(Environment environment) {
        final Boolean useAbsolutePath = environment.getProperty("content-service.store.local.absolute", Boolean.class,
                Boolean.TRUE);
        final FileSystemResourceLoader resourceLoader;
        if (useAbsolutePath) {
            resourceLoader = new FileSystemResourceLoader() {

                @Override
                protected Resource getResourceByPath(String path) {
                    return new AbsoluteContextResource(path);
                }
            };
        } else {
            resourceLoader = new FileSystemResourceLoader();
        }
        final PathMatchingResourcePatternResolver delegate = new PathMatchingResourcePatternResolver(resourceLoader);
        final String localPath = environment.getProperty("content-service.store.local.path", StringUtils.EMPTY);
        LOGGER.info("Files stored to '{}'", localPath);

        return new LocalResourceResolver(new ContextualPatternResolver(delegate, localPath));
    }

    /**
     * FileSystemResource that explicitly expresses an absolute path through implementing the ContextResource interface.
     */
    private static class AbsoluteContextResource extends FileSystemResource implements ContextResource {

        private AbsoluteContextResource(String path) {
            super(path);
        }

        @Override
        public String getPathWithinContext() {
            return getPath();
        }
    }

}
