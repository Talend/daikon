package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Not extends TestMongoCriteria_Abstract {

    @Test
    public void testParseNotExpression() {
        Criteria criteria = doTest("not (field1='value1')");
        Criteria expectedCriteria = Criteria.where("field1").ne("value1");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseNotExpression2() {
        Criteria criteria = doTest("not(not (field1='value1'))");
        Criteria expectedCriteria = Criteria.where("field1").is("value1");
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseNotExpression3() {
        Criteria criteria = doTest("not (field1='value1' and field1 complies 'aaa9' or field1>999)");
        Criteria expectedCriteria = new Criteria().andOperator(
                new Criteria().orOperator(Criteria.where("field1").ne("value1"), Criteria.where("field1").not().regex(
                        "^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}])$")),
                Criteria.where("field1").lte(999L));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseNotExpression4() {
        Criteria criteria = doTest("not (field1='value1' or field1 complies 'aaa9' and field1>999)");
        Criteria expectedCriteria = new Criteria().andOperator(Criteria.where("field1").ne("value1"),
                new Criteria().orOperator(Criteria.where("field1").not().regex(
                        "^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}])$"),
                        Criteria.where("field1").lte(999L)));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseNotExpression5() {
        Criteria criteria = doTest("not (field1='value1' or field1 complies 'aaa9' and not(field1>999))");
        // --> field1!='value1' and (not (field1 complies 'aaa9') or field1<= 999
        Criteria expectedCriteria = new Criteria().andOperator(Criteria.where("field1").ne("value1"),
                new Criteria().orOperator(Criteria.where("field1").not().regex(
                        "^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}])$"),
                        Criteria.where("field1").gt(999L)));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseNotExpression6() {
        Criteria criteria = doTest("field1='value1' and (not (field1 complies 'aaa9') or field1> 999)");
        Criteria expectedCriteria = new Criteria().andOperator(Criteria.where("field1").is("value1"),
                new Criteria().orOperator(Criteria.where("field1").not().regex(
                        "^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}])$"),
                        Criteria.where("field1").gt(999L)));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseNotExpression7() {
        Criteria criteria = doTest("not (field1='value1' or not (field1 complies 'aaa9' and field1>999))");
        // --> field1!='value1' and (field1 complies 'aaa9' and field1>999
        Criteria expectedCriteria = new Criteria().andOperator(Criteria.where("field1").ne("value1"),
                new Criteria().andOperator(Criteria.where("field1").regex(
                        "^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}])$"),
                        Criteria.where("field1").gt(999L)));
        assertCriteriaEquals(expectedCriteria, criteria);
    }
}
