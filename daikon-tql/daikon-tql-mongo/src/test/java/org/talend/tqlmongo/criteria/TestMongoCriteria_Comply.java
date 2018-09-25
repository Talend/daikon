
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
                .regex("^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){7}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("ghassen")).count());
    }

    @Test
    public void testParseFieldCompliesPattern2() {
        Criteria criteria = doTest("name complies 'Aaaaaaa'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){6}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Ghassen")).count());
    }

    @Test
    public void testParseFieldCompliesPattern3() {
        Criteria criteria = doTest("name complies 'Aaaaaa 9aaa'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){5} ([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(1, records.stream().filter(r -> r.getName().equals("Benoit 2eme")).count());
    }

    @Test
    public void testParseFieldCompliesPattern4() {
        Criteria criteria = doTest("name complies 'Aaa Aaaa'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){2} ([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern5() {
        Criteria criteria = doTest("name complies 'Aaaa_99'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}_([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}]){2}$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern6() {
        Criteria criteria = doTest("name complies ']ss@'");
        Criteria expectedCriteria = Criteria.where("name").regex("^\\]s{2}@$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern7() {
        Criteria criteria = doTest("name complies 'Aaaa أبجد Aaaa'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3} أبجد ([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}$");
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

    @Test
    public void testParseFieldCompliesPattern9() {
        Criteria criteria = doTest("name complies 'h'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^(\\x{3041}|\\x{3043}|\\x{3045}|\\x{3047}|\\x{3049}|\\x{3063}|\\x{3083}|\\x{3085}|\\x{3087}|\\x{308E}|\\x{3095}|\\x{3096})$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern10() {
        Criteria criteria = doTest("name complies 'H'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^(\\x{3042}|\\x{3044}|\\x{3046}|\\x{3048}|[\\x{304A}-\\x{3062}]|[\\x{3064}-\\x{3082}]|\\x{3084}|\\x{3086}|[\\x{3088}-\\x{308D}]|[\\x{308F}-\\x{3094}])$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern11() {
        Criteria criteria = doTest("name complies 'k'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^(\\x{30A1}|\\x{30A3}|\\x{30A5}|\\x{30A7}|\\x{30A9}|\\x{30C3}|\\x{30E3}|\\x{30E5}|\\x{30E7}|\\x{30EE}|\\x{30F5}|\\x{30F6}|[\\x{31F0}-\\x{31FF}]|[\\x{FF67}-\\x{FF6F}])$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern12() {
        Criteria criteria = doTest("name complies 'K'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^(\\x{30A2}|\\x{30A4}|\\x{30A6}|\\x{30A8}|[\\x{30AA}-\\x{30C2}]|[\\x{30C4}-\\x{30E2}]|\\x{30E4}|\\x{30E6}|[\\x{30E8}-\\x{30ED}]|[\\x{30EF}-\\x{30F4}]|[\\x{30F7}-\\x{30FA}]|\\x{FF66}|[\\x{FF71}-\\x{FF9D}])$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern13() {
        Criteria criteria = doTest("name complies 'C'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{4E00}-\\x{9FEF}]|[\\x{3400}-\\x{4DB5}]|[\\x{20000}-\\x{2A6D6}]|[\\x{2A700}-\\x{2B734}]|[\\x{2B740}-\\x{2B81D}]|[\\x{2B820}-\\x{2CEA1}]|[\\x{2CEB0}-\\x{2EBE0}]|[\\x{F900}-\\x{FA6D}]|[\\x{FA70}-\\x{FAD9}]|[\\x{2F800}-\\x{2FA1D}]|[\\x{2F00}-\\x{2FD5}]|[\\x{2E80}-\\x{2E99}]|[\\x{2E9B}-\\x{2EF3}]|\\x{3005}|\\x{3007}|[\\x{3021}-\\x{3029}]|[\\x{3038}-\\x{303B}])$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern14() {
        Criteria criteria = doTest("name complies 'G'");
        Criteria expectedCriteria = Criteria.where("name").regex("^([\\x{AC00}-\\x{D7AF}])$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void testParseFieldCompliesPattern15() {
        Criteria criteria = doTest("name complies 'Aaa G K Hhh C !'");
        Criteria expectedCriteria = Criteria.where("name").regex(
                "^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}])([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){2} ([\\x{AC00}-\\x{D7AF}]) (\\x{30A2}|\\x{30A4}|\\x{30A6}|\\x{30A8}|[\\x{30AA}-\\x{30C2}]|[\\x{30C4}-\\x{30E2}]|\\x{30E4}|\\x{30E6}|[\\x{30E8}-\\x{30ED}]|[\\x{30EF}-\\x{30F4}]|[\\x{30F7}-\\x{30FA}]|\\x{FF66}|[\\x{FF71}-\\x{FF9D}]) (\\x{3042}|\\x{3044}|\\x{3046}|\\x{3048}|[\\x{304A}-\\x{3062}]|[\\x{3064}-\\x{3082}]|\\x{3084}|\\x{3086}|[\\x{3088}-\\x{308D}]|[\\x{308F}-\\x{3094}])(\\x{3041}|\\x{3043}|\\x{3045}|\\x{3047}|\\x{3049}|\\x{3063}|\\x{3083}|\\x{3085}|\\x{3087}|\\x{308E}|\\x{3095}|\\x{3096}){2} ([\\x{4E00}-\\x{9FEF}]|[\\x{3400}-\\x{4DB5}]|[\\x{20000}-\\x{2A6D6}]|[\\x{2A700}-\\x{2B734}]|[\\x{2B740}-\\x{2B81D}]|[\\x{2B820}-\\x{2CEA1}]|[\\x{2CEB0}-\\x{2EBE0}]|[\\x{F900}-\\x{FA6D}]|[\\x{FA70}-\\x{FAD9}]|[\\x{2F800}-\\x{2FA1D}]|[\\x{2F00}-\\x{2FD5}]|[\\x{2E80}-\\x{2E99}]|[\\x{2E9B}-\\x{2EF3}]|\\x{3005}|\\x{3007}|[\\x{3021}-\\x{3029}]|[\\x{3038}-\\x{303B}]) !$");
        Assert.assertEquals(expectedCriteria, criteria);
        List<Record> records = this.getRecords(criteria);
        Assert.assertEquals(0, records.size());
    }

}
