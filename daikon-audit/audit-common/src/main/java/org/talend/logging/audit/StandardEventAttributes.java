package org.talend.logging.audit;

/**
 * This interface contains collection of standard attributes which can be logged with audit events. For example:
 *
 * <pre>
 * Context ctx = ContextBuilder.create(StandardEventAttributes.USER_NAME, "testuser").build();
 * AuditLoggerFactory.getEventAuditLogger().loginSuccess(ctx, "User {user} has logged in");
 * </pre>
 */
public interface StandardEventAttributes {

    String USER_NAME = "username";
}
