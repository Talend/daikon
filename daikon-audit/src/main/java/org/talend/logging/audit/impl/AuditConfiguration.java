package org.talend.logging.audit.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.talend.logging.audit.AuditLoggingException;

/**
 *
 */
enum AuditConfiguration {
    ROOT_LOGGER("root.logger", String.class, "audit"),
    APPLICATION_NAME("application.name", String.class),
    SERVICE_NAME("service.name", String.class, ""),
    INSTANCE_NAME("instance.name", String.class, ""),
    APPENDER_FILE_PATH("appender.file.path", String.class),
    APPENDER_FILE_MAXSIZE("appender.file.maxsize", Long.class, 52428800L),
    APPENDER_FILE_MAXBACKUP("appender.file.maxbackup", Integer.class, 20),
    LOCATION("location", Boolean.class, Boolean.FALSE),
    APPENDER_SOCKET_HOST("appender.socket.host", String.class, "localhost"),
    APPENDER_SOCKET_PORT("appender.socket.port", Integer.class, 4560),
    APPENDER_CONSOLE_PATTERN("appender.console.pattern", String.class, "%d{yyyy-MM-dd HH:mm:ss} %-5p %c - %m%n"),
    APPENDER_CONSOLE_TARGET("appender.console.target", LogTarget.class, LogTarget.OUTPUT),
    LOG_APPENDER("log.appender", LogAppendersSet.class);

    private static final String PLACEHOLDER_START = "${";

    private static final String PLACEHOLDER_END = "}";

    private static final String PLACEHOLDER_DELIM = ":";

    private final String property;

    private final Class<?> clz;

    private final Object defaultValue;

    private Object value;

    private boolean alreadySet;

    <T> AuditConfiguration(String property, Class<T> clz) {
        this(property, clz, null);
    }

    <T> AuditConfiguration(String property, Class<T> clz, Object defaultValue) {
        this.property = property;
        this.clz = clz;
        this.defaultValue = defaultValue;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getClz() {
        return clz;
    }

    boolean getAlreadySet() {
        return this.alreadySet;
    }

    void setAlreadySet(boolean alreadySet) {
        this.alreadySet = alreadySet;
    }

    public String getString() {
        return getValue(String.class);
    }

    public Integer getInteger() {
        return getValue(Integer.class);
    }

    public Long getLong() {
        return getValue(Long.class);
    }

    public Boolean getBoolean() {
        return getValue(Boolean.class);
    }

    public Object getValue() {
        return value;
    }

    public <T> T getValue(Class<T> clz) {
        if (!this.clz.equals(clz)) {
            throw new IllegalArgumentException("Wrong class type " + clz.getName() + ", expected " + this.clz.getName());
        }
        if (!alreadySet && defaultValue == null) {
            throw new IllegalStateException("Value for property " + property + " is not set and it has no default value");
        }
        return alreadySet ? clz.cast(value) : clz.cast(defaultValue);
    }

    public <T> void setValue(T value, Class<? extends T> clz) {
        if (!this.clz.equals(clz)) {
            throw new IllegalArgumentException("Wrong class type " + clz.getName() + ", expected " + this.clz.getName());
        }

        if (alreadySet) {
            throw new IllegalStateException("Parameter " + property + " cannot be set twice");
        }

        this.alreadySet = true;
        this.value = value;
    }

    public static void loadFromFile(String fileName) {
        final Properties props = new Properties();

        try (InputStream in = Files.newInputStream(Paths.get(fileName))) {
            props.load(in);
        } catch (IOException e) {
            throw new AuditLoggingException(e);
        }

        loadFromProperties(props);
    }

    public static void loadFromClasspath(String fileName) {
        final Properties props = new Properties();

        try (InputStream in = AuditConfiguration.class.getResourceAsStream(fileName)) {
            props.load(in);
        } catch (IOException e) {
            throw new AuditLoggingException(e);
        }

        loadFromProperties(props);
    }

    @SuppressWarnings({ "unchecked" })
    public static void loadFromProperties(Properties props) {
        for (String key : props.stringPropertyNames()) {
            String rawValue = props.getProperty(key).trim();
            String value = replaceTokens(rawValue);

            AuditConfiguration prop = AuditConfiguration.valueOf(getEnumName(key));

            if (String.class.equals(prop.getClz())) {
                prop.setValue(value, String.class);
            } else if (Integer.class.equals(prop.getClz())) {
                prop.setValue(Integer.valueOf(value), Integer.class);
            } else if (Long.class.equals(prop.getClz())) {
                prop.setValue(Long.valueOf(value), Long.class);
            } else if (Boolean.class.equals(prop.getClz())) {
                prop.setValue(Boolean.valueOf(value), Boolean.class);
            } else if (Enum.class.isAssignableFrom(prop.getClz())) {
                Class<? extends Enum> enumClz = (Class<? extends Enum>) prop.getClz();
                prop.setValue(Enum.valueOf(enumClz, value.toUpperCase()), enumClz);
            } else if (LogAppendersSet.class.isAssignableFrom(prop.getClz())) {
                String[] parts = value.split(",");
                LogAppendersSet appenders = new LogAppendersSet();
                for (String app : parts) {
                    appenders.add(Enum.valueOf(LogAppenders.class, app.toUpperCase()));
                }
                prop.setValue(appenders, LogAppendersSet.class);
            } else {
                throw new IllegalArgumentException("Unsupported property type " + prop.getClz().getName());
            }
        }

        // some fields are mandatory or optional based on other config options
        //        validateConfiguration();
    }

    private static String getEnumName(String propertyName) {
        return propertyName.toUpperCase().replace('.', '_');
    }

    private static void validateConfiguration() {
        List<String> missingFields = null;

        for (AuditConfiguration conf : AuditConfiguration.values()) {
            if (!conf.alreadySet && conf.defaultValue == null) {
                if (missingFields == null) {
                    missingFields = new ArrayList<>();
                }
                missingFields.add(conf.property);
            }
        }

        if (missingFields != null && !missingFields.isEmpty()) {
            throw new IllegalArgumentException("These mandatory audit logging properties were not configured: " + missingFields);
        }
    }

    private static String replaceTokens(String value) {
        String answer = value;
        Set<PropertyToken> tokens = getTokens(value);

        for (PropertyToken tok : tokens) {
            String tokenValue = System.getProperty(tok.tokenName);
            if (tokenValue == null) {
                tokenValue = System.getenv(getEnvVariableName(tok.tokenName));
            }
            if (tokenValue == null) {
                tokenValue = tok.defaultValue;
            }
            if (tokenValue == null) {
                throw new AuditLoggingException("Cannot resolve placeholder " + tok.placeholder);
            }

            answer = answer.replace(tok.placeholder, tokenValue);
        }

        return answer;
    }

    private static Set<PropertyToken> getTokens(String value) {
        final Set<PropertyToken> answer = new LinkedHashSet<>();

        int i = value.indexOf(PLACEHOLDER_START);
        while (i != -1) {
            int e = value.indexOf(PLACEHOLDER_END, i);
            if (e == -1) {
                break;
            }

            final String placeholder = value.substring(i, e + 1);

            String tok = placeholder.substring(PLACEHOLDER_START.length(), placeholder.length() - PLACEHOLDER_END.length());
            int d = tok.indexOf(PLACEHOLDER_DELIM, i);

            PropertyToken propertyToken = new PropertyToken();
            propertyToken.placeholder = placeholder;

            if (d == -1 || d >= e) {
                propertyToken.tokenName = tok;
            } else {
                propertyToken.tokenName = tok.substring(0, d);
                propertyToken.defaultValue = tok.substring(d + 1);
            }

            answer.add(propertyToken);

            i = value.indexOf(PLACEHOLDER_START, e);
        }

        return answer;
    }

    private static String getEnvVariableName(String token) {
        return token.toUpperCase().replace('.', '_');
    }

    private static class PropertyToken {

        private String placeholder;

        private String tokenName;

        private String defaultValue;

        @Override
        public String toString() {
            return "PropertyToken: " + placeholder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            PropertyToken that = (PropertyToken) o;
            return Objects.equals(placeholder, that.placeholder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(placeholder);
        }
    }
}
