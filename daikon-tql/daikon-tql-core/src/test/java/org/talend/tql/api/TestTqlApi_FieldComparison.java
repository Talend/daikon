package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.*;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_FieldComparison extends TestTqlParser_Abstract {

    @Test
    public void testApiFieldComparison_numericField() throws Exception {

        // TQL native query
        TqlElement expected = doTest("0001='value'");

        // TQL api query
        TqlElement tqlElement = eq("0001", "value");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiFieldComparison_eq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 = field(field2)");

        // TQL api query
        TqlElement tqlElement = eqFields("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiStringComparison_eq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 = 'field2'");

        // TQL api query
        TqlElement tqlElement = eq("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiIntComparison_eq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 = 1");

        // TQL api query
        TqlElement tqlElement = eq("field1", 1);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiDoubleComparison_eq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 = 123.456");

        // TQL api query
        TqlElement tqlElement = eq("field1", 123.456);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiFieldComparison_neq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 != field(field2)");

        // TQL api query
        TqlElement tqlElement = neqFields("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiStringComparison_neq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 != 'field2'");

        // TQL api query
        TqlElement tqlElement = neq("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiIntComparison_neq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 != 1");

        // TQL api query
        TqlElement tqlElement = neq("field1", 1);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiDoubleComparison_neq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 != 1.45");

        // TQL api query
        TqlElement tqlElement = neq("field1", 1.45);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiBooleanComparison_neq() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 != true");

        // TQL api query
        TqlElement tqlElement = neq("field1", true);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiIntComparison_lt() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 < 1");

        // TQL api query
        TqlElement tqlElement = lt("field1", 1);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiDoubleComparison_lt() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 < 123.456");

        // TQL api query
        TqlElement tqlElement = lt("field1", 123.456);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiFieldComparison_lt() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 < field(f2)");

        // TQL api query
        TqlElement tqlElement = ltFields("field1", "f2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiFieldComparison_lte() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 <= field(field2)");

        // TQL api query
        TqlElement tqlElement = lteFields("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiIntComparison_lte() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 <= 2");

        // TQL api query
        TqlElement tqlElement = TqlBuilder.lte("field1", 2);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiDoubleComparison_lte() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 <= 123.456");

        // TQL api query
        TqlElement tqlElement = lte("field1", 123.456);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiFieldComparison_gte() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 >= field(field2)");

        // TQL api query
        TqlElement tqlElement = gteFields("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiIntComparison_gte() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 >= 1");

        // TQL api query
        TqlElement tqlElement = gte("field1", 1);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiDoubleComparison_gte() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 >= 123.456");

        // TQL api query
        TqlElement tqlElement = TqlBuilder.gte("field1", 123.456);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiFieldComparison_gt() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 > field(field2)");

        // TQL api query
        TqlElement tqlElement = gtFields("field1", "field2");

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiIntComparison_gt() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 > 1");

        // TQL api query
        TqlElement tqlElement = gt("field1", 1);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

    @Test
    public void testApiDoubleComparison_gt() throws Exception {

        // TQL native query
        TqlElement expected = doTest("field1 > 123.456");

        // TQL api query
        TqlElement tqlElement = gt("field1", 123.456);

        Assert.assertEquals(expected.toString(), tqlElement.toString());

    }

}
