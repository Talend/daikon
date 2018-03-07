package org.talend.daikon.messages.spring.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.talend.daikon.messages.header.producer.TenantIdProvider;
import org.talend.daikon.multitenant.context.TenancyContextHolder;

@Configuration
@ConditionalOnProperty("iam.accounts.url")
public class MultiTenantProducerProvidersConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    @Primary
    public TenantIdProvider defaultTenantIdProvider() {
        return new TenantIdProvider() {

            @Override
            public String getTenantId() {
                if (TenancyContextHolder.getContext() != null) {
                    return TenancyContextHolder.getContext().getTenant().getIdentity().toString();
                } else {
                    return "";
                }
            }
        };
    }

}
