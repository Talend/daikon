// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.messages.spring.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.messages.header.consumer.TenantIdSetter;
import org.talend.daikon.messages.spring.consumer.ConsumerApp;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerApp.class, properties = "iam.accounts.url=aValue")
public class EnableMessagesConsumerAutoConfigTest {

    @Autowired
    private TenantIdSetter tenantIdSetter;

    @Autowired
    private ApplicationContext context;

    @Test
    public void noBeanOverriding() {
        assertThat(tenantIdSetter).isNotNull();
        Map<String, TenantIdSetter> tenantIdSetterBeans = context.getBeansOfType(TenantIdSetter.class);
        assertThat(tenantIdSetterBeans).hasSize(1);
    }
}
