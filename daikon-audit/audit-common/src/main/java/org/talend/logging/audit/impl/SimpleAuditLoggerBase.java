package org.talend.logging.audit.impl;

import org.talend.logging.audit.Context;
import org.talend.logging.audit.ContextBuilder;
import org.talend.logging.audit.LogLevel;

public class SimpleAuditLoggerBase implements AuditLoggerBase {

    private static final String SYSPROP_CONFIG_FILE = "talend.logging.audit.config";

    private static final String KAFKA_BACKEND = "org.talend.logging.audit.kafka.KafkaBackend";

    private final AbstractBackend backend;

    public SimpleAuditLoggerBase() {
        this(loadConfig());
    }

    public SimpleAuditLoggerBase(AuditConfigurationMap externalConfig) {
        final AuditConfigurationMap config = new AuditConfigurationMapImpl(externalConfig);

        final Backends backend = AuditConfiguration.BACKEND.getValue(config, Backends.class);
        switch (backend) {
        case AUTO:
            if (Utils.isKafkaPresent()) {
                this.backend = loadBackend(KAFKA_BACKEND, config);
            } else {
                throw new IllegalArgumentException("Selected backend is AUTO and no suitable backends found");
            }
            break;

        case KAFKA:
            if (!Utils.isKafkaPresent()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.backend = loadBackend(KAFKA_BACKEND, config);
            break;

        default:
            throw new IllegalArgumentException("Unsupported backend " + backend);
        }
    }

    private static AuditConfigurationMap loadConfig() {
        final String confPath = System.getProperty(SYSPROP_CONFIG_FILE);
        if (confPath != null) {
            return AuditConfiguration.loadFromFile(confPath);
        } else {
            return AuditConfiguration.loadFromClasspath("/audit.properties");
        }
    }

    private static AbstractBackend loadBackend(String className, AuditConfigurationMap config) {
        try {
            final Class<?> clz = Class.forName(className);
            return (AbstractBackend) clz.getConstructor(AuditConfigurationMap.class).newInstance(config);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to load backend " + className, e);
        }
    }

    @Override
    public void log(LogLevel level, String category, Context context, Throwable throwable, String message) {
        Context actualContext = context == null ? ContextBuilder.emptyContext() : ContextBuilder.create(context).build();
        backend.setNewContext(actualContext);

        this.backend.log(category, level, message, throwable);
    }
}
