package org.talend.daikon.logging.http.headers;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

/**
 *
 */
class MDCUtils {

    private static final String USER_AGENT = "User-Agent";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private MDCUtils() {
    }

    static void fillMDC(HttpServletRequest request, boolean replaceRemoteAddrWithForwardedFor) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (replaceRemoteAddrWithForwardedFor && forwardedFor != null) {
            putInMDCIfNotNull(HttpHeadersMDC.HTTP_REMOTE_ADDR, forwardedFor);
        } else {
            putInMDCIfNotNull(HttpHeadersMDC.HTTP_REMOTE_ADDR, request.getRemoteAddr());
        }
        putInMDCIfNotNull(HttpHeadersMDC.HTTP_REMOTE_PORT, request.getRemotePort());
        putInMDCIfNotNull(HttpHeadersMDC.HTTP_REMOTE_USER, request.getRemoteUser());
        putInMDCIfNotNull(HttpHeadersMDC.HTTP_USER_AGENT, toString(request.getHeaders(USER_AGENT)));
        putInMDCIfNotNull(HttpHeadersMDC.HTTP_FORWARDED_FOR, toString(request.getHeaders(X_FORWARDED_FOR)));
    }

    static void cleanMDC() {
        MDC.remove(HttpHeadersMDC.HTTP_REMOTE_ADDR);
        MDC.remove(HttpHeadersMDC.HTTP_REMOTE_PORT);
        MDC.remove(HttpHeadersMDC.HTTP_REMOTE_USER);
        MDC.remove(HttpHeadersMDC.HTTP_USER_AGENT);
        MDC.remove(HttpHeadersMDC.HTTP_FORWARDED_FOR);
    }

    private static void putInMDCIfNotNull(String key, Object value) {
        if (value != null) {
            MDC.put(key, value.toString());
        }
    }

    private static String toString(Enumeration<String> enumr) {
        List<String> list = Collections.list(enumr);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        }
        return list.toString();
    }
}
