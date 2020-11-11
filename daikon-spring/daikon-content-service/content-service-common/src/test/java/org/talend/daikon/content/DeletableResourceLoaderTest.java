package org.talend.daikon.content;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public abstract class DeletableResourceLoaderTest extends DeletableLoaderResourceTests {

    @After
    public void after() throws IOException {
        resolver.clear("/**");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNull() {
        resolver.getResource(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyString() {
        resolver.getResource("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnSpaceString() {
        resolver.getResource("  ");
    }

    @Test
    public void shouldGetCurrentClassloader() {
        assertEquals(Thread.currentThread().getContextClassLoader(), resolver.getClassLoader());
    }

    @Test
    public void shouldCreateDirectoriesForResource() throws Exception {
        // Given
        final DeletableResource resource = resolver.getResource("/path/to/file/test");
        try (OutputStream outputStream = resource.getOutputStream()) {
            outputStream.write("test content".getBytes());
        }

        // Then
        assertTrue(resolver.getResource("/path/to/file/test").exists());
        assertEquals("test content", IOUtils.toString(resolver.getResource("/path/to/file/test").getInputStream()));
    }

    @Test
    public void getResource() throws Exception {
        // Given
        final DeletableResource resource = resolver.getResource("test.log");
        try (OutputStream outputStream = resource.getOutputStream()) {
            outputStream.write("test content".getBytes());
        }
        assertTrue(resource.exists());

        // When
        resource.delete();

        // Then
        assertFalse(resolver.getResource("test.log").exists());
    }

    @Test
    public void getResources() throws Exception {
        // Given
        final DeletableResource resource1 = resolver.getResource("test1.log");
        try (OutputStream outputStream = resource1.getOutputStream()) {
            outputStream.write("test content".getBytes());
        }

        final DeletableResource resource2 = resolver.getResource("test2.log");
        try (OutputStream outputStream = resource2.getOutputStream()) {
            outputStream.write("test content".getBytes());
        }

        // When
        final DeletableResource[] resources = resolver.getResources("test*.log");

        // Then
        assertGetResources(resources);
    }

    protected void assertGetResources(DeletableResource[] resources) {
        assertTrue( //
                Stream.of(resources) //
                        .filter(r -> r.getFilename() != null) //
                        .allMatch(r -> r.getFilename().endsWith("test1.log") //
                                || r.getFilename().endsWith("test2.log") //
                        ) //
        );
    }

    @Test
    public void getClassLoader() {
        assertEquals(Thread.currentThread().getContextClassLoader(), resolver.getClassLoader());
    }

    @Test
    public void shouldClear() throws Exception {
        // Given
        createFile("file1.txt");
        createFile("file2.txt");
        assertTrue(resolver.getResource("file1.txt").exists());
        assertTrue(resolver.getResource("file2.txt").exists());

        // When
        resolver.clear("/**");

        // Then
        assertFalse(resolver.getResource("file1.txt").exists());
        assertFalse(resolver.getResource("file2.txt").exists());
    }

    private void createFile(String fileName) throws IOException {
        final DeletableResource file = resolver.getResource(fileName);
        try (OutputStream outputStream = file.getOutputStream()) {
            outputStream.write("content".getBytes());
        }
    }
}
