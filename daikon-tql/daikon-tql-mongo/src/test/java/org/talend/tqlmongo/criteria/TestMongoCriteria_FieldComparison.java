package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_FieldComparison extends TestMongoCriteria_Abstract {

    @Test
    public void testField_eq() throws Exception {
        Criteria criteria = doTest("field1 = field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").is("field2");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testField_ne() throws Exception {
        Criteria criteria = doTest("field1 != field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").ne("field2");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testField_lt() throws Exception {
        Criteria criteria = doTest("field1 < field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").lt("field2");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testField_gt() throws Exception {
        Criteria criteria = doTest("field1 > field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").gt("field2");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testField_gte() throws Exception {
        Criteria criteria = doTest("field1 >= field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").gte("field2");
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testField_lte() throws Exception {
        Criteria criteria = doTest("field1 <= field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").lte("field2");
        Assert.assertEquals(expectedCriteria, criteria);
    }

}
