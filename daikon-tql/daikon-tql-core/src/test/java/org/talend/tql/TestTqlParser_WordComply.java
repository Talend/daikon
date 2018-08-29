package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_WordComply extends TestTqlParser_Abstract {

    @Test
    public void testParseFieldWordCompliesPattern1() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '[word][digit]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='[word][digit]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern2() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '[Word]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='[Word]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern3() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '[Word] [word][digit]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='[Word] [word][digit]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern4() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '[Word] [Word]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='[Word] [Word]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern5() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '[Word]_[digit]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='[Word]_[digit]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern6() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '][word]@'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='][word]@'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern7() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies '[Word] أبجد [word]'");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern='[Word] أبجد [word]'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldWordCompliesPattern8() throws Exception {
        TqlElement tqlElement = doTest("name wordComplies ''");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[FieldWordCompliesPattern{field='FieldReference{path='name'}', pattern=''}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }
}
