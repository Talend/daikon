package org.talend.tqlmongo.criteria;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Between extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldBetweenQuoted() throws Exception {
        Criteria criteria = doTest("name between ['A', 'Z']");
        Criteria expectedCriteria = Criteria.where("name").gte("A").lte("Z");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldBetweenInt() throws Exception {
        Criteria criteria = doTest("age between [27, 29]");
        Criteria expectedCriteria = Criteria.where("age").gte(27L).lte(29L);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 28.8).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 29).count());
    }

    @Test
    public void testParseFieldBetweenIntOpenLowerBound() throws Exception {
        Criteria criteria = doTest("age between ]27, 29]");
        Criteria expectedCriteria = Criteria.where("age").gt(27L).lte(29L);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 28.8).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 29).count());
    }

    @Test
    public void testParseFieldBetweenIntOpenUpperBound() throws Exception {
        Criteria criteria = doTest("age between [27, 29[");
        Criteria expectedCriteria = Criteria.where("age").gte(27L).lt(29L);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 28.8).count());
    }

    @Test
    public void testParseFieldBetweenIntBothOpenBounds() throws Exception {
        Criteria criteria = doTest("age between ]27, 29[");
        Criteria expectedCriteria = Criteria.where("age").gt(27L).lt(29L);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 28.8).count());
    }

    @Test
    public void testParseFieldBetweenDecimal() throws Exception {
        Criteria criteria = doTest("age between [27.0, 29.0]");
        Criteria expectedCriteria = Criteria.where("age").gte(27.0).lte(29.0);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 28.8).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getAge() == 29.0).count());
    }
}
