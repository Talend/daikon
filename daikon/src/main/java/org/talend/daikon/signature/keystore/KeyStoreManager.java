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
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.signature.exceptions.InvalidKeyStoreException;

public class KeyStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStoreManager.class);

    private String storeType = "JKS";

    protected KeyStore verifyKeyStore;

    public void loadKeyStore(InputStream keyStoreInputStream, String keyStorePass) throws InvalidKeyStoreException {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(getStoreType());
            keyStore.load(keyStoreInputStream, keyStorePass.toCharArray());
            verifyKeyStore = keyStore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            LOGGER.error("Load key store failed." + e);//$NON-NLS-1$
            throw new InvalidKeyStoreException("Load key store failed." + e.getMessage(), e);
        } finally {
            try {
                keyStoreInputStream.close();
            } catch (IOException ex) {
                LOGGER.error("Close input stream failed." + ex); //$NON-NLS-1$
            }
        }
    }

    public KeyStore getVerifyKeyStore() throws InvalidKeyStoreException {
        return verifyKeyStore;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }
}
