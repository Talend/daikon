package org.talend.tql.parser;

import org.junit.Test;
import org.talend.tql.excp.TqlException;
import org.talend.tql.model.Expression;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TqlTest {

    @Test
    public void parse() throws Exception {
        String query = "toto = 'hello world'";

        Expression parse = Tql.parse(query);

        // no exception
        assertNotNull(parse);
    }

    @Test(expected = TqlException.class)
    public void parse_elementThrowTqlException() throws Exception {
        String query = "toto";

        Tql.parse(query);

        fail(); // should have thrown exception
    }

}