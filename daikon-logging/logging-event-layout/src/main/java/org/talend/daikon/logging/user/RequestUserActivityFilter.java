package org.talend.daikon.logging.user;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.daikon.logging.event.field.MdcKeys;

/**
 * Checks for atcivity id in the header.
 * If it doesn't exist, establishes a new id.
 * 
 * @author sdiallo
 */
public class RequestUserActivityFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUserActivityFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // NoOp
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String userActivityId = httpServletRequest.getHeader(MdcKeys.HEADER_REQUEST_USER_ACTIVITY_ID);

        if (StringUtils.isEmpty(userActivityId)) {
            userActivityId = UUID.randomUUID().toString();
        }

        RequestUserActivityContext.getCurrent().setCorrelationId(userActivityId);
        MDC.put(MdcKeys.USER_ACTIVITY_ID, userActivityId);
        LOGGER.debug("userActivityId ={} request={}", userActivityId, httpServletRequest.getPathInfo());

        try {
            chain.doFilter(httpServletRequest, response);
        } finally {
            MDC.remove(MdcKeys.USER_ACTIVITY_ID);
            RequestUserActivityContext.clearCurrent();
        }

    }

    @Override
    public void destroy() {
        // NoOp
    }

}