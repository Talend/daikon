package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.isEmpty;
import static org.talend.tql.api.TqlBuilder.isInvalid;
import static org.talend.tql.api.TqlBuilder.isValid;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.TqlElement;

/**
 * Created by achever on 04/07/17.
 */
public class TestTqlApi_Is extends TestTqlParser_Abstract {

    @Test
    public void testApiIsEmpty() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 is empty");
        // TQL api query
        TqlElement tqlElement = isEmpty("field1");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIsValid() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 is valid");
        // TQL api query
        TqlElement tqlElement = isValid("field1");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiIsInvalid() throws Exception {
        // TQL native query
        TqlElement expected = doTest("field1 is invalid");
        // TQL api query
        TqlElement tqlElement = isInvalid("field1");
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
