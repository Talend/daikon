package org.talend.daikon.messages.spring.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.messages.header.consumer.TenantIdSetter;
import org.talend.daikon.messages.spring.test.utils.MessageTestApp;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageTestApp.class)
public class DefaultConsumerSettersConfigurationTest {

    @Autowired
    private TenantIdSetter tenantIdSetter;

    @Autowired
    private ApplicationContext context;

    @Test
    public void noBeanOverriding() {
        assertThat(tenantIdSetter).isNotNull();
        String[] beans = context.getBeanNamesForType(TenantIdSetter.class);
        assertThat(beans).hasSize(1);
        assertThat(beans[0]).isEqualTo("noopTenantIdSetter");
    }
}