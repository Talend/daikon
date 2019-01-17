package org.talend.daikon.logging.http.headers;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;

/**
 *
 */
class SpringUtils {

    private static final String INTERCEPTOR_KEY = HttpHeadersMDCInterceptor.class.getName();

    private SpringUtils() {
    }

    static void setAsyncMDCInterceptor(HttpServletRequest request, boolean replaceRemoteAddrWithForwardedFor) {
        WebAsyncManager manager = WebAsyncUtils.getAsyncManager(request);
        if (manager.getCallableInterceptor(INTERCEPTOR_KEY) == null) {
            manager.registerCallableInterceptor(INTERCEPTOR_KEY,
                    new HttpHeadersMDCInterceptor(replaceRemoteAddrWithForwardedFor));
        }
    }

    private static class HttpHeadersMDCInterceptor implements CallableProcessingInterceptor {

        private final boolean replaceRemoteAddrWithForwardedFor;

        private HttpHeadersMDCInterceptor(boolean replaceRemoteAddrWithForwardedFor) {
            this.replaceRemoteAddrWithForwardedFor = replaceRemoteAddrWithForwardedFor;
        }

        @Override
        public <T> void preProcess(NativeWebRequest request, Callable<T> task) {
            MDCUtils.fillMDC(request.getNativeRequest(HttpServletRequest.class), replaceRemoteAddrWithForwardedFor);
        }

        @Override
        public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) {
            MDCUtils.cleanMDC();
        }
    }
}
