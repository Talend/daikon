package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.neq;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_Boolean extends TestTqlParser_Abstract {

    @Test
    public void testApiLiteralComparisonEqTrue() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 = true");
        // TQL api query
        TqlElement tqlElement = eq("field1", true);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiLiteralComparisonNeqTrue() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != true");
        // TQL api query
        TqlElement tqlElement = neq("field1", true);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
