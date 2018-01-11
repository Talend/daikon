package org.talend.daikon.logging.http.headers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 */
public class HttpHeadersMDCFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHeadersMDCFilter.class);

    private static final String REPLACE_PARAM_NAME = "replaceRemoteAddrWithForwardedFor";

    /** If <code>true</code> the remote address MDC value will be filled with X-Forwarded-For value (if available) */
    private boolean replaceRemoteAddrWithForwardedFor;

    public boolean isReplaceRemoteAddrWithForwardedFor() {
        return replaceRemoteAddrWithForwardedFor;
    }

    public void setReplaceRemoteAddrWithForwardedFor(boolean replaceRemoteAddrWithForwardedFor) {
        this.replaceRemoteAddrWithForwardedFor = replaceRemoteAddrWithForwardedFor;
        LOG.debug("Setting {} to {} via setter", REPLACE_PARAM_NAME, replaceRemoteAddrWithForwardedFor);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("Initializing filter {}", filterConfig.getFilterName());

        final String param1Value = filterConfig.getInitParameter(REPLACE_PARAM_NAME);
        if (param1Value != null) {
            setReplaceRemoteAddrWithForwardedFor(Boolean.parseBoolean(param1Value));
            LOG.debug("Setting {} to {} via servlet parameter", REPLACE_PARAM_NAME, param1Value);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            Utils.fillMDC((HttpServletRequest) request, replaceRemoteAddrWithForwardedFor);
        } else {
            LOG.debug("Unsupported request type {}", request.getClass().getName());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            Utils.cleanMDC();
        }
    }

    @Override
    public void destroy() {
    }
}
