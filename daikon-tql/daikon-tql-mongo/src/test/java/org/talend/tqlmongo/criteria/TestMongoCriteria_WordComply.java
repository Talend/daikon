package org.talend.tqlmongo.criteria;

import java.util.List;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

public class TestMongoCriteria_WordComply extends TestMongoCriteria_Abstract {

    @Test
    public void lowerLatin() {
        Criteria criteria = doTest("name wordComplies '[word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Ll}]{2,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void upperLatin() {
        Criteria criteria = doTest("name wordComplies '[Word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
    }

    @Test
    public void mixedLatin() {
        Criteria criteria = doTest("name wordComplies '[Word] [digit][word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,} [\\p{Nd}][\\p{Ll}]{2,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void mixedLatin2() {
        Criteria criteria = doTest("name wordComplies '[Word] [Word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,} \\p{Lu}[\\p{Ll}]{1,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void underscore() {
        Criteria criteria = doTest("name wordComplies '[Word]_[Number]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,}_\\[Number\\]$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void bracketsAndEmail() {
        Criteria criteria = doTest("name wordComplies '][word]@'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\][\\p{Ll}]{2,}@$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void caseSensitiveWord() {
        Criteria criteria = doTest("name wordComplies '[Word] [word] [Word]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^\\p{Lu}[\\p{Ll}]{1,} [\\p{Ll}]{2,} \\p{Lu}[\\p{Ll}]{1,}$",
                false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void empty() {
        Criteria criteria = doTest("name wordComplies ''");
        Criteria expectedCriteria = Criteria.where("name").is("");
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void ideogram() {
        Criteria criteria = doTest("name wordComplies '[Ideogram]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Han}]$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void lowerChar() {
        Criteria criteria = doTest("name wordComplies '[char]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Ll}]$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void upperChar() {
        Criteria criteria = doTest("name wordComplies '[Char]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Lu}]$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void alnum() {
        Criteria criteria = doTest("name wordComplies '[alnum]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Nd}|\\p{Lu}\\p{Ll}]{2,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit")).count());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());

    }

    @Test
    public void ideogramSeq() {
        Criteria criteria = doTest("name wordComplies '[IdeogramSeq]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Han}]{2,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void hiragana() {
        Criteria criteria = doTest("name wordComplies '[hira]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^([\\x{3041}-\\x{3096}]|\\x{309D}|\\x{309E}|\\x{30FC})$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void hiraganaSeq() {
        Criteria criteria = doTest("name wordComplies '[hiraSeq]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^([\\x{3041}-\\x{3096}]|\\x{309D}|\\x{309E}|\\x{30FC}){2,}$",
                false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void katakana() {
        Criteria criteria = doTest("name wordComplies '[kata]'");
        Criteria expectedCriteria = getExpectedCriteria("name",
                "^([\\x{FF66}-\\x{FF9D}]|[\\x{30A1}-\\x{30FA}]|\\x{30FD}|\\x{30FE}|[\\x{31F0}-\\x{31FF}]|\\x{30FC})$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void katakanaSeq() {
        Criteria criteria = doTest("name wordComplies '[kataSeq]'");
        Criteria expectedCriteria = getExpectedCriteria("name",
                "^([\\x{FF66}-\\x{FF9D}]|[\\x{30A1}-\\x{30FA}]|\\x{30FD}|\\x{30FE}|[\\x{31F0}-\\x{31FF}]|\\x{30FC}){2,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void hangul() {
        Criteria criteria = doTest("name wordComplies '[hangul]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^([\\x{AC00}-\\x{D7AF}])$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void hangulSeq() {
        Criteria criteria = doTest("name wordComplies '[hangulSeq]'");
        Criteria expectedCriteria = getExpectedCriteria("name", "^([\\x{AC00}-\\x{D7AF}]){2,}$", false);
        assertCriteriaEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Ignore("$not with $regex is not supported, see https://jira.talendforge.org/browse/TDS-4339")
    @Test
    public void negation() {
        Criteria criteria = doTest("not (name wordComplies '[word]')");
        Criteria expectedCriteria = getExpectedCriteria("name", "^[\\p{Ll}]{2,}$", true);
        assertCriteriaEquals(expectedCriteria, criteria);
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
            public Document getCriteriaObject() {
                if (!negation)
                    return new Document(field, new Document("$regex", regex));
                return new Document(field, new Document("$not", new Document("$regex", regex)));
            }
        };
    }
}
