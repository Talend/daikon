// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyProvider {

    protected static Logger log = LoggerFactory.getLogger(KeyProvider.class);

    public static final String PROPERTY_ENCRYPTION_KEY_FILE = "encryption.keys.file";

    public static final String SYSTEM_ENCRYPTION_KEY = "system.encryption.key";

    private Map<String, char[]> keysMap = new HashMap<String, char[]>();

    private KeyProvider() {
        init();
    }

    public static KeyProvider getInstance() {
        return KeyProviderHolder.instance;
    }

    private static class KeyProviderHolder {

        private static final KeyProvider instance = new KeyProvider();
    }

    private void init() {
        String filePath = System.getProperty(PROPERTY_ENCRYPTION_KEY_FILE);
        if (filePath != null && filePath.length() > 0) {
            if (new File(filePath).exists()) {
                try {
                    loadFromFile(new FileInputStream(new File(filePath)), false);
                } catch (FileNotFoundException e) {
                    processError("Read custome key file failed:" + e);
                }
            } else {
                processError("Can't find custom encrtypt key file.");
            }
        }
        loadFromFile(KeyProvider.class.getResourceAsStream("key.dat"), true);
    }

    private void loadFromFile(InputStream inputStream, boolean isDefault) {
        try {
            Properties prop = new Properties();
            prop.load(inputStream);
            Set<Object> keys = prop.keySet();
            for (Object key : keys) {
                if (key != null) {
                    String strKey = (String) key;
                    String value = prop.getProperty(strKey);
                    if (!isDefault) {
                        if (value != null) {
                            keysMap.put(strKey, value.toCharArray());
                        } else {
                            processError("The custom value of key is empty:" + strKey);
                        }

                    } else if (!keysMap.containsKey(strKey)) {
                        byte[] decodedBytes = Base64.getDecoder().decode(value);
                        value = new String(decodedBytes, StandardCharsets.UTF_8);
                        keysMap.put(strKey, value.toCharArray());
                    }
                }
            }
        } catch (IOException e) {
            processError("Read custom encrypt key file failed:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    processError("Read custom encrypt key file failed:", e);
                }
            }
        }
    }

    public String getKeyValue(String key) {
        char[] charValue = keysMap.get(key);
        if (charValue != null) {
            return new String(charValue);
        } else {
            processError("Can't find any value for key:" + key);
        }
        return "";
    }

    private void processError(String msg, Throwable e) {
        log.error(msg + e.getLocalizedMessage());
    }

    private void processError(String msg) {
        log.error(msg);
    }

}
