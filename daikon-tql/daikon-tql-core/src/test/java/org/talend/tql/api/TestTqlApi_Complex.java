package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.and;
import static org.talend.tql.api.TqlBuilder.between;
import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.in;
import static org.talend.tql.api.TqlBuilder.isEmpty;
import static org.talend.tql.api.TqlBuilder.isInvalid;
import static org.talend.tql.api.TqlBuilder.isValid;
import static org.talend.tql.api.TqlBuilder.neqFields;
import static org.talend.tql.api.TqlBuilder.or;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_Complex extends TestTqlParser_Abstract {

    @Test
    public void testApiComplex1() throws Exception { // TQL native query
        TqlElement expected = doTest("f1 is empty or (f2 is empty or f3 is empty)"); // TQL api query
        TqlElement tqlElement = or(isEmpty("f1"), or(isEmpty("f2"), isEmpty("f3")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("f1 is empty or (f2 is empty or f3 is empty or (f4 is empty or f5 is empty))");
        // TQL api query
        TqlElement tqlElement = or(isEmpty("f1"), or(isEmpty("f2"), isEmpty("f3"), or(isEmpty("f4"), isEmpty("f5"))));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex3() throws Exception {
        // TQL native query
        TqlElement expected = doTest("f1 is empty or (f2 is empty and f3 is empty)");
        // TQL api query
        TqlElement tqlElement = or(isEmpty("f1"), and(isEmpty("f2"), isEmpty("f3")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex4() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 is empty or (field2 is invalid or field3=true)");
        // TQL api query
        TqlElement tqlElement = or(isEmpty("field1"), or(isInvalid("field2"), eq("field3", true)));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    /*
     * @Test public void testApiOrAndWithoutParenthesisExpressions1() throws Exception {
     * 
     * // TQL native query
     * TqlElement expected = doTest("field1 is empty and field2 is invalid or field3=true");
     * 
     * // TQL api query
     * TqlElement tqlElement = or(and(isEmpty("field1"), isInvalid("field2")), eq("field3", true));
     * 
     * Assert.assertEquals(expected.toString(), tqlElement.toString());
     * 
     * }
     * 
     * @Test public void testApiOrAndWithoutParenthesisExpressions2() throws Exception {
     * 
     * // TQL native query
     * TqlElement expected = doTest("field1 is empty or field2 is invalid and field3=true");
     * 
     * // TQL api query
     * TqlElement tqlElement = or(isEmpty("field1"), and(isInvalid("field2"), eq("field3", true)));
     * 
     * Assert.assertEquals(expected.toString(), tqlElement.toString());
     * 
     * }
     */
    @Test
    public void testApiComplex5() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 is empty and (field2 is invalid or field3=true)");
        // TQL api query
        TqlElement tqlElement = and(isEmpty("field1"), or(isInvalid("field2"), eq("field3", true)));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex6() throws Exception {
        // TQL native query
        TqlElement expected = doTest("f1 is empty and (f2 is empty or f3 is invalid");
        // TQL api query
        TqlElement tqlElement = and(isEmpty("f1"), or(isEmpty("f2"), isInvalid("f3")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex7() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1 is empty or f2 is invalid) and (f3 is empty or f4 is valid");
        // TQL api query
        TqlElement tqlElement = and(or(isEmpty("f1"), isInvalid("f2")), or(isEmpty("f3"), isValid("f4")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex8() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1 is empty and f2 is invalid) or (f3 is empty and f4 is valid)");
        // TQL api query
        TqlElement tqlElement = or(and(isEmpty("f1"), isInvalid("f2")), and(isEmpty("f3"), isValid("f4")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex9() throws Exception {
        // TQL native query
        TqlElement expected = doTest(
                "(f1 between [2, 4] and f2=true) or (f3 != field(f4) and f4 in ['value1', 'value2', 'value3', 'value4'])");
        // TQL api query
        String[] values = new String[] { "value1", "value2", "value3", "value4" };

        TqlElement tqlElement = or(and(between("f1", 2, 4), eq("f2", true)), and(neqFields("f3", "f4"), in("f4", values)));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex10() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1 is empty and f2 is invalid) or (f3 is empty and (f4 is valid or f5 is invalid))");
        // TQL api query
        TqlElement tqlElement = or(and(isEmpty("f1"), isInvalid("f2")), and(isEmpty("f3"), or(isValid("f4"), isInvalid("f5"))));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComplex11() throws Exception {
        // TQL native query
        TqlElement expected = doTest(
                "f1 is empty and (f2 is empty or (f3 is empty and (f4 is valid or (f5 is invalid and f6 between [1,2])))");
        // TQL api query
        TqlElement tqlElement = and(isEmpty("f1"),
                or(isEmpty("f2"), and(isEmpty("f3"), or(isValid("f4"), and(isInvalid("f5"), between("f6", 1, 2))))));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
