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
    public void testParseFieldContainsIgnoreCaseValue1() {
        Criteria criteria = doTest("name containsIgnoreCase 'ssen'");
        Criteria expectedCriteria = Criteria.where("name").regex("ssen", "i");
        assertCriteriaEquals(criteria, expectedCriteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue2() {
        Criteria criteria = doTest("name containsIgnoreCase 'noi'");
        Criteria expectedCriteria = Criteria.where("name").regex("noi", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue3() {
        Criteria criteria = doTest("name containsIgnoreCase '2'");
        Criteria expectedCriteria = Criteria.where("name").regex("2", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue4() {
        Criteria criteria = doTest("name containsIgnoreCase 'azerty'");
        Criteria expectedCriteria = Criteria.where("name").regex("azerty", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue5() {
        Criteria criteria = doTest("name containsIgnoreCase ''");
        Criteria expectedCriteria = Criteria.where("name").regex("", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(5, records.size());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue6() {
        Criteria criteria = doTest("name containsIgnoreCase 'gha'");
        Criteria expectedCriteria = Criteria.where("name").regex("gha", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue7() {
        Criteria criteria = doTest("name containsIgnoreCase 'Gha'");
        Criteria expectedCriteria = Criteria.where("name").regex("Gha", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue8() {
        Criteria criteria = doTest("name containsIgnoreCase '+'");
        Criteria expectedCriteria = Criteria.where("name").regex("\\+", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue9() {
        Criteria criteria = doTest("name containsIgnoreCase '?'");
        Criteria expectedCriteria = Criteria.where("name").regex("\\?", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue10() {
        Criteria criteria = doTest("name containsIgnoreCase '$'");
        Criteria expectedCriteria = Criteria.where("name").regex("\\$", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());
    }

    @Test
    public void testParseFieldContainsIgnoreCaseValue11() {
        Criteria criteria = doTest("name containsIgnoreCase '\\''");
        Criteria expectedCriteria = Criteria.where("name").regex("'", "i");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());
    }
}
