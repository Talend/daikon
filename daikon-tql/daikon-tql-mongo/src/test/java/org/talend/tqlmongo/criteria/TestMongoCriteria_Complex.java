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
    public void testParseLiteralComparison() throws Exception {
        Criteria criteria = doTest("field1='value1'");
        Criteria expectedCriteria = Criteria.where("field1").is("value1");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseLiteralComparison_withParenthesis() throws Exception {
        Criteria criteria = doTest("(((field1='value1')))");
        Criteria expectedCriteria = Criteria.where("field1").is("value1");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldIsEmpty() throws Exception {
        Criteria criteria = doTest("field1 is empty");
        Criteria expectedCriteria = new Criteria().orOperator(Criteria.where("field1").is(""), Criteria.where("field1").is(null));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldIsValid() throws Exception {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 is valid");
    }

    @Test
    public void testParseFieldIsInvalid() throws Exception {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 is invalid");
    }

    @Test
    public void testParseNotExpression() throws Exception {
        Criteria criteria = doTest("not (field1='value1')");
        Criteria expectedCriteria = Criteria.where("field1").ne("value1");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_compound() throws Exception {
        Criteria criteria = doTest("field1 <= 123 and field2 = 456");
        Criteria c1 = Criteria.where("field1").lte(123L);
        Criteria c2 = Criteria.where("field2").is(456L);
        Criteria expectedCriteria = new Criteria().andOperator(c1, c2);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseAndExpressions() throws Exception {
        Criteria criteria = doTest("field1='value1' and field2='value2' and field3='value3'");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = Criteria.where("field3").is("value3");
        Criteria expectedCriteria = new Criteria().andOperator(c1, c2, c3);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseComplexExpressions() throws Exception {
        Criteria criteria = doTest("field1='value1' and field2 is empty or ((field3='value3'))");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = new Criteria().orOperator(Criteria.where("field2").is(""), Criteria.where("field2").is(null));
        Criteria c3 = new Criteria().andOperator(c1, c2);
        Criteria c4 = Criteria.where("field3").is("value3");

        Criteria expectedCriteria = new Criteria().orOperator(c3, c4);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseAndExpressions_parenthesis1() throws Exception {
        Criteria criteria = doTest("(field1='value1' and field2='value2') or field3='value3'");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = new Criteria().andOperator(c1, c2);
        Criteria c4 = Criteria.where("field3").is("value3");

        Criteria expectedCriteria = new Criteria().orOperator(c3, c4);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseAndExpressions_parenthesis2() throws Exception {
        Criteria criteria = doTest("field1='value1' and (field2='value2' or field3='value3')");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = Criteria.where("field3").is("value3");
        Criteria c4 = new Criteria().orOperator(c2, c3);

        Criteria expectedCriteria = new Criteria().andOperator(c1, c4);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseComplexExpressions_noParenthesis() throws Exception {
        Criteria criteria = doTest("field1='value1' or field2='value2' and field3='value3'");
        Criteria c1 = Criteria.where("field1").is("value1");
        Criteria c2 = Criteria.where("field2").is("value2");
        Criteria c3 = Criteria.where("field3").is("value3");
        Criteria c4 = new Criteria().andOperator(c2, c3);

        Criteria expectedCriteria = new Criteria().orOperator(c1, c4);
        Assert.assertEquals(expectedCriteria, criteria);
    }
}
