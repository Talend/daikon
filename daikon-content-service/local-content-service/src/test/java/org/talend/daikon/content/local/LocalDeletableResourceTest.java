package org.talend.daikon.content.local;

import static org.junit.Assert.*;

import org.springframework.test.context.TestPropertySource;
import org.talend.daikon.content.DeletableResourceTest;

@TestPropertySource(properties = { "content-service.store=local", "content-service.store.local.path=${java.io.tmpdir}/dataprep" })
public class LocalDeletableResourceTest extends DeletableResourceTest {

    @Override
    public void getURL() throws Exception {
        assertEquals("file", resource.getURL().getProtocol());
    }

    @Override
    public void getURI() throws Exception {
        assertEquals("file", resource.getURI().getScheme());
    }

    @Override
    public void getFile() throws Exception {
        assertNotNull(resource.getFile());
    }

    @Override
    public void lastModified() throws Exception {
        assertTrue(resource.lastModified() > 0);
    }

    @Override
    public void getDescription() throws Exception {
        assertTrue(resource.getDescription().contains("file.txt"));
    }

}
