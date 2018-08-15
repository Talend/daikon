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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.net.URL;
import java.security.KeyStore;
import org.junit.Test;
import org.talend.daikon.signature.exceptions.VerifyFailedException;
import org.talend.daikon.signature.keystore.KeyStoreManager;
import org.talend.daikon.signature.keystore.KeyStoreSetting;

public class ZipVerifierTest {

    @Test // Use talend-code-vrfy.jks
    public void testVerifySignedJob() throws Exception {
        String signedJobPath = getResourceFilePath("TJava_0.1_signed.zip");
        ZipVerifier verifer = new ZipVerifier();
        verifer.verify(signedJobPath);
    }

    @Test // Use talend-code-vrfy.jks
    public void testVerifyUnsignedJob() throws Exception {
        String unSignedJobPath = getResourceFilePath("TJava_0.1.zip");
        ZipVerifier verifer = new ZipVerifier();
        try {
            verifer.verify(unSignedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertEquals("Verify failed.Missing entry:META-INF/MANIFEST.MF", ex.getMessage());
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
            assertTrue(ex.getMessage().contains("Verify failed.SHA-256 digest error"));
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
            assertTrue(ex.getMessage().contains("Missing entry"));
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
            assertTrue(ex.getMessage().contains("Verify failed.Find unsigned entry"));
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
        verifer.verify(signedJobPath);
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
            assertTrue(ex.getMessage().contains("Verify failed."));
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
            assertTrue(ex.getMessage().contains("Verify failed."));
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
            assertTrue(ex.getMessage().contains("Verify failed.Missing entry:META-INF/MANIFEST.MF"));
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
            assertTrue(ex.getMessage().contains("Verify failed."));
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
            assertTrue(ex.getMessage().contains("Verify failed.SHA-256 digest error"));
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
            assertTrue(ex.getMessage().contains("Missing entry"));
        }
    }
}

class ZipVerifierForTest extends ZipVerifier {

    private static String jksFileName = "truststore.jks";

    public ZipVerifierForTest() throws Exception {
        super();
    }

    protected KeyStore getKeyStore() throws Exception {
        KeyStoreSetting setting = new KeyStoreSetting();
        String verifyStorePass = "704e6a56993db27996eac284b83e"; //$NON-NLS-1$
        setting.setStoreUrl(getJKSUrl());
        setting.setStorePassword(verifyStorePass.toCharArray());
        KeyStore keyStore = KeyStore.getInstance(setting.getStoreType());
        keyStore.load(setting.getStoreUrl().openStream(), setting.getStorePassword());
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
