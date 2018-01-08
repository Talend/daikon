package org.talend.logging.audit.impl;

/**
 *
 */
public class DefaultAuditLoggerBase extends AbstractAuditLoggerBase {

    private static final String SYSPROP_CONFIG_FILE = "talend.logging.audit.config";

    private final ContextEnricher contextEnricher;

    private final AbstractBackend logger;

    private static AuditConfigurationMap loadConfig() {
        final String confPath = System.getProperty(SYSPROP_CONFIG_FILE);
        if (confPath != null) {
            return AuditConfiguration.loadFromFile(confPath);
        } else {
            return AuditConfiguration.loadFromClasspath("/audit.properties");
        }
    }

    public DefaultAuditLoggerBase() {
        this(loadConfig());
    }

    public DefaultAuditLoggerBase(AuditConfigurationMap externalConfig) {
        AuditConfigurationMap config = new AuditConfigurationMapImpl(externalConfig);
        this.contextEnricher = new ContextEnricher(config);

        final Backends backend = AuditConfiguration.BACKEND.getValue(config, Backends.class);
        switch (backend) {
        case AUTO:
            if (Utils.isLogbackPresent()) {
                this.logger = new LogbackBackend(config);
            } else if (Utils.isSlf4jPresent()) {
                this.logger = new Slf4jBackend(config);
            } else if (Utils.isLog4j1Present()) {
                this.logger = new Log4j1Backend(config);
            } else {
                throw new IllegalArgumentException("Selected backend is AUTO and no suitable backends found");
            }
            break;

        case LOGBACK:
            if (!Utils.isLogbackPresent()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.logger = new LogbackBackend(config);
            break;

        case SLF4J:
            if (!Utils.isSlf4jPresent()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.logger = new Slf4jBackend(config);
            break;

        case LOG4J1:
            if (!Utils.isLog4j1Present()) {
                throw new IllegalArgumentException("Selected backend is " + backend + " and it is not available on classpath");
            }
            this.logger = new Log4j1Backend(config);
            break;

        default:
            throw new IllegalArgumentException("Unsupported backend " + backend);
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
}
