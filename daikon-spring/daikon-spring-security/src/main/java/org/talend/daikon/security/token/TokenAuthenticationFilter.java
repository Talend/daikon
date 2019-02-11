package org.talend.daikon.security.token;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

/**
 * <p>
 * A {@link GenericFilterBean} implementation that looks for HTTP header <i>Authorization</i> and for the token value in
 * header's value.
 * </p>
 * <p>
 * Token value <b>must</b> be prefixed by "Talend ". When authentication is successful {@link TokenAuthentication} is
 * set for current {@link SecurityContextHolder security context}.
 * </p>
 * <p>
 * For security purposes, the token value ({@link #hashedValue}) is not stored in plain text but using a hash to prevent
 * issues if one gets access to heap dump remotely.
 * </p>
 * 
 * @see TokenSecurityConfiguration
 */
class TokenAuthenticationFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private static final String PREFIX = "Talend ";

    private static final String TOKEN_HTTP_HEADER = "Authorization";

    private final String hashedValue;

    private final RequestMatcher protectedPaths;

    TokenAuthenticationFilter(String value, RequestMatcher protectedPaths) {
        this.hashedValue = sha256Hex(value);
        this.protectedPaths = protectedPaths;
    }

    private boolean authenticate(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            return false;
        }
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final String headerValue = servletRequest.getHeader(TOKEN_HTTP_HEADER);
        if (headerValue == null) {
            return false;
        } else if (!headerValue.startsWith(PREFIX) || headerValue.length() <= PREFIX.length()) {
            LOGGER.debug("Header value has not expected format ('{}<token>')", PREFIX);
            return false;
        } else {
            final String hashedHeaderValue = sha256Hex(headerValue.substring(PREFIX.length()));
            return hashedValue.equals(hashedHeaderValue);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        final boolean matches = protectedPaths.matches((HttpServletRequest) servletRequest);
        if (matches) {
            final SecurityContext context = SecurityContextHolder.getContext();
            final String path = ((HttpServletRequest) servletRequest).getServletPath();
            if (authenticate(servletRequest)) {
                LOGGER.trace("Request to {} successfully authenticated.", path);
                context.setAuthentication(TokenAuthentication.INSTANCE);
            } else {
                LOGGER.trace("Request to {} denied.", path);
                context.setAuthentication(null);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
