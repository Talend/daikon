package org.talend.daikon.content.s3;

import org.junit.Test;
import org.talend.daikon.content.DeletableResource;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class FixedURLS3ResourceTest {

    @Test
    public void shouldHaveFixedUrl() throws IOException {
        // Given
        final DeletableResource delegate = mock(DeletableResource.class);
        FixedURLS3Resource resource = new FixedURLS3Resource("http://fake.io:9001", "mybucket/file.csv", delegate);

        // When
        final URL url = resource.getURL();

        // Then
        assertUrl(delegate, url);
    }

    @Test
    public void shouldHaveFixedUrlWithLeadingSlash() throws IOException {
        // Given
        final DeletableResource delegate = mock(DeletableResource.class);
        FixedURLS3Resource resource = new FixedURLS3Resource("http://fake.io:9001", "/mybucket/file.csv", delegate);

        // When
        final URL url = resource.getURL();

        // Then
        assertUrl(delegate, url);
    }

    @Test
    public void shouldHaveFixedUri() throws IOException {
        // Given
        final DeletableResource delegate = mock(DeletableResource.class);
        FixedURLS3Resource resource = new FixedURLS3Resource("http://fake.io:9001", "mybucket/file.csv", delegate);

        // When
        final URI uri = resource.getURI();

        // Then
        assertUri(delegate, uri);
    }

    @Test
    public void shouldHaveFixedUriWithLeadingSlash() throws IOException {
        // Given
        final DeletableResource delegate = mock(DeletableResource.class);
        FixedURLS3Resource resource = new FixedURLS3Resource("http://fake.io:9001", "/mybucket/file.csv", delegate);

        // When
        final URI uri = resource.getURI();

        // Then
        assertUri(delegate, uri);
    }

    private void assertUrl(DeletableResource delegate, URL url) throws IOException {
        // Then
        verify(delegate, never()).getURL();
        assertEquals("http", url.getProtocol());
        assertEquals("fake.io", url.getHost());
        assertEquals(9001, url.getPort());
        assertEquals("/mybucket/file.csv", url.getPath());
    }

    private void assertUri(DeletableResource delegate, URI uri) throws IOException {
        // Then
        verify(delegate, never()).getURL();
        assertEquals("http", uri.getScheme());
        assertEquals("fake.io", uri.getHost());
        assertEquals(9001, uri.getPort());
        assertEquals("/mybucket/file.csv", uri.getPath());
    }
}