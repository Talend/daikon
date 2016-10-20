
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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

import org.junit.Test;

public class BufferedLineWriterTest {

    @Test
    public void testBufferedLineWriter() throws Throwable {
        // String length bigger than the buffer size
        String strRow_1024 = getAsciiRandomString(1024) + System.lineSeparator();

        // Test BufferedLineWriter
        StringWriter stringWriter_1 = new StringWriter();
        BufferedLineWriter buf_line_writer_1 = new BufferedLineWriter(stringWriter_1);
        BufferedLineWriter buf_line_writer_2 = new BufferedLineWriter(stringWriter_1);
        BufferedLineWriter buf_line_writer_3 = new BufferedLineWriter(stringWriter_1);
        for (int i = 0; i < 100; i++) {
            buf_line_writer_1.write(strRow_1024);
            buf_line_writer_2.write(strRow_1024);
            buf_line_writer_3.write(strRow_1024);
        }
        flushAndClose(buf_line_writer_1);
        flushAndClose(buf_line_writer_2);
        flushAndClose(buf_line_writer_3);
        assertEquals(300, checkGeneratedFile(stringWriter_1.toString()));

        // Test BufferedWriter would be used to reproduce the problem which we fix
        StringWriter stringWriter_2 = new StringWriter();
        BufferedWriter buf_writer_1 = new BufferedWriter(stringWriter_2);
        BufferedWriter buf_writer_2 = new BufferedWriter(stringWriter_2);
        BufferedWriter buf_writer_3 = new BufferedWriter(stringWriter_2);
        for (int i = 0; i < 100; i++) {
            buf_writer_1.write(strRow_1024);
            buf_writer_2.write(strRow_1024);
            buf_writer_3.write(strRow_1024);
        }
        flushAndClose(buf_writer_1);
        flushAndClose(buf_writer_2);
        flushAndClose(buf_writer_3);
        // This would maybe smaller than 300
        // assertEquals(300, checkGeneratedFile(stringWriter_2.toString()));
    }

    protected void flushAndClose(Writer writer) throws IOException {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    // Check the correct row which length is 1024
    private int checkGeneratedFile(String testStr) throws Throwable {
        BufferedReader reader = new BufferedReader(new StringReader(testStr));
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
