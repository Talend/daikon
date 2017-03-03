package org.talend.daikon.content;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class DeletableResourceTest extends DeletableLoaderResourceTests {

    private static final String LOCATION = "file.txt";

    protected DeletableResource resource;

    @Before
    public void setUp() throws Exception {
        resource = resolver.getResource(LOCATION);
        try (OutputStream outputStream = resource.getOutputStream()) {
            outputStream.write("test".getBytes());
        }
    }

    @After
    public void tearDown() throws Exception {
        try {
            resource.delete();
        } catch (IOException e) {
            // Ignored
        }
    }

    @Test
    public void delete() throws Exception {
        resource.delete();
        assertFalse(resolver.getResource(LOCATION).exists());
    }

    @Test
    public void move() throws Exception {
        // When
        resource.move("newLocation.txt");

        // Then
        assertFalse(resolver.getResource(LOCATION).exists());
        assertTrue(resolver.getResource("newLocation.txt").exists());
        assertEquals("test", IOUtils.toString(resolver.getResource("newLocation.txt").getInputStream()));
    }

    @Test
    public void exists() throws Exception {
        assertTrue(resource.exists());
    }

    @Test
    public void isReadable() throws Exception {
        assertTrue(resource.isReadable());
    }

    @Test
    public void isOpen() throws Exception {
        assertFalse(resource.isOpen());
    }

    @Test
    public abstract void getURL() throws Exception;

    @Test
    public abstract void getURI() throws Exception;

    @Test
    public abstract void getFile() throws Exception;

    @Test
    public void contentLength() throws Exception {
        // When
        try (OutputStream outputStream = resource.getOutputStream()) {
            outputStream.write("test".getBytes());
        }

        // Then
        assertEquals(4, resolver.getResource(LOCATION).contentLength());
    }

    @Test
    public abstract void lastModified() throws Exception;

    @Test
    public void createRelative() throws Exception {
        // When
        final DeletableResource root = resolver.getResource("/test/");
        final DeletableResource relative = (DeletableResource) root.createRelative("side.txt");
        try (OutputStream outputStream = relative.getOutputStream()) {
            outputStream.write("test".getBytes());
        }

        // Then
        final DeletableResource side = resolver.getResource("/test/side.txt");
        assertTrue(side.exists());
    }

    @Test
    public void getFilename() throws Exception {
        assertEquals(LOCATION, resource.getFilename());
    }

    @Test
    public abstract void getDescription() throws Exception;

    @Test
    public void getInputStream() throws Exception {
        assertEquals("test", IOUtils.toString(resolver.getResource(LOCATION).getInputStream()));
    }

    @Test
    public void isWritable() throws Exception {
        assertTrue(resource.isWritable());
    }

    @Test
    public void getOutputStream() throws Exception {
        try (OutputStream outputStream = resource.getOutputStream()) {
            assertNotNull(outputStream);
        }
    }

}
