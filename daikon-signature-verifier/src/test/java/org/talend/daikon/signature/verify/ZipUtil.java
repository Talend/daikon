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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtil.class);

    private static final int BUFFER_SIZE = 64 * 1024;

    public static void zipFolderRecursion(String sourceFileName, String zippedFileName) throws IOException {
        zip(sourceFileName, zippedFileName, null);
    }

    public static void zip(File sourceFile, String zippedFileName, FileFilter fileFilter) throws IOException {
        if (sourceFile.isDirectory()) {
            zips(sourceFile.listFiles(fileFilter), zippedFileName, fileFilter);
        } else {
            zips(new File[] { sourceFile }, zippedFileName, fileFilter);
        }
    }

    public static void zips(File[] sourceFile, String zippedFileName, FileFilter fileFilter) throws IOException {
        OutputStream fos = new FileOutputStream(zippedFileName);
        ZipOutputStream out = new ZipOutputStream(fos);
        try {
            zips(sourceFile, out, fileFilter);
        } finally {
            if (sourceFile.length > 0) {
                out.close();
            } else {
                fos.close();
            }
        }
    }

    private static void zips(File[] sourceFile, ZipOutputStream out, FileFilter fileFilter) throws IOException {
        for (File theFile : sourceFile) {
            zips(out, theFile, "", fileFilter); //$NON-NLS-1$
        }
    }

    private static void zips(ZipOutputStream out, File f, String base, FileFilter fileFilter) throws IOException {
        if (f.isDirectory()) {
            String newBase = base + f.getName() + '/';
            out.putNextEntry(new ZipEntry(newBase));
            for (File element : f.listFiles(fileFilter)) {
                zips(out, element, newBase, fileFilter);
            }
        } else {
            out.putNextEntry(new ZipEntry(base + f.getName()));
            InputStream in = new FileInputStream(f);

            byte[] b = new byte[BUFFER_SIZE];
            int readBytes = 0;
            while ((readBytes = in.read(b, 0, BUFFER_SIZE)) != -1) {
                out.write(b, 0, readBytes);
            }
            in.close();
            out.flush();
        }
    }

    public static void zip(String sourceFileName, String zippedFileName, FileFilter fileFilter) throws IOException {
        zip(new File(sourceFileName), zippedFileName, fileFilter);
    }

    public static void unzip(String zipFile, String location) throws IOException {
        try {
            File f = new File(location);
            if (f.exists() || !f.isDirectory()) {
                f.delete();
            }
            f.mkdirs();
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    File target = new File(location, ze.getName());
                    if (!target.getParentFile().exists()) {
                        target.getParentFile().mkdirs();
                    }

                    if (ze.isDirectory()) {
                        if (!target.isDirectory()) {
                            target.mkdirs();
                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(target.getAbsolutePath(), false);
                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
