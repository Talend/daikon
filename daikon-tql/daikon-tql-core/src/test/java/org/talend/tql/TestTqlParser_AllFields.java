package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_AllFields extends TestTqlParser_Abstract {

    @Test
    public void testParseLiteralComparisonNeq() throws Exception {
        TqlElement tqlElement = doTest("*!=0");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=NEQ}, field=AllFields{}, valueOrField=LiteralValue{literal=INT, value='0'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparisonLt() throws Exception {
        TqlElement tqlElement = doTest("*<0");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=LT}, field=AllFields{}, valueOrField=LiteralValue{literal=INT, value='0'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparisonGt() throws Exception {
        TqlElement tqlElement = doTest("*>0");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=AllFields{}, valueOrField=LiteralValue{literal=INT, value='0'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparisonLet() throws Exception {
        TqlElement tqlElement = doTest("*<=0");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=LET}, field=AllFields{}, valueOrField=LiteralValue{literal=INT, value='0'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseLiteralComparisonGet() throws Exception {
        TqlElement tqlElement = doTest("*>=0");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=GET}, field=AllFields{}, valueOrField=LiteralValue{literal=INT, value='0'}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }
}
