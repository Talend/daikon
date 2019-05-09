package org.talend.tqlmongo.criteria;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Boolean extends TestMongoCriteria_Abstract {

    @Test
    public void testBooleanEqTrue() {
        Criteria criteria = doTest("isGoodBoy = true");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").is(true);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testBooleanNeTrue() {
        Criteria criteria = doTest("isGoodBoy != true");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").ne(true);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(4, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());

    }

    @Test
    public void testBooleanEqFalse() {
        Criteria criteria = doTest("isGoodBoy = false");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").is(false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(4, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());
    }

    @Test
    public void testBooleanNeFalse() {
        Criteria criteria = doTest("isGoodBoy != false");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").ne(false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

}
