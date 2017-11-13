// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.multitenant.provider.DefaultTenant;

public class InheritableThreadLocalTenancyContextHolderStrategyTest extends TenancyContextHolderTest {

    @Before
    @Override
    public void setUp() throws Exception {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @After
    @Override
    public void tearDown() {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_THREADLOCAL);
    }

    @Test
    public void testSpawnThread() throws InterruptedException {
        TenancyContext tc = new DefaultTenancyContext();
        tc.setTenant(new DefaultTenant("id", "myTenant"));
        TenancyContextHolder.setContext(tc);
        StatusHolder statusHolder = new StatusHolder();
        Runnable runnable = () -> {
            statusHolder.assertEquals(tc, TenancyContextHolder.getContext());
        };
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join(60000L);
        statusHolder.assertSuccess();
    }
}
