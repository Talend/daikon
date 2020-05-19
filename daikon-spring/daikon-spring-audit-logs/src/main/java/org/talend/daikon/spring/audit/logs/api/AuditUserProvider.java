package org.talend.daikon.spring.audit.logs.api;

/**
 * Audit user provider interface
 * Give information about current user
 */
public interface AuditUserProvider {

    /**
     * Current user id
     * 
     * @return user id
     */
    String getUserId();

    /**
     * Current username
     * 
     * @return username
     */
    String getUsername();

    /**
     * Current user email
     * 
     * @return user email
     */
    String getUserEmail();

    /**
     * Current account id
     * 
     * @return account id
     */
    String getAccountId();
}
