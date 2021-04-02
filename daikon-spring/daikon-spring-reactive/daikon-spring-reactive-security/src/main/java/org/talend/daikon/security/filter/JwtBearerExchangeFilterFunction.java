package org.talend.daikon.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Exchange filter dealing with JWT Bearer authorization
 */
public final class JwtBearerExchangeFilterFunction extends AuthorizationExchangeFilterFunction {

    @Override
    protected String generateAuthorizationToken(Authentication authentication) {
        if (authentication.getCredentials() instanceof Jwt) {
            return "Bearer " + ((Jwt) authentication.getCredentials()).getTokenValue();
        }
        return null;
    }
}
