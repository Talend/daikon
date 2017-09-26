package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.contains;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_Contains extends TestTqlParser_Abstract {

    @Test
    public void testApiFieldContains1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name contains 'ssen'");
        // TQL api query
        TqlElement tqlElement = contains("name", "ssen");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldContains2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name contains 'noi'");
        // TQL api query
        TqlElement tqlElement = contains("name", "noi");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldContains3() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name contains '2'");
        // TQL api query
        TqlElement tqlElement = contains("name", "2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldContains4() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name contains 'azerty'");
        // TQL api query
        TqlElement tqlElement = contains("name", "azerty");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldContains5() throws Exception {
        TqlElement expected = doTest("name contains ''");
        // TQL api query
        TqlElement tqlElement = contains("name", "");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
