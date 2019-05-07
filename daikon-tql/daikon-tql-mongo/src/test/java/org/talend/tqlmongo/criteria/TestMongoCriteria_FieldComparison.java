package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_FieldComparison extends TestMongoCriteria_Abstract {

    @Test
    public void testFieldEq() {
        Criteria criteria = doTest("field1 = field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").is("field2");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testFieldNe() {
        Criteria criteria = doTest("field1 != field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").ne("field2");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testFieldLt() {
        Criteria criteria = doTest("field1 < field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").lt("field2");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testFieldGt() {
        Criteria criteria = doTest("field1 > field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").gt("field2");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testFieldGte() {
        Criteria criteria = doTest("field1 >= field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").gte("field2");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testFieldLte() {
        Criteria criteria = doTest("field1 <= field(field2)");
        Criteria expectedCriteria = Criteria.where("field1").lte("field2");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

}
