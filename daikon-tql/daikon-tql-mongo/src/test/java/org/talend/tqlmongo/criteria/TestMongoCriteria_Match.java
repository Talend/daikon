package org.talend.tqlmongo.criteria;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Match extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldMatchesRegex1() {
        Criteria criteria = doTest("name ~ '^[A-Z][a-z]*$'");
        Criteria expectedCriteria = Criteria.where("name").regex("^[A-Z][a-z]*$");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldMatchesRegex2() {
        Criteria criteria = doTest("name ~ '^[A-Z|a-z]*$'");
        Criteria expectedCriteria = Criteria.where("name").regex("^[A-Z|a-z]*$");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testParseFieldMatchesRegex3() {
        Criteria criteria = doTest("name ~ '^[A-Z]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^[A-Z]");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldMatchesRegex4() {
        Criteria criteria = doTest("name ~ '\\d'"); // contains any digit
        Criteria expectedCriteria = Criteria.where("name").regex("\\d");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldMatchesRegex5() {
        Criteria criteria = doTest("name ~ ''"); // contains any digit
        Criteria expectedCriteria = Criteria.where("name").is("");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }
}
