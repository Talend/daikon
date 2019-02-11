package org.talend.daikon.security.token;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint;

import java.util.stream.Stream;

import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * A Spring Security configuration class that ensures the Actuator (as well as paths in
 * {@link #ADDITIONAL_PROTECTED_PATHS}) are protected by a token authentication.
 *
 * @see NoConfiguredTokenFilter When configuration's token value is empty or missing.
 * @see TokenAuthenticationFilter When configuration's token value is present.
 * @see #ADDITIONAL_PROTECTED_PATHS for list of protected paths.
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class TokenSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSecurityConfiguration.class);

    /*
     * Use this field to indicate paths that should be secured by token. It is not a configuration setting at the moment
     * to ensure all applications share same secured endpoints.
     */
    private static final String[] ADDITIONAL_PROTECTED_PATHS = { "/info", "/actuator/**", "/version" };

    private final Filter tokenAuthenticationFilter;

    public TokenSecurityConfiguration(@Value("${talend.security.token.value:}") String token) {
        final AntPathRequestMatcher[] matchers = Stream.of(ADDITIONAL_PROTECTED_PATHS) //
                .map(AntPathRequestMatcher::new) //
                .toArray(AntPathRequestMatcher[]::new);
        final RequestMatcher protectedPaths = new OrRequestMatcher(new OrRequestMatcher(matchers), toAnyEndpoint());
        if (StringUtils.isBlank(token)) {
            LOGGER.info("No token configured, protected endpoints are unavailable.");
            tokenAuthenticationFilter = new NoConfiguredTokenFilter(protectedPaths);
        } else {
            LOGGER.info("Configured token-based access security.");
            tokenAuthenticationFilter = new TokenAuthenticationFilter(token, protectedPaths);
        }
    }

    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        registry = registry.requestMatchers(toAnyEndpoint()).hasRole(TokenAuthentication.ROLE);
        for (String protectedPath : ADDITIONAL_PROTECTED_PATHS) {
            registry = registry.antMatchers(protectedPath).hasRole(TokenAuthentication.ROLE);
        }
        registry.and().addFilterAfter(tokenAuthenticationFilter, BasicAuthenticationFilter.class);
    }
}
