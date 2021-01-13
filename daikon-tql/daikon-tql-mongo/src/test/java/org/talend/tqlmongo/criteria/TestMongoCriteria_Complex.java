package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.talend.tqlmongo.excp.TqlMongoException;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Complex extends TestMongoCriteria_Abstract {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testParseLiteralComparison() {
        Criteria criteria = doTest("field1='value1'");
        Criteria expectedCriteria = Criteria.where("field1").is("value1");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseLiteralComparisonWithParenthesis() {
        Criteria criteria = doTest("(((field1='value1')))");
        Criteria expectedCriteria = Criteria.where("field1").is("value1");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldIsEmpty() {
        Criteria criteria = doTest("field1 is empty");
        Criteria expectedCriteria = new Criteria().orOperator(Criteria.where("field1").is(""), Criteria.where("field1").is(null));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldIsNull() {
        Criteria criteria = doTest("field1 is null");
        Criteria expectedCriteria = Criteria.where("field1").is(null);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldIsValid() {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 is valid");
        Assert.fail();
    }

    @Test
    public void testParseFieldIsInvalid() {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 is invalid");
        Assert.fail();
    }

    @Test
    public void testParseNotExpression() {
        Criteria criteria = doTest("not (field1='value1')");
        Criteria expectedCriteria = Criteria.where("field1").ne("value1");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerCompound() {
        Criteria criteria = doTest("field1 <= 123 and field2 = 456");
        Criteria c1 = Criteria.where("field1").lte(123L);
        Criteria c2 = Criteria.where("field2").is(456L);
        Criteria expectedCriteria = new Criteria().andOperator(c1, c2);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseAndExpressions() {
        Criteria criteria = doTest("field1='value1' and field2='value2' and field3='value3'");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = Criteria.where("field3").is("value3");
        Criteria expectedCriteria = new Criteria().andOperator(c1, c2, c3);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseComplexExpressions() {
        Criteria criteria = doTest("field1='value1' and field2 is empty or ((field3='value3'))");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = new Criteria().orOperator(Criteria.where("field2").is(""), Criteria.where("field2").is(null));
        Criteria c3 = new Criteria().andOperator(c1, c2);
        Criteria c4 = Criteria.where("field3").is("value3");

        Criteria expectedCriteria = new Criteria().orOperator(c3, c4);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseAndExpressionsParenthesis1() {
        Criteria criteria = doTest("(field1='value1' and field2='value2') or field3='value3'");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = new Criteria().andOperator(c1, c2);
        Criteria c4 = Criteria.where("field3").is("value3");

        Criteria expectedCriteria = new Criteria().orOperator(c3, c4);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseAndExpressionsParenthesis2() {
        Criteria criteria = doTest("field1='value1' and (field2='value2' or field3='value3')");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = Criteria.where("field3").is("value3");
        Criteria c4 = new Criteria().orOperator(c2, c3);

        Criteria expectedCriteria = new Criteria().andOperator(c1, c4);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseComplexExpressionsNoParenthesis() {
        Criteria criteria = doTest("field1='value1' or field2='value2' and field3='value3'");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = Criteria.where("field3").is("value3");
        Criteria c4 = new Criteria().andOperator(c2, c3);

        Criteria expectedCriteria = new Criteria().orOperator(c1, c4);
        assertCriteriaEquals(expectedCriteria, criteria);
    }
}
