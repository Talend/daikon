package org.talend.daikon.security.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.talend.daikon.multitenant.context.TenancyContext;

class JwtTenancyContextWebFilterTest {

    @Test
    void testTenancyContextIsLoaded() {
        TenancyContextWebFilter tenancyContextWebFilter = new JwtTenancyContextWebFilter();

        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("entitlements", "FAKE")
                .claim("tenant_id", "FakeTenantId").build();

        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        TenancyContext tenancyContext = tenancyContextWebFilter.loadTenancyContext(jwtAuthenticationToken).block();
        assertEquals("FakeTenantId", tenancyContext.getTenant().getIdentity());
    }

}