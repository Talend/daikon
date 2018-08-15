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
package org.talend.daikon.signature.verify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.talend.daikon.signature.exceptions.InvalidKeyStoreException;
import org.talend.daikon.signature.exceptions.MissingEntryException;
import org.talend.daikon.signature.exceptions.NoCodeSignCertificateException;
import org.talend.daikon.signature.exceptions.NoValidCertificateException;
import org.talend.daikon.signature.exceptions.VerifyFailedException;
import org.talend.daikon.signature.keystore.KeyStoreManager;

import sun.security.provider.certpath.X509CertPath;

public class ZipVerifier {

    private PKIXParameters param;

    public ZipVerifier() throws Exception {
        initPKIXParameter();
    }

    private void initPKIXParameter() throws Exception {
        final KeyStore keyStore = getKeyStore();
        if (keyStore == null) {
            throw new InvalidKeyStoreException("Can't load the key store for verify"); //$NON-NLS-1$
        }
        param = new PKIXParameters(keyStore);
        param.setRevocationEnabled(false);
    }

    protected KeyStore getKeyStore() throws Exception {
        KeyStoreManager keyStoreManager = KeyStoreManager.getInstance();
        KeyStore keyStore = keyStoreManager.getVerifyKeyStore();
        return keyStore;
    }

    public void verify(String filePath) throws Exception {
        assert (filePath != null);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("The file is not exist:" + filePath); //$NON-NLS-1$
        }
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(filePath);
            Manifest mainfest = jarFile.getManifest();
            if (mainfest == null) {
                throw new MissingEntryException("Missing entry:" + JarFile.MANIFEST_NAME); //$NON-NLS-1$
            }
            Map<String, Attributes> manifestEntryMap = mainfest.getEntries();

            Enumeration<JarEntry> entriesEnum = jarFile.entries();
            Set<String> verifiedEntryNameSet = new HashSet<String>();
            while (entriesEnum.hasMoreElements()) {
                JarEntry entry = entriesEnum.nextElement();
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    byte[] bytes = new byte[4096];
                    InputStream is = jarFile.getInputStream(entry);
                    while ((is.read(bytes, 0, bytes.length)) != -1) {
                        // A SecurityException is thrown here if the digest is incorrect.
                    }
                }
                if (entry.isDirectory() || isSignatureRelatedEntry(entry.getName())) {
                    continue;
                }

                if (!manifestEntryMap.containsKey(entry.getName()) || entry.getCodeSigners() == null
                        || entry.getCodeSigners().length == 0) {
                    throw new Exception("Find unsigned entry:" + entry.getName());
                }

                // Verify the CertPath
                boolean isContainSignCert = false;
                for (CodeSigner cs : entry.getCodeSigners()) {
                    if (!isContainSignCert && isContainCodeSignCert(cs)) {
                        isContainSignCert = true;
                    }
                    if (cs.getTimestamp() != null) {
                        param.setDate(cs.getTimestamp().getTimestamp());
                    } else {
                        param.setDate(null);
                    }
                    PKIXCertPathValidatorResult result = validate(cs.getSignerCertPath());
                    if (result == null) {
                        throw new RuntimeException("No validate result for cert path."); //$NON-NLS-1$
                    }
                }
                if (!isContainSignCert) {
                    throw new NoCodeSignCertificateException(
                            "Can't find any code sign certificate for the entry:" + entry.getName()); //$NON-NLS-1$
                }
                verifiedEntryNameSet.add(entry.getName());
            }

            // Check signed the entry number
            if (manifestEntryMap.size() != verifiedEntryNameSet.size()) {
                for (String key : manifestEntryMap.keySet()) {
                    if (!verifiedEntryNameSet.contains(key)) {
                        throw new MissingEntryException("Missing entry:" + key); //$NON-NLS-1$
                    }
                }
            }
        } catch (Exception ex) {
            throw new VerifyFailedException("Verify failed." + ex.getMessage(), ex); //$NON-NLS-1$
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
    }

    private PKIXCertPathValidatorResult validate(CertPath certPath) throws NoSuchAlgorithmException, CertPathValidatorException,
            InvalidAlgorithmParameterException, NoValidCertificateException, CertificateException {
        if (certPath == null || certPath.getCertificates() == null || certPath.getCertificates().size() == 0) {
            throw new NoValidCertificateException("No valid certificate"); //$NON-NLS-1$
        }
        List<? extends Certificate> certList = certPath.getCertificates();
        List<X509Certificate> validCertList = new ArrayList<X509Certificate>();
        for (Certificate cert : certList) {
            if (cert instanceof X509Certificate) {
                X509Certificate x509Cert = (X509Certificate) cert;
                try {
                    if (param.getDate() == null) {
                        x509Cert.checkValidity();
                    } else {
                        x509Cert.checkValidity(param.getDate());
                    }
                    validCertList.add(x509Cert);
                } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                    // Igore here
                }
            }
        }
        if (validCertList.size() == 0) {
            throw new NoValidCertificateException("No valid certificate, all certificates are expired."); //$NON-NLS-1$
        }

        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
        return (PKIXCertPathValidatorResult) validator.validate(new X509CertPath(validCertList), param);
    }

    private boolean isSignatureRelatedEntry(String entryName) {
        if (entryName.equals(JarFile.MANIFEST_NAME) || entryName.matches("META-INF/.*.SF") //$NON-NLS-1$
                || entryName.matches("META-INF/.*.RSA")) { //$NON-NLS-1$
            return true;
        }
        return false;
    }

    private boolean isContainCodeSignCert(CodeSigner codeSigner) throws CertificateParsingException {
        if (codeSigner != null) {
            List<? extends Certificate> certificateList = codeSigner.getSignerCertPath().getCertificates();
            if (certificateList != null) {
                for (Object cert : certificateList) {
                    if (cert instanceof X509Certificate && isCodeSignCert((X509Certificate) cert)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCodeSignCert(final X509Certificate cert) throws CertificateParsingException {
        List<String> keyUsage = cert.getExtendedKeyUsage();
        return keyUsage != null && (keyUsage.contains("2.5.29.37.0") || keyUsage.contains("1.3.6.1.5.5.7.3.3")); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
