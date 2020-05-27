package org.talend.daikon.spring.audit.logs.api;

/**
 * Audit log scope indicating if audit logs must be generated for :
 * - ALL cases
 * - SUCCESS cases only
 * - ERROR cases only
 */
public enum AuditLogScope {
    ALL,
    SUCCESS,
    ERROR
}
