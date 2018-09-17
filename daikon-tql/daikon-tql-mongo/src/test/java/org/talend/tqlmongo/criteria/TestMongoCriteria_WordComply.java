package org.talend.tqlmongo.criteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class TestMongoCriteria_WordComply extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldCompliesPattern1() {
        Criteria criteria = doTest("name wordComplies '[word]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^[\\p{Ll}]{2,}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testParseFieldCompliesPattern2() {
        Criteria criteria = doTest("name wordComplies '[Word]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\p{Lu}[\\p{Ll}]{1,}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
    }

    @Test
    public void testParseFieldCompliesPattern3() {
        Criteria criteria = doTest("name wordComplies '[Word] [digit][word]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\p{Lu}[\\p{Ll}]{1,} [\\p{Nd}][\\p{Ll}]{2,}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldCompliesPattern4() {
        Criteria criteria = doTest("name wordComplies '[Word] [Word]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\p{Lu}[\\p{Ll}]{1,} \\p{Lu}[\\p{Ll}]{1,}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern5() {
        Criteria criteria = doTest("name wordComplies '[Word]_[Number]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\p{Lu}[\\p{Ll}]{1,}_\\[Number\\]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern6() {
        Criteria criteria = doTest("name wordComplies '][word]@'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\][\\p{Ll}]{2,}@$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern7() {
        Criteria criteria = doTest("name wordComplies '[Word] [word] [Word]'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\p{Lu}[\\p{Ll}]{1,} [\\p{Ll}]{2,} \\p{Lu}[\\p{Ll}]{1,}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern8() {
        Criteria criteria = doTest("name wordComplies ''");
        Criteria expectedCriteria = Criteria.where("name").is("");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }
}
