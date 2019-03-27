// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.sandbox.properties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Implementation of {@link StandardPropertiesStrategy} for Sun/Oracle JVM (tested on 1.6)
 */
class SunOracleStandardPropertiesStrategy implements StandardPropertiesStrategy {

    private static final String SUN_PROPERTIES = "sun.conf"; //$NON-NLS-1$

    private static final String SUN_BOOT_CLASS_PATH = "sun.boot.class.path"; //$NON-NLS-1$

    private static final String JAVA_CLASS_PATH = "java.class.path"; //$NON-NLS-1$

    @Override
    public Properties getStandardProperties() {
        Properties properties = new Properties();
        try {
            InputStream resource = this.getClass().getResourceAsStream(SUN_PROPERTIES);
            if (resource == null) {
                throw new RuntimeException("Expected file '" + SUN_PROPERTIES + "' but wasn't found."); //$NON-NLS-1$ //$NON-NLS-2$
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource));
            {
                String line = bufferedReader.readLine();
                while (line != null) {
                    if (!line.startsWith("#")) { //$NON-NLS-1$ // Ignore comments in file
                        String systemProperty = System.getProperty(line);
                        if (systemProperty != null) { // java.util.Properties throws NPE when calling put with null
                            if (SUN_BOOT_CLASS_PATH.equals(line)) {
                                StringBuilder filteredValue = filterJBoss(systemProperty);
                                properties.put(line, filteredValue.toString());
                            } else if ("java.endorsed.dirs".equals(line)) { //$NON-NLS-1$
                                // Skip it!
                            } else if (JAVA_CLASS_PATH.equals(line)) {
                                StringBuilder filteredValue = filterJBoss(
                                        System.getProperty(SUN_BOOT_CLASS_PATH, System.getProperty(JAVA_CLASS_PATH)));
                                properties.put(line, filteredValue.toString());
                            } else {
                                properties.put(line, systemProperty);
                            }
                        }
                    }
                    line = bufferedReader.readLine();
                }
            }
            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private StringBuilder filterJBoss(String systemProperty) {
        StringBuilder filteredValue = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(systemProperty, ":"); //$NON-NLS-1$
        while (tokenizer.hasMoreElements()) {
            String current = tokenizer.nextToken();
            if (!current.contains("jboss")) { //$NON-NLS-1$
                filteredValue.append(current);
                if (tokenizer.hasMoreElements()) {
                    filteredValue.append(':');
                }
            }

        }
        return filteredValue;
    }
}
