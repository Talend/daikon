package org.talend.tqlmongo.criteria;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.talend.tql.excp.TqlException;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_In extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldInQuoted1() {
        Criteria criteria = doTest("field1 in ['value1']");
        Criteria expectedCriteria = Criteria.where("field1").in(Collections.singletonList("value1"));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInQuoted2() {
        Criteria criteria = doTest("field1 in ['value1', 'value2']");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList("value1", "value2"));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInQuoted5() {
        Criteria criteria = doTest("field1 in ['value1', 'value2', 'value3', 'value4', 'value5']");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList("value1", "value2", "value3", "value4", "value5"));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInInt1() {
        Criteria criteria = doTest("field1 in [11]");
        Criteria expectedCriteria = Criteria.where("field1").in(Collections.singletonList(11L));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInInt2() {
        Criteria criteria = doTest("field1 in [11, 22]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11L, 22L));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInInt5() {
        Criteria criteria = doTest("field1 in [11, 22, 33, 44, 55]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11L, 22L, 33L, 44L, 55L));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInDecimal1() {
        Criteria criteria = doTest("field1 in [11.11]");
        Criteria expectedCriteria = Criteria.where("field1").in(Collections.singletonList(11.11));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInDecimal2() {
        Criteria criteria = doTest("field1 in [11.11, 22.22]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11.11, 22.22));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInDecimal5() {
        Criteria criteria = doTest("field1 in [11.11, 22.22, 33.33, 44.44, 55.55]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11.11, 22.22, 33.33, 44.44, 55.55));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInString() {
        expectedException.expect(TqlException.class);
        doTest("field1 in [a, b]");
        Assert.fail();
    }

    @Test
    public void testParseFieldInBoolean() {
        Criteria criteria = doTest("field1 in [true, false]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(true, false));
        assertCriteriaEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInMix() {
        Criteria criteria = doTest("field1 in [true, 'value1', 11, false]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(true, "value1", 11L, false));
        assertCriteriaEquals(expectedCriteria, criteria);
    }
}
