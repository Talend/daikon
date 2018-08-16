package org.talend.logging.audit.impl;

import org.talend.logging.audit.LogLevel;

/**
 *
 */
public class EventDefinition {

    private final String name;

    private String category;

    private LogLevel logLevel;

    private String message;

    public EventDefinition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void set(String property, String value) {
        switch (property.toLowerCase()) {
        case "level":
            logLevel = LogLevel.valueOf(value.toUpperCase());
            break;

        case "category":
            category = value.toLowerCase();
            break;

        case "message":
            message = value;
            break;

        default:
            throw new IllegalArgumentException("Unknown property " + property);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDefinition that = (EventDefinition) o;

        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
