package org.talend.daikon.spring.audit.logs.api;

public interface AuditUserProvider {

    String getUserId();

    String getUsername();

    String getUserEmail();

    String getAccountId();
}
