package org.talend.daikon.content.s3;

import org.springframework.boot.SpringBootConfiguration;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.DeletableResourceLoaderTest;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@SpringBootConfiguration
public class S3DeletablePathResolverTest extends DeletableResourceLoaderTest {

    @Override
    protected void assertGetResources(DeletableResource[] resources) {
        super.assertGetResources(resources);
        assertTrue(Arrays.stream(resources).allMatch(r -> r instanceof FixedURLS3Resource));
    }
}
