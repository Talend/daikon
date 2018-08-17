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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;

import org.junit.Test;
import org.talend.daikon.signature.exceptions.InvalidKeyStoreException;
import org.talend.daikon.signature.exceptions.MissingEntryException;
import org.talend.daikon.signature.exceptions.NoCodeSignCertificateException;
import org.talend.daikon.signature.exceptions.UnsignedEntryException;
import org.talend.daikon.signature.exceptions.VerifyFailedException;
import org.talend.daikon.signature.keystore.KeyStoreSetting;

public class ZipVerifierTest {

    @Test // Use talend-code-vrfy.jks
    public void testVerifySignedJob() throws Exception {
        String signedJobPath = getResourceFilePath("TJava_0.1_signed.zip");
        ZipVerifier verifer = new ZipVerifier();
        boolean hasException = false;
        try {
            verifer.verify(signedJobPath);
        } catch (Exception ex) {
            hasException = true;
        }
        assertFalse(hasException);
    }

    @Test // Use talend-code-vrfy.jks
    public void testVerifyUnsignedJob() throws Exception {
        String unSignedJobPath = getResourceFilePath("TJava_0.1.zip");
        ZipVerifier verifer = new ZipVerifier();
        try {
            verifer.verify(unSignedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof MissingEntryException);
        }
    }

    @Test // Use talend-code-vrfy.jks
    public void testVerifySignedJobModifiedOneFile() throws Exception {
        String unSignedJobPath = getResourceFilePath("TJava_0.1_signed_modified.zip");
        ZipVerifier verifer = new ZipVerifier();
        try {
            verifer.verify(unSignedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof VerifyFailedException);
        }
    }

    @Test // Use talend-code-vrfy.jks
    public void testVerifySignedJobDeleteOneFile() throws Exception {
        String unSignedJobPath = getResourceFilePath("TJava_0.1_signed_delete_one_file.zip");
        ZipVerifier verifer = new ZipVerifier();
        try {
            verifer.verify(unSignedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof MissingEntryException);
        }
    }

    @Test // Use talend-code-vrfy.jks
    public void testVerifySignedJobAddOneFile() throws Exception {
        String unSignedJobPath = getResourceFilePath("TJava_0.1_signed_add_new_file.zip");
        ZipVerifier verifer = new ZipVerifier();
        try {
            verifer.verify(unSignedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof UnsignedEntryException);
        }
    }

    private String getResourceFilePath(String fileName) {
        String resourcePath = ZipVerifierTest.class.getResource(fileName).getFile();
        return resourcePath;
    }

    @Test // Use third party jks
    public void testVerifySignedValid() throws Exception {
        String signedJobPath = getResourceFilePath("signed-valid.zip");
        ZipVerifierForTest.setJksFileName("truststore.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        boolean hasException = false;
        try {
            verifer.verify(signedJobPath);
        } catch (Exception ex) {
            hasException = true;
        }
        assertFalse(hasException);
    }

    @Test // Use third party jks
    public void testVerifySignedJobByWebTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("signed-by-web.zip");
        ZipVerifierForTest.setJksFileName("truststore.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof NoCodeSignCertificateException);
        }
    }

    @Test // Use third party jks
    public void testVerifySignedJobByTruststore2() throws Exception {
        String signedJobPath = getResourceFilePath("signed-valid.zip");
        ZipVerifierForTest.setJksFileName("truststore2.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof CertPathValidatorException);
        }
    }

    @Test // Use third party jks
    public void testVerifyUnsignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("unsigned.zip");
        ZipVerifierForTest.setJksFileName("truststore.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof MissingEntryException);
        }
    }

    @Test // Use third party jks
    public void testVerifyAddedSignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("added-unsigned-file.zip");
        ZipVerifierForTest.setJksFileName("truststore.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof UnsignedEntryException);
        }
    }

    @Test // Use third party jks
    public void testVerifyModifiedSignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("modified-signed-valid.zip");
        ZipVerifierForTest.setJksFileName("truststore.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof VerifyFailedException);
        }
    }

    @Test // Use third party jks
    public void testVerifyDeletedSignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("deleted-signed-valid.zip");
        ZipVerifierForTest.setJksFileName("truststore.jks");
        ZipVerifier verifer = new ZipVerifierForTest();
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof MissingEntryException);
        }
    }
}

class ZipVerifierForTest extends ZipVerifier {

    private static String jksFileName = "truststore.jks";

    public ZipVerifierForTest() throws Exception {
        super();
    }

    protected KeyStore getKeyStore() throws InvalidKeyStoreException {
        KeyStoreSetting setting = new KeyStoreSetting();
        String verifyStorePass = "704e6a56993db27996eac284b83e"; //$NON-NLS-1$
        setting.setStoreUrl(getJKSUrl());
        setting.setStorePassword(verifyStorePass.toCharArray());
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(setting.getStoreType());
            keyStore.load(setting.getStoreUrl().openStream(), setting.getStorePassword());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new InvalidKeyStoreException("Load key store failed." + e.getMessage(), e);
        }

        return keyStore;
    }

    public static String getJksFileName() {
        return jksFileName;
    }

    public static void setJksFileName(String jksFileName) {
        ZipVerifierForTest.jksFileName = jksFileName;
    }

    private URL getJKSUrl() {
        return ZipVerifierTest.class.getResource(jksFileName);
    }
}
