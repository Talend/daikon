package org.talend.daikon.security.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.talend.daikon.multitenant.context.DefaultTenancyContext;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.provider.DefaultTenant;
import reactor.core.publisher.Mono;

public class JwtTenancyContextWebFilter extends TenancyContextWebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTenancyContextWebFilter.class);

    @Override
    public Mono<TenancyContext> loadTenancyContext(Authentication authentication) {
        final TenancyContext tenantContext = new DefaultTenancyContext();
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            LOGGER.debug("Populate TenancyContext for '{}' based on jwt data",
                    jwtPrincipal.getClaims().getOrDefault("tenant_id", null));
            tenantContext.setTenant(new DefaultTenant(jwtPrincipal.getClaims().getOrDefault("tenant_id", null), null));
        }
        return Mono.justOrEmpty(tenantContext);
    }

}
