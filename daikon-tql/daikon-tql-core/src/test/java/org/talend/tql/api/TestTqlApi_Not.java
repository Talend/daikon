package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.and;
import static org.talend.tql.api.TqlBuilder.complies;
import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.gt;
import static org.talend.tql.api.TqlBuilder.not;
import static org.talend.tql.api.TqlBuilder.or;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.Expression;
import org.talend.tql.model.TqlElement;

public class TestTqlApi_Not extends TestTqlParser_Abstract {

    @Test
    public void testApiNotExpression() throws Exception {
        // TQL native query
        TqlElement expected = doTest("not (field1='value1')");
        // TQL Built query
        Expression actual = not(eq("field1", "value1"));
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testApiNotExpression2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("not(not (field1='value1'))");
        // TQL Built query
        Expression actual = not(not(eq("field1", "value1")));
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testApiNotExpression3() throws Exception {
        // TQL native query
        TqlElement expected = doTest("not ((field1='value1' and field1 complies 'aaa9') or field1>999)");
        // TQL Built query
        Expression actual = not(or(and(eq("field1", "value1"), complies("field1", "aaa9")), gt("field1", 999)));
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    /*
     * @Test
     * public void testApiNotExpression5() throws Exception {
     * TqlElement tqlElement = doTest("not (field1='value1' or field1 complies 'aaa9' and not(field1>999))");
     * // --> field1!='value1' and (not (field1 complies 'aaa9') or field1<= 999
     * String expected = "OrExpression{expressions=[AndExpression{expressions="
     * + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
     * +
     * "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}, "
     * +
     * "AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}, NotExpression{expression=OrExpression{expressions="
     * +
     * "[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, "
     * + "valueOrField=LiteralValue{literal=INT, value='999'}}]}]}}]}]}}]}]}";
     * Assert.assertEquals(expected, tqlElement.toString());
     * }
     * 
     * @Test
     * public void testApiNotExpression6() throws Exception {
     * TqlElement tqlElement = doTest("field1='value1' and (not (field1 complies 'aaa9') or field1> 999)");
     * String expected =
     * "OrExpression{expressions=[AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, "
     * +
     * "valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}, OrExpression{expressions=[AndExpression{expressions="
     * +
     * "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}]}]}}]}, "
     * +
     * "AndExpression{expressions=[ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, "
     * + "valueOrField=LiteralValue{literal=INT, value='999'}}]}]}]}]}";
     * Assert.assertEquals(expected, tqlElement.toString());
     * }
     * 
     * @Test
     * public void testApiNotExpression7() throws Exception {
     * TqlElement tqlElement = doTest("not (field1='value1' or not (field1 complies 'aaa9' and field1>999))");
     * // --> field1!='value1' and (field1 complies 'aaa9' and field1>999
     * String expected = "OrExpression{expressions=[AndExpression{expressions="
     * + "[NotExpression{expression=OrExpression{expressions=[AndExpression{expressions="
     * +
     * "[ComparisonExpression{operator=ComparisonOperator{operator=EQ}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=QUOTED_VALUE, value='value1'}}]}, "
     * + "AndExpression{expressions=[NotExpression{expression=OrExpression{expressions="
     * + "[AndExpression{expressions=[FieldCompliesPattern{field='FieldReference{path='field1'}', pattern='aaa9'}, "
     * +
     * "ComparisonExpression{operator=ComparisonOperator{operator=GT}, field=FieldReference{path='field1'}, valueOrField=LiteralValue{literal=INT, value='999'}}]}]}}]}]}}]}]}"
     * ;
     * Assert.assertEquals(expected, tqlElement.toString());
     * }
     */
}
