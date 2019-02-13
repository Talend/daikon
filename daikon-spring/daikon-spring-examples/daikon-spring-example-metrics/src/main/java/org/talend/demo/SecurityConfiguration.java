package org.talend.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * <p>
 * Disables Actuator security to ease sample application usage.
 * </p>
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Purpose is not show case security, allow all accesses
        http.authorizeRequests().antMatchers("/**").permitAll();

        // Disable CSRF to ease demo with POSTs requests
        http.cors().and().csrf().disable();
    }
}
