package org.talend.daikon.signature.verify;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateGenerater {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateGenerater.class);

    private SecureRandom secureRandom;

    private String rootJKSFileName = "root.jks";

    private String rootAlias = "code-signing-ca";

    private String rootJKSFileNameTwo = "root2.jks";

    private String rootAliasTwo = "code-signing-ca2";

    private String rootJKSKeyPass = "123456";

    private String subJKSAlias = "code-signing";

    private String dName = "C=NO,ST=NO,L=NoLocality,O=Talend,OU=R&D,CN=Code-signing test CA";

    private String subDName = "C=NO,ST=NO,L=NoLocality,O=Talend,OU=R&D,CN=Code-signing test certificate";

    private String trustStoreName = "truststore.jks";

    private String trustStoreTwoName = "truststore2.jks";

    private String codeSignJksValidPath = "code-signing_valid.jks";

    private String codeSignJksNoUsagePath = "code-signing_NoUsage.jks";

    private String codeSignJksExpiredPath = "code-signing_Expired.jks";

    private String sigAlgName = "sha256WithRSA";

    private String keyStoreType = "JKS";

    private String subJKSKeyPass = null;

    private String folderPath = null;

    public CertificateGenerater(String folderPath, String storePass) {
        this.folderPath = folderPath;
        this.subJKSKeyPass = storePass;
    }

    public void generateCertificate() throws Exception {
        secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        createRootCA(rootAlias, rootJKSFileName);
        LOGGER.debug("Created root ca");

        createRootCA(rootAliasTwo, rootJKSFileNameTwo);
        LOGGER.debug("Created root ca2");

        Date now = new Date();
        long validity = 7L * 24 * 3600 * 1000;

        createSignJks(now, new Date(now.getTime() + validity), codeSignJksValidPath, true);
        LOGGER.debug("Created valid code sign JKS");

        createNoUsageCodeSignJks();
        LOGGER.debug("created no usage code sign JKS");

        createSignJks(new Date(now.getTime() - validity), now, codeSignJksExpiredPath, true);
        LOGGER.debug("created expired code sign JKS");

        createSignJks(now, new Date(now.getTime() + validity), trustStoreName, true);
        LOGGER.debug("created truststore JKS");

        createSignJks(now, new Date(now.getTime() + validity), trustStoreTwoName, false);
        LOGGER.debug("created invalid truststore JKS us ca2");
    }

    private void createRootCA(String alias, String fileName) throws Exception {
        List<Extension> exts = new ArrayList<>();
        KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.keyEncipherment
                | KeyUsage.dataEncipherment | KeyUsage.keyCertSign);
        Extension extension = new Extension(Extension.keyUsage, true, new DEROctetString(keyUsage));
        exts.add(extension);

        // Missing ekeyOid = new ObjectIdentifier("2.5.29.19"); from the old code here
        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning);
        extension = new Extension(Extension.extendedKeyUsage, false, new DEROctetString(extendedKeyUsage));
        exts.add(extension);

        KeyPair keyPair = genKey();
        BigInteger serialNumber = new BigInteger(64, secureRandom);
        Date from = new Date();
        Date to = new Date(from.getTime() + 365L * 24 * 3600 * 1000);
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(new X500Principal(dName), serialNumber,
                from, to, new X500Principal(dName), keyPair.getPublic());
        for (Extension e : exts) {
            certificateBuilder.addExtension(e);
        }
        certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        ContentSigner signer = new JcaContentSignerBuilder(sigAlgName).build(keyPair.getPrivate());
        X509Certificate cert = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
                .getCertificate(certificateBuilder.build(signer));
        X509Certificate[] certs = { cert };

        String[] aliasNames = { alias };
        saveJks(aliasNames, keyPair.getPrivate(), rootJKSKeyPass, certs, fileName);
    }

    private void saveJks(String[] aliasNames, PrivateKey privKey, String pwd, Certificate[] certChain, String filepath)
            throws Exception {
        KeyStore outputKeyStore = KeyStore.getInstance(keyStoreType);
        outputKeyStore.load(null, pwd.toCharArray());

        for (int i = 0; i < aliasNames.length; i++) {
            outputKeyStore.setCertificateEntry(aliasNames[i], certChain[i]);
        }
        outputKeyStore.setKeyEntry(aliasNames[0], privKey, pwd.toCharArray(), certChain);
        File certFile = new File(folderPath, filepath);
        if (certFile.exists()) {
            certFile.delete();
        }
        try (FileOutputStream out = new FileOutputStream(certFile)) {
            outputKeyStore.store(out, pwd.toCharArray());
        }
    }

    private void createSignJks(Date from, Date to, String storePath, boolean useRootJks) throws Exception {
        List<Extension> exts = new ArrayList<>();
        KeyUsage keyUsage = new KeyUsage(
                KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment);
        Extension extension = new Extension(Extension.keyUsage, true, new DEROctetString(keyUsage));
        exts.add(extension);

        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning);
        extension = new Extension(Extension.extendedKeyUsage, false, new DEROctetString(extendedKeyUsage));
        exts.add(extension);

        signCert(useRootJks, subJKSKeyPass, from, to, exts, storePath, true);
    }

    private void createNoUsageCodeSignJks() throws Exception {
        long validity = 7L * 24 * 3600 * 1000;
        Date firstDate = new Date();
        Date lastDate = new Date(firstDate.getTime() + validity);

        signCert(true, subJKSKeyPass, firstDate, lastDate, Collections.<Extension> emptyList(), codeSignJksNoUsagePath, false);
    }

    private void signCert(boolean useRootJks, String subjectPasswd, Date from, Date to, List<Extension> exts, String storePath,
            boolean containCACert) throws Exception {
        String innerRootAlias = null;
        String keyStoreFileName = null;
        if (useRootJks) {
            keyStoreFileName = rootJKSFileName;
            innerRootAlias = rootAlias;
        } else {
            keyStoreFileName = rootJKSFileNameTwo;
            innerRootAlias = rootAliasTwo;
        }

        KeyPair keyPair = genKey();
        BigInteger serialNumber = new BigInteger(64, secureRandom);
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(new X500Principal(dName), serialNumber,
                from, to, new X500Principal(subDName), keyPair.getPublic());
        for (Extension e : exts) {
            certificateBuilder.addExtension(e);
        }

        KeyStore keyStore = this.loadKeyStore(new File(folderPath, keyStoreFileName), rootJKSKeyPass);
        X509Certificate caCert = (X509Certificate) keyStore.getCertificate(innerRootAlias);
        PrivateKey caPrivateKey = (PrivateKey) keyStore.getKey(innerRootAlias, rootJKSKeyPass.toCharArray());

        ContentSigner signer = new JcaContentSignerBuilder(sigAlgName).build(caPrivateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
                .getCertificate(certificateBuilder.build(signer));

        Certificate[] certs = null;
        String[] aliasNames = null;
        if (containCACert) {
            certs = new Certificate[] { cert, caCert };
            aliasNames = new String[] { subJKSAlias, rootAlias };
        } else {
            certs = new Certificate[] { cert };
            aliasNames = new String[] { subJKSAlias };
        }

        saveJks(aliasNames, keyPair.getPrivate(), subjectPasswd, certs, storePath);
    }

    private KeyStore loadKeyStore(File keyStoreFile, String storePass)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
            keyStore.load(inputStream, storePass.toCharArray());
        }
        return keyStore;
    }

    private KeyPair genKey() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048, secureRandom);

        return kpg.generateKeyPair();
    }

    public String getTrustStorePath() {
        return new File(folderPath, trustStoreName).getAbsolutePath();
    }

    public String getTrustStoreTwoPath() {
        return new File(folderPath, trustStoreTwoName).getAbsolutePath();
    }

    public String getCodeSignJksValidPath() {
        return codeSignJksValidPath;
    }

    public String getCodeSignJksNoUsagePath() {
        return codeSignJksNoUsagePath;
    }

    public String getCodeSignJksExpiredPath() {
        return codeSignJksExpiredPath;
    }

    public String getSubJKSKeyPass() {
        return subJKSKeyPass;
    }

    public String getSubJKSAlias() {
        return subJKSAlias;
    }
}
