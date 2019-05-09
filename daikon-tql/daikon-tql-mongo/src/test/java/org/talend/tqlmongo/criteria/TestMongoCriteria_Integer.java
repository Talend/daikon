package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.talend.tqlmongo.excp.TqlMongoException;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Integer extends TestMongoCriteria_Abstract {

    @Test
    public void testIntegerEq() {
        Criteria criteria = doTest("field1 = 123");
        Criteria expectedCriteria = Criteria.where("field1").is(123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerNe() {
        Criteria criteria = doTest("field1 != 123");
        Criteria expectedCriteria = Criteria.where("field1").ne(123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerLt() {
        Criteria criteria = doTest("field1 < 123");
        Criteria expectedCriteria = Criteria.where("field1").lt(123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerGt() {
        Criteria criteria = doTest("field1 > 123");
        Criteria expectedCriteria = Criteria.where("field1").gt(123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerGte() {
        Criteria criteria = doTest("field1 >= 123");
        Criteria expectedCriteria = Criteria.where("field1").gte(123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerLte() {
        Criteria criteria = doTest("field1 <= 123");
        Criteria expectedCriteria = Criteria.where("field1").lte(123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerNegative() {
        Criteria criteria = doTest("field1 = -123");
        Criteria expectedCriteria = Criteria.where("field1").is(-123L);
        assertCriteriaEquals(expectedCriteria, criteria);

        criteria = doTest("field1 <= -123");
        expectedCriteria = Criteria.where("field1").lte(-123L);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerOutOfRange() {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 = 99999999999999999999999999999999999999999999999999");
        Assert.fail();
    }
}
