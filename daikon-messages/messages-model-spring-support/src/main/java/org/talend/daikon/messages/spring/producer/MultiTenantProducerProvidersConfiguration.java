package org.talend.daikon.messages.spring.producer;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.talend.daikon.messages.header.producer.TenantIdProvider;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;

@Configuration
@ConditionalOnProperty("iam.accounts.url")
public class MultiTenantProducerProvidersConfiguration {

    @Bean
    @Primary
    public TenantIdProvider defaultTenantIdProvider() {
        return new TenantIdProvider() {

            @Override
            public String getTenantId() {
                Optional<Tenant> optionalTenant = TenancyContextHolder.getContext().getOptionalTenant();
                if (optionalTenant.isPresent()) {
                    return optionalTenant.get().getIdentity().toString();
                } else {
                    return null;
                }
            }
        };
    }

}
