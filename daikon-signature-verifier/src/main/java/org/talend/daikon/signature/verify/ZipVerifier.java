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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.signature.exceptions.InvalidKeyStoreException;
import org.talend.daikon.signature.exceptions.MissingEntryException;
import org.talend.daikon.signature.exceptions.NoCodeSignCertificateException;
import org.talend.daikon.signature.exceptions.NoValidCertificateException;
import org.talend.daikon.signature.exceptions.UnsignedArchiveException;
import org.talend.daikon.signature.exceptions.UnsignedEntryException;
import org.talend.daikon.signature.exceptions.VerifyException;
import org.talend.daikon.signature.exceptions.VerifyFailedException;
import org.talend.daikon.signature.keystore.KeyStoreManager;

public class ZipVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipVerifier.class);

    private boolean isCheckSignatureTimestamp = true;

    private PKIXParameters param;

    /**
     * ZipVerifier will close this keyStoreInputStream after load the key store
     * 
     * @param keyStoreInputStream - ZipVerifier will close this keyStoreInputStream after load the key store
     * @param keyStorePass - The keyStore password
     * @throws InvalidKeyStoreException
     * @throws KeyStoreException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     */
    public ZipVerifier(InputStream keyStoreInputStream, String keyStorePass)
            throws InvalidKeyStoreException, KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        assert (keyStoreInputStream != null && keyStorePass != null);
        initPKIXParameter(keyStoreInputStream, keyStorePass);
    }

    private void initPKIXParameter(InputStream keyStoreInputStream, String keyStorePass)
            throws InvalidKeyStoreException, KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyStoreManager keyStoreManager = new KeyStoreManager();
        keyStoreManager.loadKeyStore(keyStoreInputStream, keyStorePass);
        final KeyStore keyStore = keyStoreManager.getVerifyKeyStore();
        if (keyStore == null) {
            throw new InvalidKeyStoreException("Can't load the key store"); //$NON-NLS-1$
        }

        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        List<X509Certificate> intermediateCerts = new ArrayList<>();
        // Look-up for intermediate and root CAs
        String els;
        X509Certificate tcert;
        Enumeration<String> es = keyStore.aliases();
        while (es.hasMoreElements()) {
            els = (String) es.nextElement();
            if (keyStore.isCertificateEntry(els)) {
                tcert = (X509Certificate) keyStore.getCertificate(els);
                // Verify that this certificate is a CA. PKIX algorithm will perform
                // more elaborate checks, but there is no point in having a non-CA added to the
                // path validation beyond leaf certificate
                if (tcert.getBasicConstraints() < 0) {
                    continue;
                }
                // If the cert is self-signed: it is a root CA.
                if (isSelfSigned(tcert)) {
                    trustAnchors.add(new TrustAnchor(tcert, null));
                } else {
                    // It is an intermediate CA, add it to the intermediate list
                    intermediateCerts.add(tcert);
                }
            }
        }
        param = new PKIXParameters(trustAnchors);
        param.setRevocationEnabled(false);
        param.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediateCerts)));
    }

    public void verify(String filePath) throws VerifyFailedException {
        assert (filePath != null);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new VerifyFailedException("The file does not exist:" + filePath); //$NON-NLS-1$
        }
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(filePath);
            Manifest mainfest = jarFile.getManifest();
            if (mainfest == null) {
                throw new UnsignedArchiveException("Unsigned archive, missing entry:" + JarFile.MANIFEST_NAME); //$NON-NLS-1$
            }
            Map<String, Attributes> manifestEntryMap = mainfest.getEntries();
            Enumeration<JarEntry> entriesEnum = jarFile.entries();
            Set<String> verifiedEntryNameSet = new HashSet<String>();
            while (entriesEnum.hasMoreElements()) {
                JarEntry entry = entriesEnum.nextElement();
                if (entry.isDirectory() || isSignatureRelatedEntry(entry.getName())) {
                    continue;
                }
                if (!manifestEntryMap.containsKey(entry.getName())) {
                    throw new UnsignedEntryException("Found unsigned entry:" + entry.getName());
                }
                readAndCheckEntry(jarFile, entry);
                checkCodeSigners(entry);
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
                try {
                    jarFile.close();
                } catch (IOException ex) {
                    LOGGER.debug("Close jar file failed:" + ex);
                }
            }
        }
    }

    private static boolean isSelfSigned(X509Certificate cert) {
        // Checks that the certificate is signed by its own public key.
        try {
            PublicKey p = cert.getPublicKey();
            cert.verify(p);
            return true;
        } catch (Exception e) {
            // Signature/key is invalid or absent.
            return false;
        }
    }

    private void readAndCheckEntry(JarFile jarFile, JarEntry entry) throws VerifyFailedException {
        InputStream is = null;
        byte[] buffer = new byte[8192];
        try {
            is = jarFile.getInputStream(entry);
            // Perform an explicit read of a file entry. will throw a SecurityException if
            // entry's signature checks fail.
            while ((is.read(buffer, 0, buffer.length)) != -1)
                ;
        } catch (java.lang.SecurityException ex) {
            throw new VerifyFailedException("Verify failed." + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new VerifyFailedException("Verify failed." + ex.getMessage(), ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.debug("Close stream failed:" + ex);
                }
            }
        }
    }

    private void checkCodeSigners(JarEntry entry) throws NoSuchAlgorithmException, CertPathValidatorException,
            InvalidAlgorithmParameterException, CertificateException, VerifyException {
        if (entry.getCodeSigners() == null || entry.getCodeSigners().length == 0) {
            throw new UnsignedEntryException("Found unsigned entry, no code signers:" + entry.getName());
        }
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
                throw new VerifyException("No validate result for cert path."); //$NON-NLS-1$
            }
        }
        if (!isContainSignCert) {
            throw new NoCodeSignCertificateException("Can't find any code sign certificate for the entry:" + entry.getName()); //$NON-NLS-1$
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
                    if (isCheckSignatureTimestamp) {
                        x509Cert.checkValidity();
                    }
                    validCertList.add(x509Cert);
                } catch (CertificateExpiredException | CertificateNotYetValidException ex) {
                    LOGGER.debug("Found invalid certificate:" + ex);
                }
            }
        }
        if (validCertList.size() == 0) {
            throw new NoValidCertificateException("No valid certificate, all certificates are expired."); //$NON-NLS-1$
        }

        // If we are skipping the signature timestamp check, then make sure that an expired signing cert is allowed
        // for CertPath validation
        if (!isCheckSignatureTimestamp) {
            X509Certificate x509Cert = validCertList.get(0);
            Instant notAfter = x509Cert.getNotAfter().toInstant();
            param.setDate(Date.from(notAfter.minus(Duration.ofDays(2))));
        }

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509"); //$NON-NLS-1$
        CertPath toVerifyCertPath = certificateFactory.generateCertPath(validCertList);
        CertPathValidator validator = CertPathValidator.getInstance("PKIX"); //$NON-NLS-1$
        return (PKIXCertPathValidatorResult) validator.validate(toVerifyCertPath, param);
    }

    private boolean isSignatureRelatedEntry(String entryName) {
        return entryName.equalsIgnoreCase(JarFile.MANIFEST_NAME) || entryName.toUpperCase().matches("META-INF/.*.SF") //$NON-NLS-1$
                || entryName.toUpperCase().matches("META-INF/.*.RSA") || entryName.toUpperCase().matches("META-INF/.*.EC")
                || entryName.toUpperCase().matches("META-INF/.*.DSA");
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
        // Check digitalSignature Key Usage.
        boolean[] keyUsages = cert.getKeyUsage();
        boolean isDigitalSignature = true;
        if (keyUsages == null || keyUsages[0] == false) {
            LOGGER.error("Certificate is missing digital signature KeyUsage.");
            isDigitalSignature = false;
        }
        List<String> extendesKeyUsage = cert.getExtendedKeyUsage();
        return isDigitalSignature && extendesKeyUsage != null
                && (extendesKeyUsage.contains("2.5.29.37.0") || extendesKeyUsage.contains("1.3.6.1.5.5.7.3.3")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isCheckSignatureTimestamp() {
        return isCheckSignatureTimestamp;
    }

    public void setCheckSignatureTimestamp(boolean isCheckSignatureTimestamp) {
        this.isCheckSignatureTimestamp = isCheckSignatureTimestamp;
    }
}
