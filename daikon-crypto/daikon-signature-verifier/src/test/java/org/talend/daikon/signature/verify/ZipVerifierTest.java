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
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertPathValidatorException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.daikon.signature.exceptions.MissingEntryException;
import org.talend.daikon.signature.exceptions.NoValidCertificateException;
import org.talend.daikon.signature.exceptions.UnsignedArchiveException;
import org.talend.daikon.signature.exceptions.UnsignedEntryException;
import org.talend.daikon.signature.exceptions.VerifyFailedException;

public class ZipVerifierTest {

    private static String storePass = "012345"; //$NON-NLS-1$

    private static String unSignArchiveName = "unsigned.zip"; //$NON-NLS-1$

    private static File workingFolder = null;

    @BeforeClass
    public static void beforeClass() throws Exception {
        String folderPath = getResourceFilePath("");
        workingFolder = new File(folderPath, "working_folder");//$NON-NLS-1$
        if (workingFolder.exists()) {
            FileUtils.deleteDirectory(workingFolder);
        }
        workingFolder.mkdirs();
        SignedFileGenerater generater = new SignedFileGenerater(workingFolder.getAbsolutePath(),
                new File(workingFolder, unSignArchiveName), storePass);
        generater.generateSignedFiles();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.deleteDirectory(workingFolder);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testVerifySignedValid() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("signed-valid.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        Exception exception = null;
        try {
            verifer.verify(signedJobPath);
        } catch (Exception ex) {
            exception = ex;
        }
        assertTrue(exception == null);
    }

    @Test
    public void testVerifySignedByExpired() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("signed-expired.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        verifer.verify(signedJobPath);
    }
    
    @Test
    public void testVerifySignedByApkSignWithExpiredCert() throws Exception {
        String signedJobPath = getApkSignerResourceFilePath("signed-by-apksigner-expired.zip");
        String keyStorePath = getApkSignerResourceFilePath("truststore.jks");
        String storePassForApkSigner = "c1b966f70a2529d8adc13e13d293"; //$NON-NLS-1$
        
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePassForApkSigner);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof NoValidCertificateException);
        }
        verifer.setCheckSignatureTimestamp(false);
        verifer.verify(signedJobPath);
    }

    @Test
    public void testVerifySignedJobByWebTruststore() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("signed-by-zip.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof VerifyFailedException);
        }
    }

    @Test
    public void testVerifySignedJobByTruststore2() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("signed-valid.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore2.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof CertPathValidatorException);
        }
    }

    @Test
    public void testVerifyUnsignedJobByTruststore() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("unsigned.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof UnsignedArchiveException);
        }
    }

    @Test
    public void testVerifyAddedSignedJobByTruststore() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("added-unsigned-file.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof UnsignedEntryException);
        }
    }

    @Test
    public void testVerifyModifiedSignedJobByTruststore() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("modified-signed-valid.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof VerifyFailedException);
        }
    }

    @Test
    public void testVerifyDeletedSignedJobByTruststore() throws Exception {
        String signedJobPath = getPathFromWorkingFolder("deleted-signed-valid.zip");
        String keyStorePath = getPathFromWorkingFolder("truststore.jks");
        InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
        ZipVerifier verifer = new ZipVerifier(keyStoreInputStream, storePass);
        try {
            verifer.verify(signedJobPath);
            fail("exception should have been thrown in the previous line");
        } catch (VerifyFailedException ex) {
            assertTrue(ex.getCause() instanceof MissingEntryException);
        }
    }

    private static String getPathFromWorkingFolder(String fileName) {
        File file = new File(workingFolder, fileName);
        return file.getAbsolutePath();
    }

    private static String getResourceFilePath(String fileName) {
        String resourcePath = ZipVerifierTest.class.getResource(fileName).getFile();
        return resourcePath;
    }
    
    private static String getApkSignerResourceFilePath(String fileName) {
        String resourcesFolder = ZipVerifierTest.class.getClassLoader().getResource("apkSigner").getFile();
        String resourcePath = resourcesFolder + File.separator + fileName;
        return resourcePath;
    }

    private InputStream getKeyStoreInputStream(String path) throws FileNotFoundException {
        File keyStoreFile = new File(path);
        return new FileInputStream(keyStoreFile);
    }
}
