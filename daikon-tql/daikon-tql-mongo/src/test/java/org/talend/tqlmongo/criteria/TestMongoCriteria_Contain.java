package org.talend.tqlmongo.criteria;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Contain extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldContainsValue1() throws Exception {
        Criteria criteria = doTest("name contains 'ssen'");
        Criteria expectedCriteria = Criteria.where("name").regex("ssen");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsValue2() throws Exception {
        Criteria criteria = doTest("name contains 'noi'");
        Criteria expectedCriteria = Criteria.where("name").regex("noi");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldContainsValue3() throws Exception {
        Criteria criteria = doTest("name contains '2'");
        Criteria expectedCriteria = Criteria.where("name").regex("2");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldContainsValue4() throws Exception {
        Criteria criteria = doTest("name contains 'azerty'");
        Criteria expectedCriteria = Criteria.where("name").regex("azerty");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldContainsValue5() throws Exception {
        Criteria criteria = doTest("name contains ''");
        Criteria expectedCriteria = Criteria.where("name").regex("");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(5, records.size());
    }

    @Test
    public void testParseFieldContainsValue6() throws Exception {
        Criteria criteria = doTest("name contains 'gha'");
        Criteria expectedCriteria = Criteria.where("name").regex("gha");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testParseFieldContainsValue7() throws Exception {
        Criteria criteria = doTest("name contains 'Gha'");
        Criteria expectedCriteria = Criteria.where("name").regex("Gha");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsValue8() throws Exception {
        Criteria criteria = doTest("name contains 'd+*'");
        Criteria expectedCriteria = Criteria.where("name").regex("d\\+\\*");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("invalid+*name")).count());
    }
}
