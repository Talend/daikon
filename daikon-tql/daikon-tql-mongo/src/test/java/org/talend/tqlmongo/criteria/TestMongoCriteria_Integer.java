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
    public void testIntegerEq() throws Exception {
        Criteria criteria = doTest("field1 = 123");
        Criteria expectedCriteria = Criteria.where("field1").is(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerNe() throws Exception {
        Criteria criteria = doTest("field1 != 123");
        Criteria expectedCriteria = Criteria.where("field1").ne(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerLt() throws Exception {
        Criteria criteria = doTest("field1 < 123");
        Criteria expectedCriteria = Criteria.where("field1").lt(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerGt() throws Exception {
        Criteria criteria = doTest("field1 > 123");
        Criteria expectedCriteria = Criteria.where("field1").gt(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerGte() throws Exception {
        Criteria criteria = doTest("field1 >= 123");
        Criteria expectedCriteria = Criteria.where("field1").gte(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerLte() throws Exception {
        Criteria criteria = doTest("field1 <= 123");
        Criteria expectedCriteria = Criteria.where("field1").lte(123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerNegative() throws Exception {
        Criteria criteria = doTest("field1 = -123");
        Criteria expectedCriteria = Criteria.where("field1").is(-123L);
        Assert.assertEquals(expectedCriteria, criteria);

        criteria = doTest("field1 <= -123");
        expectedCriteria = Criteria.where("field1").lte(-123L);
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testIntegerOutOfRange() throws Exception {
        expectedException.expect(TqlMongoException.class);
        doTest("field1 = 99999999999999999999999999999999999999999999999999");
        Assert.fail();
    }
}
