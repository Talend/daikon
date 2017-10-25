package org.talend.tqlmongo.criteria;

import org.junit.Test;
import org.talend.tqlmongo.excp.TqlMongoException;

public class TestMongoCriteria_AllFields extends TestMongoCriteria_Abstract {

    @Test(expected = TqlMongoException.class)
    public void testFieldEq() throws Exception {
        /*
         * There's no way to specify a condition on all fields and visitor has no additional metadata information so
         * it can infer field names.
         */
        doTest("* = 0");
    }
}
