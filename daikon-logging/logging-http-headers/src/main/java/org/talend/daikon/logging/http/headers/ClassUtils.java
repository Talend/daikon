package org.talend.daikon.logging.http.headers;

/**
 *
 */
class ClassUtils {

    private ClassUtils() {
    }

    static boolean isSpringAvailable() {
        try {
            Class.forName("org.springframework.web.context.request.async.WebAsyncManager");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
