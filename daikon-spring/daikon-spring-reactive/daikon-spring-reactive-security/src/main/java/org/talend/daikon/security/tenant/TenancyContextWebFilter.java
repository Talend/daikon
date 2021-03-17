package org.talend.daikon.security.tenant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.talend.daikon.multitenant.context.TenancyContext;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Inspired by {@link org.springframework.security.web.server.context.ReactorContextWebFilter}
 */
public abstract class TenancyContextWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext().filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication).flatMap(authentication -> chain.filter(exchange)
                        .subscriberContext(c -> c.hasKey(TenancyContext.class) ? c : withTenancyContext(c, authentication)));

    }

    private Context withTenancyContext(Context mainContext, Authentication authentication) {
        return mainContext.putAll(loadTenancyContext(authentication).as(ReactiveTenancyContextHolder::withTenancyContext));
    }

    public abstract Mono<TenancyContext> loadTenancyContext(Authentication authentication);
}
