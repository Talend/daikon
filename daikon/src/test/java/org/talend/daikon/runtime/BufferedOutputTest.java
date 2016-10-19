
// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.runtime;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BufferedOutputTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testBufferdWirter() throws Throwable {
        // String length bigger than the buffer size
        String strRow_1024 = getAsciiRandomString(1024) + System.lineSeparator();

        // Test Buffered Output
        String testFile_1 = tempFolder.newFile("test_buffered_writer.txt").getAbsolutePath();
        BufferedOutput buf_writer_1 = new BufferedOutput(createOutputStreamWriter(testFile_1));
        BufferedOutput buf_writer_2 = new BufferedOutput(createOutputStreamWriter(testFile_1));
        BufferedOutput buf_writer_3 = new BufferedOutput(createOutputStreamWriter(testFile_1));
        for (int i = 0; i < 100; i++) {
            buf_writer_1.write(strRow_1024);
            buf_writer_2.write(strRow_1024);
            buf_writer_3.write(strRow_1024);
        }
        flushAndClose(buf_writer_1);
        flushAndClose(buf_writer_2);
        flushAndClose(buf_writer_3);
        assertEquals(300, checkGeneratedFile(testFile_1));

        // Test BufferedWriter woule be used to reproduce the problem which we fix
        String testFile_2 = tempFolder.newFile("test_buffered_writer_output.txt").getAbsolutePath();
        BufferedWriter buf_writer_output_1 = new BufferedWriter(createOutputStreamWriter(testFile_2));
        BufferedWriter buf_writer_output_2 = new BufferedWriter(createOutputStreamWriter(testFile_2));
        BufferedWriter buf_writer_output_3 = new BufferedWriter(createOutputStreamWriter(testFile_2));
        for (int i = 0; i < 100; i++) {
            buf_writer_output_1.write(strRow_1024);
            buf_writer_output_2.write(strRow_1024);
            buf_writer_output_3.write(strRow_1024);
        }
        flushAndClose(buf_writer_output_1);
        flushAndClose(buf_writer_output_2);
        flushAndClose(buf_writer_output_3);
        // This would maybe smaller than 300
        // assertEquals(300, checkGeneratedFile(testFile_2));
    }

    protected void flushAndClose(Writer writer) throws IOException {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    private OutputStreamWriter createOutputStreamWriter(String file) throws Throwable {
        return new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
    }

    // Check the correct row which length is 1024
    private int checkGeneratedFile(String testFile) throws Throwable {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        String rowStr = null;
        int nb_line = 0;
        while ((rowStr = reader.readLine()) != null) {
            if (rowStr.length() == 1024) {
                nb_line++;
            }
        }
        reader.close();
        return nb_line;
    }

    protected String getAsciiRandomString(int length) {
        Random random = new Random();
        int cnt = 0;
        StringBuffer buffer = new StringBuffer();
        char ch;
        int end = 'z' + 1;
        int start = ' ';
        while (cnt < length) {
            ch = (char) (random.nextInt(end - start) + start);
            if (Character.isLetterOrDigit(ch)) {
                buffer.append(ch);
                cnt++;
            }
        }
        return buffer.toString();
    }
}
