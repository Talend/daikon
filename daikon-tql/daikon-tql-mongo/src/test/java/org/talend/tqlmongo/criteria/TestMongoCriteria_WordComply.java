package org.talend.tqlmongo.criteria;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class TestMongoCriteria_WordComply extends TestMongoCriteria_Abstract {

    @Test
    public void testParseFieldCompliesPattern1() {
        Criteria criteria = doTest("name wordComplies '[word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Ll}]{2,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testParseFieldCompliesPattern2() {
        Criteria criteria = doTest("name wordComplies '[Word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
    }

    @Test
    public void testParseFieldCompliesPattern3() {
        Criteria criteria = doTest("name wordComplies '[Word] [digit][word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,} [\\p{Nd}][\\p{Ll}]{2,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldCompliesPattern4() {
        Criteria criteria = doTest("name wordComplies '[Word] [Word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,} \\p{Lu}[\\p{Ll}]{1,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern5() {
        Criteria criteria = doTest("name wordComplies '[Word]_[Number]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,}_\\[Number\\]$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern6() {
        Criteria criteria = doTest("name wordComplies '][word]@'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\][\\p{Ll}]{2,}@$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern7() {
        Criteria criteria = doTest("name wordComplies '[Word] [word] [Word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,} [\\p{Ll}]{2,} \\p{Lu}[\\p{Ll}]{1,}$",
                false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern8() {
        Criteria criteria = doTest("name wordComplies ''");
        Criteria expectedCriteria = Criteria.where("name").is("");
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern9() {
        Criteria criteria = doTest("name wordComplies '[Ideogram]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Han}]$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern10() {
        Criteria criteria = doTest("name wordComplies '[char]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Ll}]$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern11() {
        Criteria criteria = doTest("name wordComplies '[Char]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Lu}]$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern12() {
        Criteria criteria = doTest("name wordComplies '[alnum]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Nd}|\\p{Lu}\\p{Ll}]{2,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());

    }

    @Test
    public void testParseFieldCompliesPattern13() {
        Criteria criteria = doTest("name wordComplies '[IdeogramSeq]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Han}]{2,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern14() {
        Criteria criteria = doTest("name wordComplies '[alnum(CJK)]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Nd}|\\p{Han}]{2,}$", false);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern15() {
        Criteria criteria = doTest("not (name wordComplies '[word]')");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Ll}]{2,}$", true);
        Assert.assertEquals(expectedCriteria.getCriteriaObject(), criteria.getCriteriaObject());
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(4, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("+?'n$")).count());
    }

    private Criteria getExpectedCriteria(String field, String regex, boolean negation) {
        return new Criteria() {
            @Override
            public DBObject getCriteriaObject() {
                if (!negation)
                    return new BasicDBObject(field, new BasicDBObject("$regex", regex));
                return new BasicDBObject(field, new BasicDBObject("$not", new BasicDBObject("$regex", regex)));
            }
        };
    }
}
