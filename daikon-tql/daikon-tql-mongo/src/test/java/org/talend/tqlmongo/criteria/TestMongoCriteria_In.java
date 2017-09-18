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
    public void testParseFieldInQuoted1() throws Exception {
        Criteria criteria = doTest("field1 in ['value1']");
        Criteria expectedCriteria = Criteria.where("field1").in(Collections.singletonList("value1"));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInQuoted2() throws Exception {
        Criteria criteria = doTest("field1 in ['value1', 'value2']");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList("value1", "value2"));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInQuoted5() throws Exception {
        Criteria criteria = doTest("field1 in ['value1', 'value2', 'value3', 'value4', 'value5']");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList("value1", "value2", "value3", "value4", "value5"));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInInt1() throws Exception {
        Criteria criteria = doTest("field1 in [11]");
        Criteria expectedCriteria = Criteria.where("field1").in(Collections.singletonList(11L));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInInt2() throws Exception {
        Criteria criteria = doTest("field1 in [11, 22]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11L, 22L));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInInt5() throws Exception {
        Criteria criteria = doTest("field1 in [11, 22, 33, 44, 55]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11L, 22L, 33L, 44L, 55L));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInDecimal1() throws Exception {
        Criteria criteria = doTest("field1 in [11.11]");
        Criteria expectedCriteria = Criteria.where("field1").in(Collections.singletonList(11.11));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInDecimal2() throws Exception {
        Criteria criteria = doTest("field1 in [11.11, 22.22]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11.11, 22.22));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInDecimal5() throws Exception {
        Criteria criteria = doTest("field1 in [11.11, 22.22, 33.33, 44.44, 55.55]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(11.11, 22.22, 33.33, 44.44, 55.55));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInString() throws Exception {
        expectedException.expect(TqlException.class);
        doTest("field1 in [a, b]");
        Assert.fail();
    }

    @Test
    public void testParseFieldInBoolean() throws Exception {
        Criteria criteria = doTest("field1 in [true, false]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(true, false));
        Assert.assertEquals(expectedCriteria, criteria);
    }

    @Test
    public void testParseFieldInMix() throws Exception {
        Criteria criteria = doTest("field1 in [true, 'value1', 11, false]");
        Criteria expectedCriteria = Criteria.where("field1").in(Arrays.asList(true, "value1", 11L, false));
        Assert.assertEquals(expectedCriteria, criteria);
    }
}
