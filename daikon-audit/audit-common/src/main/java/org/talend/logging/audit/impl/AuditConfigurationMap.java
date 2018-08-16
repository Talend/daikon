package org.talend.logging.audit.impl;

import java.util.Map;

/**
 *
 */
public interface AuditConfigurationMap extends Map<AuditConfiguration, Object> {

    String getString(AuditConfiguration config);

    Integer getInteger(AuditConfiguration config);

    Long getLong(AuditConfiguration config);

    Boolean getBoolean(AuditConfiguration config);

    boolean isValid(AuditConfiguration config);

    Object getValue(AuditConfiguration config);

    <T> T getValue(AuditConfiguration config, Class<T> clz);

    <T> void setValue(AuditConfiguration config, T value, Class<? extends T> clz);

    void validateConfiguration();
}
