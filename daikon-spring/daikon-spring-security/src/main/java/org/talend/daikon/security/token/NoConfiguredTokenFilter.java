package org.talend.daikon.security.token;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

/**
 * <p>
 * A implementation of {@link GenericFilterBean} that returns HTTP 503 status for all the paths it matches.
 * </p>
 * <p>
 * This filter is used to indicate to caller that the URL is protected by token authentication <b>but</b> service did
 * not declare the token value.
 * </p>
 * 
 * @see TokenSecurityConfiguration
 */
class NoConfiguredTokenFilter extends GenericFilterBean {

    private RequestMatcher protectedPaths;

    NoConfiguredTokenFilter(RequestMatcher protectedPaths) {
        this.protectedPaths = protectedPaths;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final boolean matches = protectedPaths.matches((HttpServletRequest) req);
        if (matches) {
            ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            chain.doFilter(req, res);
        }
    }

}
