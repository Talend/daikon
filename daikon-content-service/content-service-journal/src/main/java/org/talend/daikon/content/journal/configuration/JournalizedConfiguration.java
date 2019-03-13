package org.talend.daikon.content.journal.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.content.journal.JournalizedResourceResolver;
import org.talend.daikon.content.journal.ResourceJournal;

@Configuration
@ConditionalOnProperty(name = "content-service.journalized", havingValue = "true")
public class JournalizedConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public BeanPostProcessor journalized() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof ResourceResolver) {
                    final ResourceJournal journal;
                    try {
                        journal = applicationContext.getBean(ResourceJournal.class);
                        journal.setResourceResolver((ResourceResolver) bean);
                    } catch (BeansException e) {
                        throw new MissingJournalBean(e);
                    }
                    return new JournalizedResourceResolver((ResourceResolver) bean, journal);
                }
                return bean;
            }
        };
    }

    @Bean
    public FailureAnalyzer incorrectJournalConfiguration() {
        return new IncorrectJournalConfigurationAnalyzer();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    class MissingJournalBean extends RuntimeException {
        MissingJournalBean(Exception e) {
            super(e);
        }
    }
}
