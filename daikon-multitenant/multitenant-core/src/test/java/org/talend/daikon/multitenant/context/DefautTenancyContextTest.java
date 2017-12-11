package org.talend.daikon.multitenant.context;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.daikon.multitenant.provider.DefaultTenant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DefautTenancyContextTest {

    private DefaultTenancyContext tenancyContext;

    @Before
    public void setUp() {
        this.tenancyContext = new DefaultTenancyContext();
    }

    @Test
    public void testContext() {
        Tenant tenant = new DefaultTenant();
        tenancyContext.setTenant(tenant);
        assertEquals(tenant, tenancyContext.getTenant());
        assertEquals(tenant, tenancyContext.getOptionalTenant().get());
    }

    @Test(expected = NoCurrentTenantException.class)
    public void testNullContext() {
        tenancyContext.setTenant(null);
        tenancyContext.getTenant();
    }

    @Test
    public void testOptionalContext() {
        tenancyContext.setTenant(null);
        assertFalse(tenancyContext.getOptionalTenant().isPresent());
    }
}
