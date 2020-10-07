package org.talend.logging.audit.impl;

import org.junit.Assert;
import org.junit.Test;

import static org.easymock.EasyMock.partialMockBuilder;

public class AbstractBackendTest {

    @Test
    public void testThatDefaultBahaviourIsToHaveMessages() {
        AbstractBackend logger = partialMockBuilder(AbstractBackend.class).createMock();
        Assert.assertTrue(logger.enableMessageFormat());
    }

}
