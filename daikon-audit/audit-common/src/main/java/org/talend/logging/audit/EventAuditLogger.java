package org.talend.logging.audit;

/**
 * Event-based audit logging API interface.
 *
 * <p>
 * An application which wants to implement event-based audit logging has to create a sub-interface from
 * {@link EventAuditLogger} and define the events there. Each method represents a single event, and
 * should be annotated with {@link AuditEvent} annotations. For example:
 * 
 * <pre>
 *     public interface TestAuditLogger extends EventAuditLogger {
 *        {@literal @}AuditEvent(category = "test", message = "Test message")
 *         void test();
 *     }
 * </pre>
 *
 * <p>
 * To log this audit event:
 * 
 * <pre>
 *     TestAuditLogger logger = AuditLoggerFactory.getEventAuditLogger(TestAuditLogger.class);
 *     ...
 *     logger.test();
 * </pre>
 *
 * @see AuditLoggerFactory
 * @see StandardEventAuditLogger
 */
public interface EventAuditLogger {

}
