package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.wordComplies;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_WordComply extends TestTqlParser_Abstract {

    @Test
    public void testApiFieldCompliesPattern1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '[word]'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "[word]");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '[Word]'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "[Word]");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern3() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '[Word] [digit][word]'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "[Word] [digit][word]");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern4() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '[Word] [Word]'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "[Word] [Word]");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern5() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '[Word]_[number]'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "[Word]_[number]");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern6() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies ']ss@'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "]ss@");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern7() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '[Word] أبجد [Word]'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "[Word] أبجد [Word]");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern8() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies ''");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern9() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies '\\''");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "'");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiFieldCompliesPattern10() throws Exception {
        // TQL native query
        TqlElement expected = doTest("name wordComplies 'C\\'est quoi'");
        // TQL api query
        TqlElement tqlElement = wordComplies("name", "C'est quoi");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }
}
