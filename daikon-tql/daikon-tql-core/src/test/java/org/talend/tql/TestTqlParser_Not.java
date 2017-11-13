package org.talend.tql;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.model.TqlElement;

public class TestTqlParser_Not extends TestTqlParser_Abstract {

    @Test
    public void testParseNotExpression() throws Exception {
        TqlElement tqlElement = doTest("not (field1='value1')");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, "
                + "field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}]}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseNotExpression2() throws Exception {
        TqlElement tqlElement = doTest("not(not (field1='value1'))");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, "
                + "field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}]}}]}]}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseNotExpression3() throws Exception {
        TqlElement tqlElement = doTest("not (field1='value1' and field1 complies 'aaa9' or field1>999)");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}, "
                + "FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}]}, AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=INT, value='999'}}]}]}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseNotExpression4() throws Exception {
        TqlElement tqlElement = doTest("not (field1='value1' or field1 complies 'aaa9' and field1>999)");
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}, "
                + "AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}, "
                + "ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=INT, value='999'}}]}]}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseNotExpression5() throws Exception {
        TqlElement tqlElement = doTest("not (field1='value1' or field1 complies 'aaa9' and not(field1>999))");
        // --> field1!='value1' and (not (field1 complies 'aaa9') or field1<= 999
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}, "
                + "AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}, NotExpression{expression=OrExpression{expressions="
                + "[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=INT, value='999'}}]}]}}]}]}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseNotExpression6() throws Exception {
        TqlElement tqlElement = doTest("field1='value1' and (not (field1 complies 'aaa9') or field1> 999)");
        String expected = "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}, OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}]}]}}]}, "
                + "AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, "
                + "valueOrField=LiteralValue{literal=INT, value='999'}}]}]}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }

    @Test
    public void testParseNotExpression7() throws Exception {
        TqlElement tqlElement = doTest("not (field1='value1' or not (field1 complies 'aaa9' and field1>999))");
        // --> field1!='value1' and (field1 complies 'aaa9' and field1>999
        String expected = "OrExpression{expressions=[AndExpression{expressions="
                + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
                + "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}, "
                + "AndExpression{expressions=[NotExpression{expression=OrExpression{expressions="
                + "[AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}, "
                + "ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=INT, value='999'}}]}]}}]}]}}]}]}";
        Assert.assertEquals(expected, tqlElement.toString());
    }
}
