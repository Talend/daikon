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

    @Test
    public void getURL() throws Exception {
        assertEquals("https", resource.getURL().getProtocol());
    }

    @Test
    public void getURI() throws Exception {
        assertEquals("https", resource.getURI().getScheme());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getFile() throws Exception {
        resource.getFile(); // Not supported on S3
    }

    @Test
    public void lastModified() throws Exception {
        assertEquals(0, resource.lastModified()); // Not implemented by S3 mock.
    }

    @Test
    public void getDescription() throws Exception {
        assertEquals("Amazon s3 resource [bucket='s3-content-service' and object='file.txt']", resource.getDescription());
    }

}