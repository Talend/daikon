package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.and;
import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.eqFields;
import static org.talend.tql.api.TqlBuilder.isInvalid;
import static org.talend.tql.api.TqlBuilder.or;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

/**
 * Created by achever on 04/07/17.
 */
public class TestTqlApi_And extends TestTqlParser_Abstract {

    @Test
    public void testApiAndSimple1NumericFields() throws Exception {
        // TQL native query
        TqlElement expected = doTest("0001=field(toto) and 0002=field(tata)");
        // TQL api query
        TqlElement tqlElement = and(eqFields("0001", "toto"), eqFields("0002", "tata"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiAndSimple1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1=field(toto) and field2=field(tata)");
        // TQL api query
        TqlElement tqlElement = and(eqFields("field1", "toto"), eqFields("field2", "tata"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiAndSimple2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1=field(value1) and field2=field(value2) and field3=field(value3)");
        // TQL api query
        TqlElement tqlElement = and(eqFields("field1", "value1"), eqFields("field2", "value2"), eqFields("field3", "value3"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiAndSimple3() throws Exception {
        // TQL native query
        TqlElement expected = doTest(
                "field1=field(value1) and field2=field(value2) and field3=field(value3) and field4=field(value4) and field5=field(value5)");
        // TQL api query
        TqlElement tqlElement = and(eqFields("field1", "value1"), //
                eqFields("field2", "value2"), //
                eqFields("field3", "value3"), eqFields("field4", "value4"), eqFields("field5", "value5"));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiAndComposite1() throws Exception {
        // TQL native query
        TqlElement expected = doTest("f1=field(toto) and (f2=field(titi) and f3=field(tata))");
        // TQL api query
        TqlElement tqlElement = and(eqFields("f1", "toto"), and(eqFields("f2", "titi"), eqFields("f3", "tata")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiAndComposite2() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1=field(toto) and f2=field(tutu)) and (f2=field(titi) and f3=field(tata))");
        // TQL api query
        TqlElement tqlElement = and(and(eqFields("f1", "toto"), eqFields("f2", "tutu")),
                and(eqFields("f2", "titi"), eqFields("f3", "tata")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiAndComposite3() throws Exception {
        // TQL native query
        TqlElement expected = doTest("(f1=field(toto) or f2=field(tutu)) and (f2=field(titi) or f3=field(tata))");
        // TQL api query
        TqlElement tqlElement = and(or(eqFields("f1", "toto"), eqFields("f2", "tutu")),
                or(eqFields("f2", "titi"), eqFields("f3", "tata")));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComposite4() throws Exception {
        // TQL native query
        TqlElement expected = doTest(
                "(f1=field(toto) or f2=field(tutu)) and (f2=field(titi) or (f3 = field(tata) and f4 is invalid))");
        // TQL api query
        TqlElement tqlElement = and(or(eqFields("f1", "toto"), eqFields("f2", "tutu")),
                or(eqFields("f2", "titi"), and(eqFields("f3", "tata"), isInvalid("f4"))));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiComposite5() throws Exception {
        // TQL native query
        TqlElement expected = doTest("f1=1 and (f2=2 and (f3=3 and (f4=4 and f5=5 and f6=6)))");
        // TQL api query
        TqlElement tqlElement = and(eq("f1", 1), and(eq("f2", 2), and(eq("f3", 3), and(eq("f4", 4), eq("f5", 5), eq("f6", 6)))));
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
