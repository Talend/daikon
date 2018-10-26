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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class CertificateGenerater {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateGenerater.class);

    private SecureRandom secureRandom;;

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
        createRootCA();
        LOGGER.debug("Created root ca");
        createRootCATwo();
        LOGGER.debug("Created root ca2");
        createValidCodeSignJks();
        LOGGER.debug("Created valid code sign JKS");
        createNoUsageCodeSignJks();
        LOGGER.debug("created no usage code sign JKS");
        createExpiredCodeSignJks();
        LOGGER.debug("created expired code sign JKS");
        createTrustStoreJks();
        LOGGER.debug("created truststore JKS");
        createTrustStoreJksTwo();
        LOGGER.debug("created invalid truststore JKS us ca2");
    }

    private void createRootCA() throws Exception {
        CertAndKeyGen certGen = new CertAndKeyGen("RSA", sigAlgName);
        certGen.setRandom(secureRandom);
        certGen.generate(1024);
        X500Name subject = new X500Name(dName);

        KeyUsageExtension keyUsage = new KeyUsageExtension();
        keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
        keyUsage.set(KeyUsageExtension.NON_REPUDIATION, true);
        keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.KEY_CERTSIGN, true);
        ObjectIdentifier ekeyOid = new ObjectIdentifier(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 });
        Vector<ObjectIdentifier> vkeyOid = new Vector<ObjectIdentifier>();
        vkeyOid.add(ekeyOid);
        ekeyOid = new ObjectIdentifier("2.5.29.19");
        vkeyOid.add(ekeyOid);
        ExtendedKeyUsageExtension exKeyUsage = new ExtendedKeyUsageExtension(vkeyOid);
        CertificateExtensions exts = new CertificateExtensions();
        exts.set("keyUsage", keyUsage);
        exts.set("extendedKeyUsage", exKeyUsage);
        exts.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(true, -1));

        X509Certificate certificate = certGen.getSelfCertificate(subject, new Date(), 365L * 24 * 3600 * 1000, exts);
        X509Certificate[] certs = { certificate };

        String[] aliasNames = { rootAlias };
        saveJks(aliasNames, certGen.getPrivateKey(), rootJKSKeyPass, certs, rootJKSFileName);
    }

    private void createRootCATwo() throws Exception {
        CertAndKeyGen certGen = new CertAndKeyGen("RSA", sigAlgName);
        certGen.setRandom(secureRandom);
        certGen.generate(1024);
        X500Name subject = new X500Name(dName);

        KeyUsageExtension keyUsage = new KeyUsageExtension();
        keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
        keyUsage.set(KeyUsageExtension.NON_REPUDIATION, true);
        keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.KEY_CERTSIGN, true);
        ObjectIdentifier ekeyOid = new ObjectIdentifier(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 });
        Vector<ObjectIdentifier> vkeyOid = new Vector<ObjectIdentifier>();
        vkeyOid.add(ekeyOid);
        ekeyOid = new ObjectIdentifier("2.5.29.19");
        vkeyOid.add(ekeyOid);
        ExtendedKeyUsageExtension exKeyUsage = new ExtendedKeyUsageExtension(vkeyOid);
        CertificateExtensions exts = new CertificateExtensions();
        exts.set("keyUsage", keyUsage);
        exts.set("extendedKeyUsage", exKeyUsage);
        exts.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(true, -1));

        X509Certificate certificate = certGen.getSelfCertificate(subject, new Date(), 365L * 24 * 3600 * 1000, exts);
        X509Certificate[] certs = { certificate };

        String[] aliasNames = { rootAliasTwo };
        saveJks(aliasNames, certGen.getPrivateKey(), rootJKSKeyPass, certs, rootJKSFileNameTwo);
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
        FileOutputStream out = new FileOutputStream(certFile);
        outputKeyStore.store(out, pwd.toCharArray());
        out.close();
    }

    private void createValidCodeSignJks() throws Exception {
        KeyUsageExtension keyUsage = new KeyUsageExtension();
        keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
        keyUsage.set(KeyUsageExtension.NON_REPUDIATION, true);
        keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
        ObjectIdentifier ekeyOid = new ObjectIdentifier(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 });
        Vector<ObjectIdentifier> vkeyOid = new Vector<ObjectIdentifier>();
        vkeyOid.add(ekeyOid);
        ExtendedKeyUsageExtension exKeyUsage = new ExtendedKeyUsageExtension(vkeyOid);
        CertificateExtensions exts = new CertificateExtensions();
        exts.set("keyUsage", keyUsage);
        exts.set("extendedKeyUsage", exKeyUsage);

        long validity = 7L * 24 * 3600 * 1000;
        Date firstDate = new Date();
        Date lastDate = new Date(firstDate.getTime() + validity);
        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        signCert(true, subJKSKeyPass, interval, exts, codeSignJksValidPath, true);
    }

    private void createNoUsageCodeSignJks() throws Exception {
        CertificateExtensions exts = new CertificateExtensions();

        long validity = 7L * 24 * 3600 * 1000;
        Date firstDate = new Date();
        Date lastDate = new Date(firstDate.getTime() + validity);
        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        signCert(true, subJKSKeyPass, interval, exts, codeSignJksNoUsagePath, false);
    }

    private void createExpiredCodeSignJks() throws Exception {
        KeyUsageExtension keyUsage = new KeyUsageExtension();
        keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
        keyUsage.set(KeyUsageExtension.NON_REPUDIATION, true);
        keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
        ObjectIdentifier ekeyOid = new ObjectIdentifier(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 });
        Vector<ObjectIdentifier> vkeyOid = new Vector<ObjectIdentifier>();
        vkeyOid.add(ekeyOid);
        ExtendedKeyUsageExtension exKeyUsage = new ExtendedKeyUsageExtension(vkeyOid);
        CertificateExtensions exts = new CertificateExtensions();
        exts.set("keyUsage", keyUsage);
        exts.set("extendedKeyUsage", exKeyUsage);

        long validity = 7L * 24 * 3600 * 1000;
        Date lastDate = new Date();
        Date firstDate = new Date(lastDate.getTime() - validity);

        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        signCert(true, subJKSKeyPass, interval, exts, codeSignJksExpiredPath, true);
    }

    private void createTrustStoreJks() throws Exception {
        KeyUsageExtension keyUsage = new KeyUsageExtension();
        keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
        keyUsage.set(KeyUsageExtension.NON_REPUDIATION, true);
        keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
        ObjectIdentifier ekeyOid = new ObjectIdentifier(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 });
        Vector<ObjectIdentifier> vkeyOid = new Vector<ObjectIdentifier>();
        vkeyOid.add(ekeyOid);
        ExtendedKeyUsageExtension exKeyUsage = new ExtendedKeyUsageExtension(vkeyOid);
        CertificateExtensions exts = new CertificateExtensions();
        exts.set("keyUsage", keyUsage);
        exts.set("extendedKeyUsage", exKeyUsage);

        long validity = 7L * 24 * 3600 * 1000;
        Date firstDate = new Date();
        Date lastDate = new Date(firstDate.getTime() + validity);

        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        signCert(true, subJKSKeyPass, interval, exts, trustStoreName, true);
    }

    private void createTrustStoreJksTwo() throws Exception {
        KeyUsageExtension keyUsage = new KeyUsageExtension();
        keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
        keyUsage.set(KeyUsageExtension.NON_REPUDIATION, true);
        keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
        keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
        ObjectIdentifier ekeyOid = new ObjectIdentifier(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 });
        Vector<ObjectIdentifier> vkeyOid = new Vector<ObjectIdentifier>();
        vkeyOid.add(ekeyOid);
        ExtendedKeyUsageExtension exKeyUsage = new ExtendedKeyUsageExtension(vkeyOid);
        CertificateExtensions exts = new CertificateExtensions();
        exts.set("keyUsage", keyUsage);
        exts.set("extendedKeyUsage", exKeyUsage);

        long validity = 7L * 24 * 3600 * 1000;
        Date firstDate = new Date();
        Date lastDate = new Date(firstDate.getTime() + validity);

        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        signCert(false, subJKSKeyPass, interval, exts, trustStoreTwoName, true);
    }

    private void signCert(boolean useRootJks, String subjectPasswd, CertificateValidity interval, CertificateExtensions exts,
            String storePath, boolean containCACert) throws Exception {
        String innerRootAlias = null;
        String keyStoreFileName = null;
        if (useRootJks) {
            keyStoreFileName = rootJKSFileName;
            innerRootAlias = rootAlias;
        } else {
            keyStoreFileName = rootJKSFileNameTwo;
            innerRootAlias = rootAliasTwo;
        }
        KeyStore keyStore = this.loadKeyStore(new File(folderPath, keyStoreFileName), rootJKSKeyPass);
        X509Certificate caCert = (X509Certificate) keyStore.getCertificate(innerRootAlias);
        PrivateKey caPrivateKey = (PrivateKey) keyStore.getKey(innerRootAlias, rootJKSKeyPass.toCharArray());

        CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", sigAlgName);
        certAndKeyGen.setRandom(secureRandom);
        certAndKeyGen.generate(1024);
        byte certbytes[] = caCert.getEncoded();
        X509CertImpl x509certimpl = new X509CertImpl(certbytes);
        X509CertInfo x509certinfo = (X509CertInfo) x509certimpl.get("x509.info");
        x509certinfo.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        x509certinfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));
        AlgorithmId algID = AlgorithmId.get(sigAlgName);
        x509certinfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algID));
        X500Name issuer = new X500Name(dName);
        x509certinfo.set("issuer.dname", issuer);
        X500Name subject = new X500Name(subDName);
        x509certinfo.set("subject.dname", subject);
        x509certinfo.set(X509CertInfo.KEY, new CertificateX509Key(certAndKeyGen.getPublicKey()));
        x509certinfo.set(X509CertInfo.VALIDITY, interval);
        x509certinfo.set(X509CertInfo.EXTENSIONS, exts);
        X509CertImpl cert = new X509CertImpl(x509certinfo);
        cert.sign(caPrivateKey, sigAlgName);

        KeyPair keyPair = genKey();
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(keyPair.getPrivate());

        Certificate[] certs = null;
        String[] aliasNames = null;
        if (containCACert) {
            certs = new Certificate[] { cert, caCert };
            aliasNames = new String[] { subJKSAlias, rootAlias };
        } else {
            certs = new Certificate[] { cert };
            aliasNames = new String[] { subJKSAlias };
        }
        saveJks(aliasNames, certAndKeyGen.getPrivateKey(), subjectPasswd, certs, storePath);
    }

    private KeyStore loadKeyStore(File keyStoreFile, String storePass)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(keyStoreFile);
            keyStore.load(inputStream, storePass.toCharArray());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return keyStore;
    }

    private KeyPair genKey() throws NoSuchAlgorithmException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        kpg.initialize(1024, secureRandom);

        KeyPair kp = kpg.generateKeyPair();

        return kp;
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
