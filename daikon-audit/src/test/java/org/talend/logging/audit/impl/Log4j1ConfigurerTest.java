package org.talend.logging.audit.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.*;
import org.talend.logging.audit.LogAppenders;

/**
 *
 */
public class Log4j1ConfigurerTest {

    private static final List<AuditConfigEntry> AUDIT_ENTRIES = new ArrayList<>();

    @BeforeClass
    public static void setupSuite() {
        AUDIT_ENTRIES.clear();
        for (AuditConfiguration c : AuditConfiguration.values()) {
            AuditConfigEntry e = new AuditConfigEntry(c, c.getValue(), c.getAlreadySet());
            AUDIT_ENTRIES.add(e);
        }
    }

    @AfterClass
    public static void cleanupSuite() {
        for (AuditConfigEntry e : AUDIT_ENTRIES) {
            e.entry.setAlreadySet(false);
            e.entry.setValue(e.value, e.entry.getClz());
            e.entry.setAlreadySet(e.alreadySet);
        }
    }

    @Before
    public void setupTest() {
        for (AuditConfiguration c : AuditConfiguration.values()) {
            c.setAlreadySet(false);
        }
    }

    /**
     * This test logs non-standard char using non-standard encoding and checks that this char is translated correctly
     */
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testFileAppenderEncoding() throws Exception {
        final File tempFile = Files.createTempFile("audit", "test").toFile();
        tempFile.deleteOnExit();

        final String cat = AuditConfiguration.ROOT_LOGGER.getString();
        final Logger logger = Logger.getLogger(cat);

        logger.removeAllAppenders();

        final LogAppendersSet logAppenders = new LogAppendersSet();
        logAppenders.add(LogAppenders.FILE);

        AuditConfiguration.APPLICATION_NAME.setValue("test", String.class);
        AuditConfiguration.LOG_APPENDER.setValue(logAppenders, LogAppendersSet.class);
        AuditConfiguration.APPENDER_FILE_PATH.setValue(tempFile.toString(), String.class);
        AuditConfiguration.ENCODING.setValue("ASCII", String.class);

        Log4j1Configurer.configure();

        Logger.getLogger(cat + ".test").info("Test message: Ы");

        Thread.sleep(500);

        byte[] bytes = Files.readAllBytes(tempFile.toPath());
        Arrays.sort(bytes);

        int ind = Arrays.binarySearch(bytes, (byte) 0x3f);

        Assert.assertTrue(ind >= 0);
    }
}
