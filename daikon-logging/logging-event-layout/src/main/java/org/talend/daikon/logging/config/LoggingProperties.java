package org.talend.daikon.logging.config;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton reading and exposing logging properties
 */
public class LoggingProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProperties.class);

    private static final String CONFIG_PROPERTIES_FILE = "logging.properties";

    private static final LoggingProperties INSTANCE = new LoggingProperties();

    private final Properties properties;

    private LoggingProperties() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(CONFIG_PROPERTIES_FILE));
        } catch (IOException e) {
            LOGGER.error("Config properties file can't be read", e);
        }
    }

    private static LoggingProperties getInstance() {
        return INSTANCE;
    }

    /**
     * Return the property corresponding to a given key or null if the key doesn't exist
     * 
     * @param key Given key
     * @return Corresponding property value
     */
    public static String get(String key) {
        return getInstance().properties.getProperty(key);
    }
}
