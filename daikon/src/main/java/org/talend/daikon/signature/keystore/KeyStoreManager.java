// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.signature.keystore;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.signature.exceptions.InvalidKeyStoreException;

public class KeyStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStoreManager.class);

    protected static KeyStoreSetting verifyStoreSettings;

    protected KeyStore verifyKeyStore;

    private static KeyStoreManager instance = null;

    public static KeyStoreManager getInstance() {
        if (instance == null) {
            synchronized (KeyStoreManager.class) {
                if (instance == null) {
                    instance = new KeyStoreManager();
                }
            }
        }
        return instance;
    }

    private KeyStoreManager() {
        initKeyStoreSettings();
    }

    protected void initKeyStoreSettings() {
        verifyStoreSettings = null;
        String verifyStorePass = "ABC14A986449D5C6511E675B7C37658B"; //$NON-NLS-1$
        URL verifyJKSUrl = KeyStoreManager.class.getResource("talend-code-vrfy.jks"); //$NON-NLS-1$
        verifyStoreSettings = new KeyStoreSetting();
        verifyStoreSettings.setStoreUrl(verifyJKSUrl);
        verifyStoreSettings.setStorePassword(verifyStorePass.toCharArray());
    }

    public KeyStoreSetting getVerifyStoreSettings() {
        return verifyStoreSettings;
    }

    public KeyStore getVerifyKeyStore() throws InvalidKeyStoreException {
        if (verifyKeyStore == null) {
            final KeyStoreSetting verifyKeyStoreSetting = getVerifyStoreSettings();
            if (verifyKeyStoreSetting != null) {
                KeyStore keyStore;
                try {
                    keyStore = KeyStore.getInstance(verifyKeyStoreSetting.getStoreType());
                    keyStore.load(verifyKeyStoreSetting.getStoreUrl().openStream(), verifyKeyStoreSetting.getStorePassword());
                    verifyKeyStore = keyStore;
                } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
                    LOGGER.error("Load key store failed." + e);//$NON-NLS-1$
                    throw new InvalidKeyStoreException("Load key store failed." + e.getMessage(), e);
                }
            }
        }
        return verifyKeyStore;
    }
}
