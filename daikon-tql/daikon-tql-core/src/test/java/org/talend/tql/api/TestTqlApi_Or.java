package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.and;
import static org.talend.tql.api.TqlBuilder.between;
import static org.talend.tql.api.TqlBuilder.eqFields;
import static org.talend.tql.api.TqlBuilder.or;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

/**
 * Created by achever on 04/07/17.
 */
public class TestTqlApi_Or extends TestTqlParser_Abstract {

    @Test
    public void testApiOrSimple1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1=field(toto) or field2=field(tata)");
        // TQL api query
        TqlElement tqlElement = or(eqFields("field1", "toto"), eqFields("field2", "tata"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiOrSimple2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1=field(value1) or field2=field(value2) or field3=field(value3)");
        // TQL api query
        TqlElement tqlElement = or(eqFields("field1", "value1"), eqFields("field2", "value2"), eqFields("field3", "value3"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiOrSimple3() throws Exception {
        // TQL native query
        TqlElement expected = doTest(
                "field1=field(value1) or field2=field(value2) or field3=field(value3) or field4=field(value4) or field5=field(value5)");
        // TQL api query
        TqlElement tqlElement = or(eqFields("field1", "value1"), eqFields("field2", "value2"), eqFields("field3", "value3"),
                eqFields("field4", "value4"), eqFields("field5", "value5"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiOrBetween1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 between ['1', '2'] or field2 between ['3', '4']");
        // TQL api query
        TqlElement tqlElement = or(between("field1", "1", "2"), between("field2", "3", "4"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiOrBetween2() throws Exception {
        // TQL native query
        TqlElement expected = doTest(
                "field1 between ['1', '2'] or field2 between ['3', '4'] or field3 between ['5', '6'] or field4 between ['7', '8'] or field5 between ['9', '10']");
        // TQL api query
        TqlElement tqlElement = or(between("field1", "1", "2"), between("field2", "3", "4"), between("field3", "5", "6"),
                between("field4", "7", "8"), between("field5", "9", "10"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiOrComposite1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1=field(value1) or f2=field(value2)) or (f3=field(value3) or f4=field(value4))");
        // TQL api query
        TqlElement tqlElement = or(or(eqFields("f1", "value1"), eqFields("f2", "value2")),
                or(eqFields("f3", "value3"), eqFields("f4", "value4")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiOrComposite2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1=field(value1) and f2=field(value2)) or (f3=field(value3) and f4=field(value4))");
        // TQL api query
        TqlElement tqlElement = or(and(eqFields("f1", "value1"), eqFields("f2", "value2")),
                and(eqFields("f3", "value3"), eqFields("f4", "value4")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }
}
