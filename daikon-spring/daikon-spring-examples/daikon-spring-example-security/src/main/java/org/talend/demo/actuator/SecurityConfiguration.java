package org.talend.demo.actuator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.talend.daikon.security.token.TokenProtectedPath;

/**
 * <p>
 * This configuration shows that application can define its own Spring Security configuration, independently from the
 * configuration specified in {@link org.talend.daikon.security.token.TokenSecurityConfiguration}.
 * </p>
 * <p>
 * There is no need to import {@link org.talend.daikon.security.token.TokenSecurityConfiguration} in application code,
 * auto configuration already does this.
 * </p>
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // By default, inherits from daikon-spring-security configuration (see this class documentation)

        // Expose "/say/hi" path without authentication, to show that application can define its own security rules.
        http.authorizeRequests().antMatchers("/say/hi").permitAll();
    }

    @Bean
    public TokenProtectedPath protectedSayHi() {
        return () -> "/say/secure/hi";
    }
}
