package org.talend.daikon.multitenant.context;

import org.junit.After;
import org.junit.Before;
import org.talend.daikon.multitenant.provider.DefaultTenant;

/**
 * @author agonzalez
 */
public class GlobalTenancyContextHolderStrategyTest extends InheritableThreadLocalTenancyContextHolderStrategyTest {

    @Before
    @Override
    public void setUp() throws Exception {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_GLOBAL);
        TenancyContext tc = new DefaultTenancyContext();
        tc.setTenant(new DefaultTenant("id", "myTenant"));
        TenancyContextHolder.setContext(tc);

    }

    @After
    @Override
    public void tearDown() {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_THREADLOCAL);
    }
}
