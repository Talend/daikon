package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_Decimal extends TestTqlParser_Abstract {

    @Test
    public void testParseLiteralComparison_eq() throws Exception {
        TqlElement tqlElement = doTest("field1=123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=EQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_neq() throws Exception {
        TqlElement tqlElement = doTest("field1!=123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=NEQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_lt() throws Exception {
        TqlElement tqlElement = doTest("field1<123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=LT}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_gt() throws Exception {
        TqlElement tqlElement = doTest("field1>123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=GT}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_let() throws Exception {
        TqlElement tqlElement = doTest("field1<=123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=LET}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_get() throws Exception {
        TqlElement tqlElement = doTest("field1>=123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=GET}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_negative() throws Exception {
        TqlElement tqlElement = doTest("field1=-123.456");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=EQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='-123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());

        tqlElement = doTest("field1>=-123.456");
        expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=GET}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=DECIMAL, value='-123.456'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }
}
