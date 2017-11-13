package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_Match extends TestTqlParser_Abstract {

    @Test
    public void testParseFieldMatchesRegex1() throws Exception {
        TqlElement tqlElement = doTest("name ~ '^[A-Z][a-z]*$'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldMatchesRegex{field='FieldReference{path='name'}', regex='^[A-Z][a-z]*$'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldMatchesRegex2() throws Exception {
        TqlElement tqlElement = doTest("name ~ '^[A-Z|a-z]*$'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldMatchesRegex{field='FieldReference{path='name'}', regex='^[A-Z|a-z]*$'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldMatchesRegex3() throws Exception {
        TqlElement tqlElement = doTest("name ~ '^[A-Z]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldMatchesRegex{field='FieldReference{path='name'}', regex='^[A-Z]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldMatchesRegex4() throws Exception {
        TqlElement tqlElement = doTest("name ~ '\\d'"); // contains any digit
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldMatchesRegex{field='FieldReference{path='name'}', regex='\\d'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldMatchesRegex5() throws Exception {
        TqlElement tqlElement = doTest("name ~ ''"); // contains any digit
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldMatchesRegex{field='FieldReference{path='name'}', regex=''}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }
}
