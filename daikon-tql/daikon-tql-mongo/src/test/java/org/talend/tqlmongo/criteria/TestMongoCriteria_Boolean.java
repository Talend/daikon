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
    public void testBooleanEqTrue() throws Exception {
        Criteria criteria = doTest("isGoodBoy = true");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").is(true);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("invalid+*name")).count());
    }

    @Test
    public void testBooleanNeTrue() throws Exception {
        Criteria criteria = doTest("isGoodBoy != true");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").ne(true);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());

    }

    @Test
    public void testBooleanEqFalse() throws Exception {
        Criteria criteria = doTest("isGoodBoy = false");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").is(false);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testBooleanNeFalse() throws Exception {
        Criteria criteria = doTest("isGoodBoy != false");
        Criteria expectedCriteria = Criteria.where("isGoodBoy").ne(false);
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("invalid+*name")).count());
    }

}
