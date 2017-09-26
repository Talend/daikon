package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.in;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_In extends TestTqlParser_Abstract {

    @Test
    public void testApiFieldInQuoted1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in ['value1']");
        // TQL api query
        TqlElement tqlElement = in("field1", "value1");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInQuoted2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in ['value1', 'value2']");
        // TQL api query
        TqlElement tqlElement = in("field1", "value1", "value2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInQuoted5() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in ['value1', 'value2', 'value3', 'value4', 'value5']");
        // TQL api query
        TqlElement tqlElement = in("field1", "value1", "value2", "value3", "value4", "value5");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInInt1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in [11]");
        // TQL api query
        TqlElement tqlElement = in("field1", 11);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInInt2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in [11, 12, 13]");
        // TQL api query
        TqlElement tqlElement = in("field1", 11, 12, 13);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInDouble1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in [11.12]");
        // TQL api query
        TqlElement tqlElement = in("field1", 11.12);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInDouble2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in [11.12, 12.45, 13.788]");
        // TQL api query
        TqlElement tqlElement = in("field1", 11.12, 12.45, 13.788);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldInBoolean() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 in [true, false]");
        // TQL api query
        TqlElement tqlElement = in("field1", true, false);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
