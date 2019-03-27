package org.talend.logging.audit.impl;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.talend.logging.audit.LogAppenders;

/**
 *
 */
public class AuditConfigurationMapImpl extends EnumMap<AuditConfiguration, Object> implements AuditConfigurationMap {

    public AuditConfigurationMapImpl() {
        super(AuditConfiguration.class);
    }

    public AuditConfigurationMapImpl(AuditConfigurationMap map) {
        super(map);
    }

    private static String getAppenderName(String configName) {
        final String appenderPrefix = "APPENDER_";

        if (!configName.startsWith(appenderPrefix)) {
            return null;
        }

        final int nameStart = appenderPrefix.length();
        final int nameEnd = configName.indexOf('_', nameStart);

        if (nameEnd == -1) {
            return null;
        }

        return configName.substring(nameStart, nameEnd);
    }

    public void validateConfiguration() {
        List<String> missingFields = null;

        LogAppendersSet appenders = getValue(AuditConfiguration.LOG_APPENDER, LogAppendersSet.class);
        if (appenders == null || appenders.isEmpty()) {
            throw new IllegalArgumentException("List of appenders is not configured");
        }

        for (AuditConfiguration conf : AuditConfiguration.values()) {
            if (!isValid(conf)) {
                String appenderName = getAppenderName(conf.name());
                if (appenderName != null && !appenders.contains(LogAppenders.valueOf(appenderName))) {
                    // don't validate appenders which are not configured
                    continue;
                }

                if (missingFields == null) {
                    missingFields = new ArrayList<>();
                }
                missingFields.add(conf.toString());
            }
        }

        if (missingFields != null && !missingFields.isEmpty()) {
            throw new IllegalArgumentException("These mandatory audit logging properties were not configured: " + missingFields);
        }
    }

    public String getString(AuditConfiguration config) {
        return getValue(config, String.class);
    }

    public Integer getInteger(AuditConfiguration config) {
        return getValue(config, Integer.class);
    }

    public Long getLong(AuditConfiguration config) {
        return getValue(config, Long.class);
    }

    public Boolean getBoolean(AuditConfiguration config) {
        return getValue(config, Boolean.class);
    }

    public boolean isValid(AuditConfiguration config) {
        return (getValue(config) != null || config.canBeNull());
    }

    public Object getValue(AuditConfiguration config) {
        return containsKey(config) ? get(config) : config.getDefaultValue();
    }

    public <T> T getValue(AuditConfiguration config, Class<T> clz) {
        if (!config.getClz().equals(clz)) {
            throw new IllegalArgumentException("Wrong class type " + clz.getName() + ", expected " + config.getClz().getName());
        }
        Object value = getValue(config);
        if (value == null && !config.canBeNull()) {
            throw new IllegalStateException(
                    "Value for property " + config.toString() + " is not set and it has no default value");
        }
        return clz.cast(value);
    }

    public <T> void setValue(AuditConfiguration config, T value, Class<? extends T> clz) {
        if (!config.getClz().equals(clz)) {
            throw new IllegalArgumentException("Wrong class type " + clz.getName() + ", expected " + config.getClz().getName());
        }

        if (containsKey(config)) {
            throw new IllegalStateException("Parameter " + config.toString() + " cannot be set twice");
        }

        put(config, value);
    }
}
