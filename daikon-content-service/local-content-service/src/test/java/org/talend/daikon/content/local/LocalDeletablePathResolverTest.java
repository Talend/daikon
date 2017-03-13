package org.talend.daikon.content.local;

import org.springframework.test.context.TestPropertySource;
import org.talend.daikon.content.DeletableResourceLoaderTest;

@TestPropertySource(properties = { "content-service.store=local", "content-service.store.local.path=${java.io.tmpdir}/dataprep" })
public class LocalDeletablePathResolverTest extends DeletableResourceLoaderTest {
    // All standard test
}
