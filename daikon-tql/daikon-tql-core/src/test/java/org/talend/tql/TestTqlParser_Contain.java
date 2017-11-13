package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_Contain extends TestTqlParser_Abstract {

    @Test
    public void testParseFieldContainsExpression1() throws Exception {
        TqlElement tqlElement = doTest("name contains 'ssen'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldContainsExpression{field='FieldReference{path='name'}', value='ssen'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldContainsExpression2() throws Exception {
        TqlElement tqlElement = doTest("name contains 'noi'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldContainsExpression{field='FieldReference{path='name'}', value='noi'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldContainsExpression3() throws Exception {
        TqlElement tqlElement = doTest("name contains '2'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldContainsExpression{field='FieldReference{path='name'}', value='2'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldContainsExpression4() throws Exception {
        TqlElement tqlElement = doTest("name contains 'azerty'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldContainsExpression{field='FieldReference{path='name'}', value='azerty'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldContainsExpression5() throws Exception {
        TqlElement tqlElement = doTest("name contains ''");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldContainsExpression{field='FieldReference{path='name'}', value=''}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }
}
