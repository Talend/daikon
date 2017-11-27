package org.talend.logging.audit;

/**
 * This interface provides set of standard events, considered generic enough to be useful for different applications.
 *
 * <p>If some events that application needs are missing here, it can create a custom java interface which extends this one
 * and define events similarly (see {@link EventAuditLogger} for details).
 *
 * <p>Any method can accept an instance of {@link Throwable} and/or an instance of {@link Context} as parameters.
 *
 * @see EventAuditLogger
 */
public interface StandardEventAuditLogger extends EventAuditLogger {

    @AuditEvent(category = "security", message = "User has logged in successfully")
    void loginSuccess(Object... args);

    @AuditEvent(category = "security", message = "User login attempt failed")
    void loginFail(Object... args);

    @AuditEvent(category = "security", message = "User has been locked out")
    void userLockout(Object... args);

    @AuditEvent(category = "security", message = "User has been created")
    void userCreated(Object... args);

    @AuditEvent(category = "security", message = "User has been modified")
    void userModified(Object... args);

    @AuditEvent(category = "security", message = "User has been deleted")
    void userDeleted(Object... args);

    @AuditEvent(category = "security", message = "User password has been changed")
    void passwordChanged(Object... args);

    @AuditEvent(category = "security", message = "User password has been reset")
    void passwordReset(Object... args);

    @AuditEvent(category = "security", message = "Role has been created")
    void roleCreated(Object... args);

    @AuditEvent(category = "security", message = "Role has been deleted")
    void roleDeleted(Object... args);

    @AuditEvent(category = "security", message = "Role has been assigned to user")
    void roleAssigned(Object... args);

    @AuditEvent(category = "security", message = "Role has been revoked from user")
    void roleRevoked(Object... args);

    @AuditEvent(category = "security", message = "Invalid user input")
    void invalidInput(Object... args);

    @AuditEvent(category = "security", message = "Invalid session")
    void invalidSession(Object... args);

    @AuditEvent(category = "failure", message = "Unexpected exception")
    void systemException(Throwable throwable, Object... args);
}
