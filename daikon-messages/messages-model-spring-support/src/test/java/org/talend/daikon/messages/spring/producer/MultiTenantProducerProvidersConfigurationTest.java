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
package org.talend.daikon.messages.spring.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.messages.header.producer.TenantIdProvider;
import org.talend.daikon.messages.spring.test.utils.MessageTestApp;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageTestApp.class, properties = "iam.accounts.url=aValue")
public class MultiTenantProducerProvidersConfigurationTest {

    @Autowired
    private TenantIdProvider tenantIdProvider;

    @Autowired
    private ApplicationContext context;

    @Test
    public void noBeanOverriding() {
        assertThat(tenantIdProvider).isNotNull();
        String[] beans = context.getBeanNamesForType(TenantIdProvider.class);
        assertThat(beans).hasSize(1);
        assertThat(beans[0]).isEqualTo("multiTenantIdProvider");
    }
}
