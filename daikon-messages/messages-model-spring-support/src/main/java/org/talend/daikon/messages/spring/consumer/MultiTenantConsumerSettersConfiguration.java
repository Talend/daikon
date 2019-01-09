package org.talend.daikon.messages.spring.consumer;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.talend.daikon.messages.header.consumer.TenantIdSetter;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.daikon.multitenant.provider.DefaultTenant;

@Configuration
@ConditionalOnProperty("iam.accounts.url")
@AutoConfigureBefore({ DefaultConsumerSettersConfiguration.class })
public class MultiTenantConsumerSettersConfiguration {

    @Bean
    public TenantIdSetter tenantIdSetter() {
        return new TenantIdSetter() {

            @Override
            public void setCurrentTenantId(String tenantId) {
                final Tenant tenant = new DefaultTenant(tenantId, null);
                TenancyContext context = TenancyContextHolder.createEmptyContext();
                context.setTenant(tenant);
                TenancyContextHolder.setContext(context);
            }
        };
    }

}
