package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Decimal extends TestMongoCriteria_Abstract {

    @Test
    public void testDecimalEq() {
        Criteria criteria = doTest("field1 = 123.45");
        Criteria expectedCriteria = Criteria.where("field1").is(123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimalNe() {
        Criteria criteria = doTest("field1 != 123.45");
        Criteria expectedCriteria = Criteria.where("field1").ne(123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimalLt() {
        Criteria criteria = doTest("field1 < 123.45");
        Criteria expectedCriteria = Criteria.where("field1").lt(123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimalGt() {
        Criteria criteria = doTest("field1 > 123.45");
        Criteria expectedCriteria = Criteria.where("field1").gt(123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimalGte() {
        Criteria criteria = doTest("field1 >= 123.45");
        Criteria expectedCriteria = Criteria.where("field1").gte(123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimalLte() {
        Criteria criteria = doTest("field1 <= 123.45");
        Criteria expectedCriteria = Criteria.where("field1").lte(123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimalNegative() {
        Criteria criteria = doTest("field1 = -123.45");
        Criteria expectedCriteria = Criteria.where("field1").is(-123.45);
        assertCriteriaEquals(expectedCriteria, criteria);

        criteria = doTest("field1 <= -123.45");
        expectedCriteria = Criteria.where("field1").lte(-123.45);
        assertCriteriaEquals(expectedCriteria, criteria);
    }

}
