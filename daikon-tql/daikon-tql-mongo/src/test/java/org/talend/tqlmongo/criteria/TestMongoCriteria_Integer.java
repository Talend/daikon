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
    public void testInteger_eq() throws Exception {
        Criteria criteria = doTest("field1 = 123");
        Criteria expectedCriteria = Criteria.where("field1").is(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_ne() throws Exception {
        Criteria criteria = doTest("field1 != 123");
        Criteria expectedCriteria = Criteria.where("field1").ne(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_lt() throws Exception {
        Criteria criteria = doTest("field1 < 123");
        Criteria expectedCriteria = Criteria.where("field1").lt(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_gt() throws Exception {
        Criteria criteria = doTest("field1 > 123");
        Criteria expectedCriteria = Criteria.where("field1").gt(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_gte() throws Exception {
        Criteria criteria = doTest("field1 >= 123");
        Criteria expectedCriteria = Criteria.where("field1").gte(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_lte() throws Exception {
        Criteria criteria = doTest("field1 <= 123");
        Criteria expectedCriteria = Criteria.where("field1").lte(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_negative() throws Exception {
        Criteria criteria = doTest("field1 = -123");
        Criteria expectedCriteria = Criteria.where("field1").is(-123L);
        Assert.assertEquals(expectedCriteria, criteria);

        criteria = doTest("field1 <= -123");
        expectedCriteria = Criteria.where("field1").lte(-123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testInteger_outOfRange() throws Exception {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 = 99999999999999999999999999999999999999999999999999");
    }
}
