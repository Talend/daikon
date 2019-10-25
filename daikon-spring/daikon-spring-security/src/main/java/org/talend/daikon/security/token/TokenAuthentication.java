package org.talend.daikon.security.token;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * <p>
 * A {@link Authentication} implementation dedicated to authenticate a user identified through
 * {@link TokenAuthenticationFilter}.
 * </p>
 * <p>
 * Implementation is immutable and a singleton.
 * </p>
 * 
 * @see TokenAuthentication#INSTANCE
 */
class TokenAuthentication implements Authentication {

    static final Authentication INSTANCE = new TokenAuthentication();

    static final String ADMIN_TOKEN_AUTHENTICATION = "ADMIN TOKEN AUTHENTICATION";

    static final String ROLE = "ACTUATOR";

    private final List<? extends GrantedAuthority> authorities;

    private TokenAuthentication() {
        this.authorities = Stream.of("ROLE_" + ROLE) //
                .map(s -> (GrantedAuthority) s::trim) //
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return getName();
    }

    @Override
    public Object getPrincipal() {
        return getName();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        if (!b) {
            throw new IllegalArgumentException("Unable to make this authentication not authenticated.");
        }
    }

    @Override
    public String getName() {
        return ADMIN_TOKEN_AUTHENTICATION;
    }
}
