package org.talend.tqlmongo.criteria;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_ContainsIgnoreCase extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldContainsIgnoreCaseValue1() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase 'ssen'");
        Criteria expectedCriteria = Criteria.where("name").regex("ssen");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue2() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase 'noi'");
        Criteria expectedCriteria = Criteria.where("name").regex("noi");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue3() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase '2'");
        Criteria expectedCriteria = Criteria.where("name").regex("2");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue4() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase 'azerty'");
        Criteria expectedCriteria = Criteria.where("name").regex("azerty");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue5() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase ''");
        Criteria expectedCriteria = Criteria.where("name").regex("");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(5, records.size());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue6() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase 'gha'");
        Criteria expectedCriteria = Criteria.where("name").regex("gha");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue7() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase 'Gha'");
        Criteria expectedCriteria = Criteria.where("name").regex("Gha");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue8() throws Exception {
        Criteria criteria = doTest("name containsIgnoreCase 'D+*'");
        Criteria expectedCriteria = Criteria.where("name").regex("D\\+\\*");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("invalid+*name")).count());
    }
}
