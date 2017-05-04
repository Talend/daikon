package org.talend.daikon.logging.correlate;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.daikon.logging.event.field.MdcKeys;

/**
 * Checks for correlation id in the header.
 * If it doesn't exist,  establishes a new correlation id.
 * @author sdiallo
 */
public class RequestCorrelationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCorrelationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // NoOp
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String correlationId = httpServletRequest.getHeader(MdcKeys.HEADER_REQUEST_CORRELATION_ID);

        if (StringUtils.isEmpty(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        RequestCorrelationContext.getCurrent().setCorrelationId(correlationId);
        MDC.put(MdcKeys.CORRELATION_ID, correlationId);
        LOGGER.debug("Correlation Id={} request={}", correlationId, httpServletRequest.getPathInfo());

        try {
            chain.doFilter(httpServletRequest, response);
        } finally {
            MDC.remove(MdcKeys.CORRELATION_ID);
            RequestCorrelationContext.clearCurrent();
        }

    }

    @Override
    public void destroy() {
        // NoOp
    }

}