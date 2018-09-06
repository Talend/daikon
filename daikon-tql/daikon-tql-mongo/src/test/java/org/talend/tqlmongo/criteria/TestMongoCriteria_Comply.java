
package org.talend.tqlmongo.criteria;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by gmzoughi on 06/07/16.
 */
public class TestMongoCriteria_Comply extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldCompliesPattern1() {
        Criteria criteria = doTest("name complies 'aaaaaaa'");
        Criteria expectedCriteria = Criteria.where("name")
                .regex("^[a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testParseFieldCompliesPattern2() {
        Criteria criteria = doTest("name complies 'Aaaaaaa'");
        Criteria expectedCriteria = Criteria.where("name")
                .regex("^[A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldCompliesPattern3() {
        Criteria criteria = doTest("name complies 'Aaaaaa 9aaa'");
        Criteria expectedCriteria = Criteria.where("name")
                .regex("^[A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ] [0-9][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldCompliesPattern4() {
        Criteria criteria = doTest("name complies 'Aaa Aaaa'");
        Criteria expectedCriteria = Criteria.where("name")
                .regex("^[A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ] [A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern5() {
        Criteria criteria = doTest("name complies 'Aaaa_99'");
        Criteria expectedCriteria = Criteria.where("name").regex("^[A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ]_[0-9][0-9]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern6() {
        Criteria criteria = doTest("name complies ']ss@'");
        Criteria expectedCriteria = Criteria.where("name").regex("^]ss@$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern7() {
        Criteria criteria = doTest("name complies 'Aaaa أبجد Aaaa'");
        Criteria expectedCriteria = Criteria.where("name")
                .regex("^[A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ] أبجد [A-Z|À-ß][a-z|à-ÿ][a-z|à-ÿ][a-z|à-ÿ]$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern8() {
        Criteria criteria = doTest("name complies ''");
        Criteria expectedCriteria = Criteria.where("name").is("");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }
}
