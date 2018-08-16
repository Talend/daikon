package org.talend.logging.audit;

/**
 * Simple API interface for audit logging. An instance of this interface should be obtained with
 * {@link AuditLoggerFactory#getAuditLogger()}.
 *
 * <p>
 * It contains two kinds methods that log audit messages. Very basic info/worning/error methods, which accept
 * all event data as parameters. There are also methods like activityInfo which infers category from method name.
 *
 * <p>
 * It can be extended by a custom interface which can add methods whose
 * declarations follow the next schema:
 * 
 * <pre>
 * 
 * void categoryLevel(Context context, Throwable throwable, String message);
 * </pre>
 * <p>
 * Context and throwable are optional. See similar methods in this interface for example.
 *
 * @see AuditLoggerFactory
 */
public interface AuditLogger {

    void info(String category, String message);

    void info(String category, Context context, String message);

    void info(String category, Throwable throwable, String message);

    void info(String category, Context context, Throwable throwable, String message);

    void warning(String category, String message);

    void warning(String category, Context context, String message);

    void warning(String category, Context context, Throwable throwable, String message);

    void error(String category, String message);

    void error(String category, Context context, String message);

    void error(String category, Context context, Throwable throwable, String message);

    // ----------------------------------------------------------------------

    void activityInfo(String message);

    void activityInfo(Context context, String message);

    void activityWarning(String message);

    void activityWarning(Context context, String message);

    void activityError(String message);

    void activityError(Context context, String message);

    // ----------------------------------------------------------------------

    void securityInfo(String message);

    void securityInfo(Context context, String message);

    void securityWarning(String message);

    void securityWarning(Context context, String message);

    void securityError(String message);

    void securityError(Context context, String message);
}
