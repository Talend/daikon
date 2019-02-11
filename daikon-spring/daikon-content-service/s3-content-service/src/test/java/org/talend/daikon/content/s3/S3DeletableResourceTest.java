package org.talend.daikon.content.s3;

import static org.junit.Assert.*;

import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.DeletableResourceTest;

public class S3DeletableResourceTest extends DeletableResourceTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        TestConfiguration.clientNumber.set(0);
    }

    @Test
    public void shouldNotThrowErrorWhenWriteAfterClose() throws Exception {
        // Given
        OutputStream outputStream = resource.getOutputStream();
        outputStream.write("1234".getBytes());
        outputStream.close();

        // When
        outputStream.write('a'); // No exception to be thrown

        // Then
        assertEquals("1234", IOUtils.toString(resource.getInputStream()));
    }

    @Override
    public String getUrlProtocol() {
        return "https";
    }

    @Override
    public String getURIScheme() {
        return "https";
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldGetFile() throws Exception {
        resource.getFile(); // Not supported on S3
    }

    @Test
    public void lastModifiedShouldBeComputed() throws Exception {
        assertEquals(0, resource.lastModified()); // Not implemented by S3 mock.
    }

    @Override
    public void getFilename() {
        assertEquals("app1/" + LOCATION, resource.getFilename());
    }

    @Test
    public void shouldGetDescription() {
        assertEquals("Amazon s3 resource [bucket='s3-content-service1' and object='app1/file.txt']", resource.getDescription());
    }

    @Test
    public void shouldGetDescriptionInMultiTenant() {
        // Given
        TestConfiguration.clientNumber.set(0);
        final Resource resource1 = resolver.getResource(LOCATION);

        // Then
        assertEquals("Amazon s3 resource [bucket='s3-content-service1' and object='app1/file.txt']", resource1.getDescription());

        // Given
        TestConfiguration.clientNumber.set(1);
        final Resource resource2 = resolver.getResource(LOCATION);

        // Then
        assertEquals("Amazon s3 resource [bucket='s3-content-service2' and object='app2/file.txt']", resource2.getDescription());

        // Given
        TestConfiguration.clientNumber.set(2);
        final Resource resource3 = resolver.getResource(LOCATION);

        // Then
        assertEquals("Amazon s3 resource [bucket='s3-content-service2' and object='file.txt']", resource3.getDescription());
    }

    @Test
    public void shouldStoreUsingMultiTenantClient() throws Exception {
        // Given
        TestConfiguration.clientNumber.set(0);
        final DeletableResource resource1 = resolver.getResource("mockS3_1.txt");
        try (OutputStream outputStream = resource1.getOutputStream()) {
            outputStream.write("test_tenant1".getBytes());
        }

        TestConfiguration.clientNumber.set(1);
        final DeletableResource resource2 = resolver.getResource("mockS3_2.txt");
        try (OutputStream outputStream = resource2.getOutputStream()) {
            outputStream.write("test_tenant2".getBytes());
        }

        // Then
        TestConfiguration.clientNumber.set(0);
        assertTrue(resolver.getResource("mockS3_1.txt").exists());
        assertFalse(resolver.getResource("mockS3_2.txt").exists());

        TestConfiguration.clientNumber.set(1);
        assertFalse(resolver.getResource("mockS3_1.txt").exists());
        assertTrue(resolver.getResource("mockS3_2.txt").exists());
    }
}