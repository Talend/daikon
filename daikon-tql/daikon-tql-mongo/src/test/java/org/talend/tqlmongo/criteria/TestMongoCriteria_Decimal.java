package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Decimal extends TestMongoCriteria_Abstract {

    @Test
    public void testDecimal_eq() throws Exception {
        Criteria criteria = doTest("field1 = 123.45");
        Criteria expectedCriteria = Criteria.where("field1").is(123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimal_ne() throws Exception {
        Criteria criteria = doTest("field1 != 123.45");
        Criteria expectedCriteria = Criteria.where("field1").ne(123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimal_lt() throws Exception {
        Criteria criteria = doTest("field1 < 123.45");
        Criteria expectedCriteria = Criteria.where("field1").lt(123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimal_gt() throws Exception {
        Criteria criteria = doTest("field1 > 123.45");
        Criteria expectedCriteria = Criteria.where("field1").gt(123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimal_gte() throws Exception {
        Criteria criteria = doTest("field1 >= 123.45");
        Criteria expectedCriteria = Criteria.where("field1").gte(123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimal_lte() throws Exception {
        Criteria criteria = doTest("field1 <= 123.45");
        Criteria expectedCriteria = Criteria.where("field1").lte(123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testDecimal_negative() throws Exception {
        Criteria criteria = doTest("field1 = -123.45");
        Criteria expectedCriteria = Criteria.where("field1").is(-123.45);
        Assert.assertEquals(expectedCriteria, criteria);

        criteria = doTest("field1 <= -123.45");
        expectedCriteria = Criteria.where("field1").lte(-123.45);
        Assert.assertEquals(expectedCriteria, criteria);
    }

}
