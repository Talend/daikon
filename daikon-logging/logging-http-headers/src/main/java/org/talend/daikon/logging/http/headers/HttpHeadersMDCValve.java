package org.talend.daikon.logging.http.headers;

import javax.servlet.ServletException;
import java.io.IOException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HttpHeadersMDCValve extends ValveBase {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHeadersMDCValve.class);

    /** If <code>true</code> the remote address MDC value will be filled with X-Forwarded-For value (if available) */
    private boolean replaceRemoteAddrWithForwardedFor;

    public HttpHeadersMDCValve() {
        super(true);
    }

    public void setReplaceRemoteAddrWithForwardedFor(boolean replaceRemoteAddrWithForwardedFor) {
        this.replaceRemoteAddrWithForwardedFor = replaceRemoteAddrWithForwardedFor;
        LOG.debug("Setting replaceRemoteAddrWithForwardedFor to {}", replaceRemoteAddrWithForwardedFor);
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (request != null) {
            MDCUtils.fillMDC(request, replaceRemoteAddrWithForwardedFor);

            if (ClassUtils.isSpringAvailable()) {
                SpringUtils.setAsyncMDCInterceptor(request, replaceRemoteAddrWithForwardedFor);
            }
        }

        try {
            getNext().invoke(request, response);
        } finally {
            MDCUtils.cleanMDC();
        }
    }
}
