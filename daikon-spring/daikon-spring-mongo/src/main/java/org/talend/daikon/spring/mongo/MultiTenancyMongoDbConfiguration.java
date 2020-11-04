package org.talend.daikon.spring.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Component;

@Configuration
@Conditional(MultiTenancyMongoDbConfiguration.MultiTenancyMongoDbCondition.class)
public class MultiTenancyMongoDbConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiTenancyMongoDbConfiguration.class);

    private static final String ACTIVE_MONGODB_MULTI_TENANCY = "multi-tenancy.mongodb.active";

    /**
     * <h1>{@link BeanPostProcessor} notice</h1>
     * Don't use any {@link org.springframework.beans.factory.annotation.Autowired} in the
     * configuration as it will prevent autowired beans to be processed by BeanPostProcessor.
     */
    @Component
    public class MultiTenancyMongoDbProcessor implements BeanPostProcessor, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof MongoDatabaseFactory) {
                LOGGER.info("Enabling MongoDB multi tenancy support on '{}' (wrapped bean: {})...", bean.getClass(), beanName);
                final TenantInformationProvider tenantProvider = applicationContext.getBean(TenantInformationProvider.class);
                final MongoClientProvider mongoClientProvider = applicationContext.getBean(MongoClientProvider.class);
                return new MultiTenancyMongoDbFactory((MongoDatabaseFactory) bean, tenantProvider, mongoClientProvider);
            }
            return bean;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }
    }

    /**
     * A condition that enable multi tenancy if {@link #ACTIVE_MONGODB_MULTI_TENANCY} is set to "true" in configuration.
     * 
     * @see Environment#getProperty(String)
     */
    public static class MultiTenancyMongoDbCondition implements Condition {

        public MultiTenancyMongoDbCondition() {
            // Needed for Spring init.
        }

        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            final Environment environment = conditionContext.getEnvironment();
            return environment.getProperty(ACTIVE_MONGODB_MULTI_TENANCY, Boolean.class, Boolean.FALSE);
        }
    }
}
