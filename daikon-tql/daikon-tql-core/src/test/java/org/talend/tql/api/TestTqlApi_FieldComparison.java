package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.eqFields;
import static org.talend.tql.api.TqlBuilder.gt;
import static org.talend.tql.api.TqlBuilder.gtFields;
import static org.talend.tql.api.TqlBuilder.gte;
import static org.talend.tql.api.TqlBuilder.gteFields;
import static org.talend.tql.api.TqlBuilder.lt;
import static org.talend.tql.api.TqlBuilder.ltFields;
import static org.talend.tql.api.TqlBuilder.lte;
import static org.talend.tql.api.TqlBuilder.lteFields;
import static org.talend.tql.api.TqlBuilder.neq;
import static org.talend.tql.api.TqlBuilder.neqFields;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_FieldComparison extends TestTqlParser_Abstract {

    @Test
    public void testApiFieldComparisonNumericField() throws Exception {
        // TQL native query
        TqlElement expected = doTest("0001='value'");
        // TQL api query
        TqlElement tqlElement = eq("0001", "value");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldComparisonEq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 = field(field2)");
        // TQL api query
        TqlElement tqlElement = eqFields("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiStringComparisonEq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 = 'field2'");
        // TQL api query
        TqlElement tqlElement = eq("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIntComparisonEq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 = 1");
        // TQL api query
        TqlElement tqlElement = eq("field1", 1);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiDoubleComparisonEq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 = 123.456");
        // TQL api query
        TqlElement tqlElement = eq("field1", 123.456);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldComparisonNeq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != field(field2)");
        // TQL api query
        TqlElement tqlElement = neqFields("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiStringComparisonNeq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != 'field2'");
        // TQL api query
        TqlElement tqlElement = neq("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIntComparisonNeq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != 1");
        // TQL api query
        TqlElement tqlElement = neq("field1", 1);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiDoubleComparisonNeq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != 1.45");
        // TQL api query
        TqlElement tqlElement = neq("field1", 1.45);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiBooleanComparisonNeq() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != true");
        // TQL api query
        TqlElement tqlElement = neq("field1", true);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIntComparisonLt() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 < 1");
        // TQL api query
        TqlElement tqlElement = lt("field1", 1);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiDoubleComparisonLt() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 < 123.456");
        // TQL api query
        TqlElement tqlElement = lt("field1", 123.456);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldComparisonLt() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 < field(f2)");
        // TQL api query
        TqlElement tqlElement = ltFields("field1", "f2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldComparisonLte() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 <= field(field2)");
        // TQL api query
        TqlElement tqlElement = lteFields("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIntComparisonLte() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 <= 2");
        // TQL api query
        TqlElement tqlElement = lte("field1", 2);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiDoubleComparisonLte() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 <= 123.456");
        // TQL api query
        TqlElement tqlElement = lte("field1", 123.456);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldComparisonGte() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 >= field(field2)");
        // TQL api query
        TqlElement tqlElement = gteFields("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIntComparisonGte() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 >= 1");
        // TQL api query
        TqlElement tqlElement = gte("field1", 1);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiDoubleComparisonGte() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 >= 123.456");
        // TQL api query
        TqlElement tqlElement = gte("field1", 123.456);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldComparisonGt() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 > field(field2)");
        // TQL api query
        TqlElement tqlElement = gtFields("field1", "field2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIntComparisonGt() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 > 1");
        // TQL api query
        TqlElement tqlElement = gt("field1", 1);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiDoubleComparisonGt() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 > 123.456");
        // TQL api query
        TqlElement tqlElement = gt("field1", 123.456);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiStringComparisonWithSingleQuote() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 != 'fiel\\'d2'");
        // TQL api query
        TqlElement tqlElement = neq("field1", "fiel'd2");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }
}
