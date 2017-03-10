package org.talend.daikon.content.s3;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.daikon.content.DeletableResourceTest;

public class S3DeletableResourceTest extends DeletableResourceTest {

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

    @Test
    public void shouldGetDescription() throws Exception {
        assertEquals("Amazon s3 resource [bucket='s3-content-service' and object='file.txt']", resource.getDescription());
    }

}