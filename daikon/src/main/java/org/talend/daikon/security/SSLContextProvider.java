// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;

/**
 * created by wchen on Nov 14, 2016 Detailled comment
 *
 */
public class SSLContextProvider {

    private static KeyManager[] buildKeyManagers(String path, String storePass, String keytype)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        InputStream stream = null;
        try {
            if (StringUtils.isEmpty(path)) {
                return null;
            }
            if (!new File(path).exists()) {
                throw new KeyStoreException("Key store not exist");
            }
            stream = new FileInputStream(path);

            KeyStore tks = KeyStore.getInstance(keytype);
            tks.load(stream, storePass.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(tks, storePass.toCharArray());

            return kmf.getKeyManagers();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static TrustManager[] buildTrustManagers(String path, String storePass, String trusttype)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        InputStream stream = null;
        try {
            if (StringUtils.isEmpty(path)) {
                return null;
            }
            if (StringUtils.isEmpty(path) || !new File(path).exists()) {
                throw new KeyStoreException("Trust store not exist");
            }
            stream = new FileInputStream(path);

            KeyStore tks = KeyStore.getInstance(trusttype);
            tks.load(stream, storePass.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(tks);

            return tmf.getTrustManagers();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public synchronized static SSLContext buildContext(String algorithm, String keypath, String keypass, String keytype,
            String trustpath, String trustpass, String trusttype) throws UnrecoverableKeyException, KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
        KeyManager[] kms = buildKeyManagers(keypath, keypass, keytype);
        TrustManager[] tms = buildTrustManagers(trustpath, trustpass, trusttype);
        SSLContext context = SSLContext.getInstance(algorithm);
        context.init(kms, tms, null);
        return context;
    }

}
