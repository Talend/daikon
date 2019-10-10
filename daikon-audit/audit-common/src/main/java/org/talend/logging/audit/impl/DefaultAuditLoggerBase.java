package org.talend.logging.audit.impl;

/**
 *
 */
public class DefaultAuditLoggerBase extends AbstractAuditLoggerBase {

    private static final String SYSPROP_CONFIG_FILE = "talend.logging.audit.config";

    private static final String LOG4J1_BACKEND = "org.talend.logging.audit.log4j1.Log4j1Backend";

    private static final String LOG4J2_BACKEND = "org.talend.logging.audit.log4j2.Log4j2Backend";

    private static final String LOGBACK_BACKEND = "org.talend.logging.audit.logback.LogbackBackend";

    private final ContextEnricher contextEnricher;

    private final AbstractBackend logger;

    public DefaultAuditLoggerBase() {
        this(loadConfig());
    }

    public DefaultAuditLoggerBase(AuditConfigurationMap externalConfig) {
        final AuditConfigurationMap config = new AuditConfigurationMapImpl(externalConfig);
        this.contextEnricher = new ContextEnricher(config);

        final Backends backend = AuditConfiguration.BACKEND.getValue(config, Backends.class);
        switch (backend) {
        case AUTO:
            if (Utils.isLogbackPresent()) {
                this.logger = loadBackend(LOGBACK_BACKEND, config);
            } else if (Utils.isLog4j1Present()) {
                this.logger = loadBackend(LOG4J1_BACKEND, config);
            } else if (Utils.isLog4j2Present()) {
                this.logger = loadBackend(LOG4J2_BACKEND, config);
            } else {
                throw new IllegalArgumentException("Selected backend is AUTO and no suitable backends found");
            }
            break;

        case LOGBACK:
            if (!Utils.isLogbackPresent()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.logger = loadBackend(LOGBACK_BACKEND, config);
            break;

        case LOG4J1:
            if (!Utils.isLog4j1Present()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.logger = loadBackend(LOG4J1_BACKEND, config);
            break;

        case LOG4J2:
            if (!Utils.isLog4j2Present()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.logger = loadBackend(LOG4J2_BACKEND, config);
            break;

        default:
            throw new IllegalArgumentException("Unsupported backend " + backend);
        }
    }

    public DefaultAuditLoggerBase(AbstractBackend backend, AuditConfigurationMap externalConfig) {
        final AuditConfigurationMap config = new AuditConfigurationMapImpl(externalConfig);
        this.contextEnricher = new ContextEnricher(config);
        this.logger = backend;
    }

    private static AuditConfigurationMap loadConfig() {
        final String confPath = System.getProperty(SYSPROP_CONFIG_FILE);
        if (confPath != null) {
            return AuditConfiguration.loadFromFile(confPath);
        } else {
            return AuditConfiguration.loadFromClasspath("/audit.properties");
        }
    }

    @Override
    protected ContextEnricher getEnricher() {
        return contextEnricher;
    }

    @Override
    protected AbstractBackend getLogger() {
        return logger;
    }

    private static AbstractBackend loadBackend(String className, AuditConfigurationMap config) {
        try {
            final Class<?> clz = Class.forName(className);
            return (AbstractBackend) clz.getConstructor(AuditConfigurationMap.class).newInstance(config);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to load backend " + className, e);
        }
    }
}
