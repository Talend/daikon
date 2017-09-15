package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_Between extends TestTqlParser_Abstract {

    @Test
    public void testParseFieldBetween_quoted() throws Exception {
        TqlElement tqlElement = doTest("field1 between ['value1', 'value2']");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldBetweenExpression{fieldName='field1', left='LiteralValue{literal=QUOTED_VALUE, value='value1'}', right='LiteralValue{literal=QUOTED_VALUE, value='value2'}'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_INT() throws Exception {
        TqlElement tqlElement = doTest("field1 between [123, 456]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldBetweenExpression{fieldName='field1', left='LiteralValue{literal=INT, value='123'}', right='LiteralValue{literal=INT, value='456'}'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_DECIMAL() throws Exception {
        TqlElement tqlElement = doTest("field1 between [123.45, 456.78]");
        String expected = "OrExpression{expressions=[AndExpression{expressions=["
                + "FieldBetweenExpression{fieldName='field1', left='LiteralValue{literal=DECIMAL, value='123.45'}', right='LiteralValue{literal=DECIMAL, value='456.78'}'}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseFieldBetween_wrongValue_string() throws Exception {
        expectedException.expect(Exception.class);
        doTest("field1 between [a, b]");
    }

    @Test
    public void testParseFieldBetween_wrongValue_boolean() throws Exception {
        expectedException.expect(Exception.class);
        doTest("field1 between [true, false]");
    }
}
