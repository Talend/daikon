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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignedFileGenerater {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignedFileGenerater.class);

    private CertificateGenerater certTool;

    private String workspacePath;

    private String archiveFileName;

    private String validArchiveName = "signed-valid.zip";

    private String expiredArchiveName = "signed-expired.zip";

    private String noUsageArchiveName = "signed-by-zip.zip";

    private String validArchiveAddedName = "added-unsigned-file.zip";

    private String validArchiveDeletedName = "deleted-signed-valid.zip";

    private String validArchiveModifiedName = "modified-signed-valid.zip";

    private String toModifyFileName = "jobInfo.properties";

    public SignedFileGenerater(String workspacePath, File unSignArchive, String storePass) {
        this.workspacePath = workspacePath;
        this.archiveFileName = unSignArchive.getName();
        certTool = new CertificateGenerater(workspacePath, storePass);
    }

    private String getSignCommand(boolean isStrict, String filePath, String jksPath) {
        StringBuffer sb = new StringBuffer();
        sb.append("jarsigner ");
        if (isStrict) {
            sb.append("-strict ");
        }
        sb.append("-storepass ").append(certTool.getSubJKSKeyPass().toCharArray()).append(" ");
        sb.append("").append(filePath).append(" ");
        sb.append(" -keystore ").append(jksPath).append(" ");
        sb.append(certTool.getSubJKSAlias());
        return sb.toString();
    }

    private void signUseValidCert() throws IOException {
        copyFile(workspacePath, archiveFileName, validArchiveName);
        String command = getSignCommand(true, new File(workspacePath, validArchiveName).getName(),
                new File(workspacePath, certTool.getCodeSignJksValidPath()).getName());
        executeCommand(command, workspacePath);
    }

    private void signUseExpiredCert() throws IOException {
        copyFile(workspacePath, archiveFileName, expiredArchiveName);
        String command = getSignCommand(true, new File(workspacePath, expiredArchiveName).getName(),
                new File(workspacePath, certTool.getCodeSignJksExpiredPath()).getName());
        executeCommand(command, workspacePath);
    }

    private void signUserNoUsageCert() throws IOException {
        copyFile(workspacePath, archiveFileName, noUsageArchiveName);
        String command = getSignCommand(true, new File(workspacePath, noUsageArchiveName).getName(),
                new File(workspacePath, certTool.getCodeSignJksNoUsagePath()).getName());
        executeCommand(command, workspacePath);
    }

    private void prepareAddFileAfterSigned() throws IOException {
        String tempFolderName = "temp_folder";
        File toUnzipFile = new File(workspacePath, this.validArchiveName);
        File tempFolder = new File(workspacePath, tempFolderName);
        if (tempFolder.exists()) {
            FileUtils.deleteDirectory(tempFolder);
        }
        ZipUtil.unzip(toUnzipFile.getAbsolutePath(), tempFolder.getAbsolutePath());
        File toAddFile = new File(tempFolder.getAbsolutePath(), "addedFile.txt");
        toAddFile.createNewFile();
        ZipUtil.zipFolderRecursion(tempFolder.getAbsolutePath(),
                new File(workspacePath, validArchiveAddedName).getAbsolutePath());
    }

    private void prepareUnsignedFile() throws IOException {
        String tempFolderName = "temp_folder";
        File tempFolder = new File(workspacePath, tempFolderName);
        if (tempFolder.exists()) {
            FileUtils.deleteDirectory(tempFolder);
        }
        tempFolder.mkdirs();
        File readMeFile = new File(tempFolder, "readme.txt");
        readMeFile.createNewFile();
        FileUtils.writeStringToFile(readMeFile, RandomStringUtils.randomAlphabetic(256), "utf-8");
        File toModifyFile = new File(tempFolder, toModifyFileName);
        toModifyFile.createNewFile();
        FileUtils.writeStringToFile(toModifyFile, RandomStringUtils.randomAlphabetic(256), "utf-8");
        ZipUtil.zipFolderRecursion(tempFolder.getAbsolutePath(), new File(workspacePath, archiveFileName).getAbsolutePath());
    }

    private void prepareDeleteFileAfterSigned() throws IOException {
        String tempFolderName = "temp_folder";
        File toUnzipFile = new File(workspacePath, this.validArchiveName);
        File tempFolder = new File(workspacePath, tempFolderName);
        if (tempFolder.exists()) {
            FileUtils.deleteDirectory(tempFolder);
        }
        ZipUtil.unzip(toUnzipFile.getAbsolutePath(), tempFolder.getAbsolutePath());
        File toDeleteFile = new File(tempFolder.getAbsolutePath(), toModifyFileName);
        toDeleteFile.delete();
        ZipUtil.zipFolderRecursion(tempFolder.getAbsolutePath(),
                new File(workspacePath, validArchiveDeletedName).getAbsolutePath());
    }

    private void prepareModifyFileAfterSigned() throws IOException {
        String tempFolderName = "temp_folder";
        File toUnzipFile = new File(workspacePath, this.validArchiveName);
        File tempFolder = new File(workspacePath, tempFolderName);
        if (tempFolder.exists()) {
            FileUtils.deleteDirectory(tempFolder);
        }
        ZipUtil.unzip(toUnzipFile.getAbsolutePath(), tempFolder.getAbsolutePath());
        File toModifyFile = new File(tempFolder.getAbsolutePath(), toModifyFileName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(toModifyFile);
            SimpleDateFormat format = new SimpleDateFormat();
            String time = format.format(new Date());
            writer.write("\n\t" + time);
        } catch (IOException e) {
            LOGGER.error("Modify file failed." + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("Close file writer failed." + e.getMessage());
                }
            }
        }
        ZipUtil.zipFolderRecursion(tempFolder.getAbsolutePath(),
                new File(workspacePath, validArchiveModifiedName).getAbsolutePath());
    }

    private void copyFile(String folderPath, String sourceName, String targetName) throws IOException {
        File destFile = new File(folderPath, targetName);
        if (destFile.exists()) {
            destFile.delete();
        }
        Files.copy(new File(folderPath, sourceName).toPath(), destFile.toPath());
    }

    public File getValidArchiveFile() {
        return new File(workspacePath, validArchiveName);
    }

    public File getExpiredArchiveFile() {
        return new File(workspacePath, expiredArchiveName);
    }

    public File getNoUsageArchiveFile() {
        return new File(workspacePath, noUsageArchiveName);
    }

    public File getValidArchiveAddedFile() {
        return new File(workspacePath, validArchiveAddedName);
    }

    public File getValidArchiveDeletedFile() {
        return new File(workspacePath, validArchiveDeletedName);
    }

    public File getValidArchiveModifiedFile() {
        return new File(workspacePath, validArchiveModifiedName);
    }

    public String getKeystorePass() {
        return certTool.getSubJKSKeyPass();
    }

    public String getTrustStorePath() {
        return certTool.getTrustStorePath();
    }

    public String getTrustStoreTwoPath() {
        return certTool.getTrustStoreTwoPath();
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public void generateSignedFiles() throws Exception {
        certTool.generateCertificate();
        LOGGER.debug("Generated certificate");
        prepareUnsignedFile();
        LOGGER.debug("Generated unsigned file");
        signUseValidCert();
        LOGGER.debug("Signed file use valid certificate");
        signUseExpiredCert();
        LOGGER.debug("Signed file use expired certificate");
        signUserNoUsageCert();
        LOGGER.debug("Signed file use no usage certificate");
        prepareAddFileAfterSigned();
        LOGGER.debug("Added one file after signed");
        prepareDeleteFileAfterSigned();
        LOGGER.debug("Deleted one file after signed");
        prepareModifyFileAfterSigned();
        LOGGER.debug("Modified one file after signed");
    }

    private void executeCommand(String command, String execDir) {
        RuntimeExecutor executor = new RuntimeExecutor();
        executor.exec(command, new File(execDir));
    }
}

class RuntimeExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExecutor.class);

    public void exec(String command, File workspacePath) {
        if (command == null || command.trim().equals("")) {
            return;
        }
        try {
            LOGGER.info("Exec " + command);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command, null, workspacePath);
            // error
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), true);
            // output
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), false);
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            // any error
            int exitVal = process.waitFor();
            LOGGER.info("ExitValue: " + exitVal);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}

class StreamGobbler extends Thread {

    protected static final Logger LOGGER = LoggerFactory.getLogger(StreamGobbler.class);

    private InputStream in;

    private boolean isErrorPipe;

    StreamGobbler(InputStream in, boolean isErrorPipe) {
        this.in = in;
        this.isErrorPipe = isErrorPipe;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (isErrorPipe) {
                    LOGGER.debug(line);
                } else {
                    LOGGER.debug(line);
                }

            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
