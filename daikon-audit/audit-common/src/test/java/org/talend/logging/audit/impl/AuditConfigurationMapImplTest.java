package org.talend.logging.audit.impl;

import static org.junit.Assert.assertEquals;
import static org.talend.logging.audit.impl.AuditConfiguration.APPENDER_FILE_PATH;
import static org.talend.logging.audit.impl.AuditConfiguration.APPLICATION_NAME;
import static org.talend.logging.audit.impl.AuditConfiguration.LOG_APPENDER;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class AuditConfigurationMapImplTest {

    private Properties properties = new Properties();

    @Before
    public void setUp() {
        setProperty(LOG_APPENDER, "none");
        setProperty(APPENDER_FILE_PATH, "/tmp");
        setProperty(APPLICATION_NAME, "App name");
    }

    @Test
    public void validateConfigurationWithValideOne() {
        AuditConfigurationMap config = AuditConfiguration.loadFromProperties(properties);

        config.validateConfiguration();
    }

    @Test
    public void validateConfigurationNotRequiredFilePath() {
        setProperty(APPENDER_FILE_PATH, null);

        AuditConfigurationMap config = AuditConfiguration.loadFromProperties(properties);
        config.validateConfiguration();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateConfigurationRequiredFilePath() {
        setProperty(LOG_APPENDER, "file");
        setProperty(APPENDER_FILE_PATH, null);

        AuditConfigurationMap config = AuditConfiguration.loadFromProperties(properties);
        config.validateConfiguration();
    }

    @Test
    public void toPropertyTest() {
        String property = toProperty(APPENDER_FILE_PATH);
        assertEquals("appender.file.path", property);
    }

    private void setProperty(AuditConfiguration config, String value) {
        String key = toProperty(config);
        if ( value == null ) {
            properties.remove(key);
        } else {
            properties.setProperty(key, value);
        }
    }

    private String toProperty(AuditConfiguration config) {
        return config.name().toLowerCase().replaceAll("_", ".");
    }

}