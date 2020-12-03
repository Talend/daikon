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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertPathValidatorException;

import org.junit.Test;
import org.talend.daikon.signature.exceptions.MissingEntryException;
import org.talend.daikon.signature.exceptions.UnsignedArchiveException;
import org.talend.daikon.signature.exceptions.UnsignedEntryException;
import org.talend.daikon.signature.exceptions.VerifyFailedException;

public class ZipVerifierWithExpiredCertTest {

    private String storePass = "c1b966f70a2529d8adc13e13d293"; //$NON-NLS-1$

    /**
     * signed-valid.zip contain two certificates 1. code sign certificate validity: [From: Mon Aug 20 16:13:06 CST 2018,
     * To: Sat Feb 16 16:13:06 CST 2019] 2. validity: [From: Mon Aug 20 16:13:05 CST 2018, To: Thu Aug 15 16:13:05 CST
     * 2019]
     * 
     * @throws Exception
     */
    @Test
    public void testVerifySignedValid() throws Exception {
        String signedJobPath = getResourceFilePath("signed-valid.zip");
        String keyStorePath = getResourceFilePath("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        Exception exception = null;
        try {
            verifer.verify(signedJobPath);
        } catch (Exception ex) {
            exception = ex;
        }
        assertTrue(exception == null);
    }

    @Test
    public void testVerifySignedJobByWebTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("signed-by-zip.zip");
        String keyStorePath = getResourceFilePath("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof VerifyFailedException);
        }
    }

    @Test
    public void testVerifySignedJobByTruststore2() throws Exception {
        String signedJobPath = getResourceFilePath("signed-valid.zip");
        String keyStorePath = getResourceFilePath("truststore2.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof CertPathValidatorException);
        }
    }

    @Test
    public void testVerifyUnsignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("unsigned.zip");
        String keyStorePath = getResourceFilePath("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof UnsignedArchiveException);
        }
    }

    @Test
    public void testVerifyAddedSignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("added-unsigned-file.zip");
        String keyStorePath = getResourceFilePath("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof UnsignedEntryException);
        }
    }

    @Test
    public void testVerifyModifiedSignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("modified-signed-valid.zip");
        String keyStorePath = getResourceFilePath("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof VerifyFailedException);
        }
    }

    @Test
    public void testVerifyDeletedSignedJobByTruststore() throws Exception {
        String signedJobPath = getResourceFilePath("deleted-signed-valid.zip");
        String keyStorePath = getResourceFilePath("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.setCheckSignatureTimestamp(false);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof MissingEntryException);
        }
    }

    private String getResourceFilePath(String fileName) {
        String resourcePath = ZipVerifierWithExpiredCertTest.class.getResource(fileName).getFile();
        return resourcePath;
    }

    private InputStream getKeyStoreInputStream(String path) throws FileNotFoundException {
        File keyStoreFile = new File(path);
        return new FileInputStream(keyStoreFile);
    }
}
