package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_Boolean extends TestTqlParser_Abstract {

    @Test
    public void testParseLiteralComparison_eqTrue() throws Exception {
        TqlElement tqlElement = doTest("field1=true");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=EQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=BOOLEAN, value='true'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_neqTrue() throws Exception {
        TqlElement tqlElement = doTest("field1!=true");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=NEQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=BOOLEAN, value='true'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_eqFalse() throws Exception {
        TqlElement tqlElement = doTest("field1=false");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=EQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=BOOLEAN, value='false'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparison_neqFalse() throws Exception {
        TqlElement tqlElement = doTest("field1!=false");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[" + "ComparisonExpression{"
                + "operator=ComparisonOperator{operator=NEQ}, " + "field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=BOOLEAN, value='false'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

}
