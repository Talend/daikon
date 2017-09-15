package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_In extends TestTqlParser_Abstract {

    @Test
    public void testParseFieldBetween_quoted1() throws Exception {
        TqlElement tqlElement = doTest("field1 in ['value1']");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=QUOTED_VALUE, value='value1'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_quoted2() throws Exception {
        TqlElement tqlElement = doTest("field1 in ['value1', 'value2']");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=QUOTED_VALUE, value='value1'}, LiteralValue{literal=QUOTED_VALUE, value='value2'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_quoted5() throws Exception {
        TqlElement tqlElement = doTest("field1 in ['value1', 'value2', 'value3', 'value4', 'value5']");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=QUOTED_VALUE, value='value1'}, LiteralValue{literal=QUOTED_VALUE, value='value2'}, LiteralValue{literal=QUOTED_VALUE, value='value3'}, LiteralValue{literal=QUOTED_VALUE, value='value4'}, LiteralValue{literal=QUOTED_VALUE, value='value5'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_int1() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=INT, value='11'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_int2() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11, 22]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=INT, value='11'}, LiteralValue{literal=INT, value='22'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_int5() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11, 22, 33, 44, 55]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=INT, value='11'}, LiteralValue{literal=INT, value='22'}, LiteralValue{literal=INT, value='33'}, LiteralValue{literal=INT, value='44'}, LiteralValue{literal=INT, value='55'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_decimal1() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11.11]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=DECIMAL, value='11.11'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_decimal2() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11.11, 22.22]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=DECIMAL, value='11.11'}, LiteralValue{literal=DECIMAL, value='22.22'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_decimal5() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11.11, 22.22, 33.33, 44.44, 55.55]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=DECIMAL, value='11.11'}, LiteralValue{literal=DECIMAL, value='22.22'}, LiteralValue{literal=DECIMAL, value='33.33'}, LiteralValue{literal=DECIMAL, value='44.44'}, LiteralValue{literal=DECIMAL, value='55.55'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_boolean1() throws Exception {
        TqlElement tqlElement = doTest("field1 in [true]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=BOOLEAN, value='true'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_boolean2() throws Exception {
        TqlElement tqlElement = doTest("field1 in [true, false]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=BOOLEAN, value='true'}, LiteralValue{literal=BOOLEAN, value='false'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_mix() throws Exception {
        TqlElement tqlElement = doTest("field1 in [11, 22.22, true]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldInExpression{fieldName='field1', values=[LiteralValue{literal=INT, value='11'}, LiteralValue{literal=DECIMAL, value='22.22'}, LiteralValue{literal=BOOLEAN, value='true'}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_wrongValue_string() throws Exception {
        expectedException.expect(Exception.class);
        doTest("field1 in [a, b]");
    }
}
