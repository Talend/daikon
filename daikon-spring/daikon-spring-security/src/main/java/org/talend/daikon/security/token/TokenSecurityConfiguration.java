package org.talend.daikon.security.token;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint;

import java.util.List;

import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
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
 * {@link #additionalProtectedEndpoints}) are protected by a token authentication.
 *
 * @see NoConfiguredTokenFilter When configuration's token value is empty or missing.
 * @see TokenAuthenticationFilter When configuration's token value is present.
 * @see #additionalProtectedEndpoints for list of protected paths.
 */
@Configuration
@EnableWebSecurity
@Order(1)
@Conditional(TokenSecurityConfiguration.TokenSecurityCondition.class)
public class TokenSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSecurityConfiguration.class);

    @Value("${talend.security.allowPublicPrometheusEndpoint:false}")
    private boolean allowPrometheusUnauthenticatedAccess;

    @Autowired
    private PathMappedEndpoints actuatorEndpoints;

    @Autowired
    private WebEndpointProperties webEndpointProperties;

    private final List<TokenProtectedPath> additionalProtectedEndpoints;

    private final RequestMatcher protectedPaths;

    private final Filter tokenAuthenticationFilter;

    public TokenSecurityConfiguration(@Value("${talend.security.token.value:}") String token,
            @Autowired List<TokenProtectedPath> additionalProtectedEndpoints) {
        this.additionalProtectedEndpoints = additionalProtectedEndpoints;
        final AntPathRequestMatcher[] matchers = additionalProtectedEndpoints.stream() //
                .map(TokenProtectedPath::getProtectedPath) //
                .map(AntPathRequestMatcher::new) //
                .toArray(AntPathRequestMatcher[]::new);
        protectedPaths = new OrRequestMatcher(new OrRequestMatcher(matchers), toAnyEndpoint());
        if (StringUtils.isBlank(token)) {
            LOGGER.info("No token configured, protected endpoints are unavailable.");
            tokenAuthenticationFilter = new NoConfiguredTokenFilter(protectedPaths);
        } else {
            LOGGER.info("Configured token-based access security.");
            tokenAuthenticationFilter = new TokenAuthenticationFilter(token, protectedPaths);
        }
    }

    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .requestMatcher(protectedPaths)//
                .csrf() //
                .disable() //
                .authorizeRequests();
        for (TokenProtectedPath protectedPath : additionalProtectedEndpoints) {
            registry = registry.antMatchers(protectedPath.getProtectedPath()).hasRole(TokenAuthentication.ROLE);
        }
        // Configure actuator
        final PathMappedEndpoint prometheus = actuatorEndpoints.getEndpoint(EndpointId.of("prometheus"));
        final PathMappedEndpoint health = actuatorEndpoints.getEndpoint(EndpointId.of("health"));
        for (PathMappedEndpoint actuatorEndpoint : actuatorEndpoints) {
            final String rootPath = actuatorEndpoint.getRootPath();
            final boolean enforceTokenUsage;

            if (actuatorEndpoint.equals(health)) {
                enforceTokenUsage = false;
            } else if (allowPrometheusUnauthenticatedAccess && actuatorEndpoint.equals(prometheus)) {
                enforceTokenUsage = false;
                LOGGER.info("======= ALLOW UNAUTHENTICATED ACCESS TO PROMETHEUS (DEV MODE ONLY!) =======");
            } else {
                enforceTokenUsage = true;
            }

            final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl matcher = registry
                    .antMatchers(webEndpointProperties.getBasePath() + "/" + rootPath + "/**");
            if (enforceTokenUsage) {
                registry = matcher.hasRole(TokenAuthentication.ROLE);
            } else {
                registry = matcher.permitAll();
            }
        }
        registry.and().addFilterAfter(tokenAuthenticationFilter, BasicAuthenticationFilter.class);
    }

    public static class TokenSecurityCondition extends AllNestedConditions {

        // do not add new spring security filter chain if not additional endpoint is configured
        public TokenSecurityCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        public TokenSecurityCondition(ConfigurationPhase configurationPhase) {
            super(configurationPhase);
        }

        @ConditionalOnBean(PathMappedEndpoints.class)
        static class conditionalPathMappedEndpoints {

        }

        @ConditionalOnBean(TokenProtectedPath.class)
        static class conditionalTokenProtectedPath {

        }
    }
}