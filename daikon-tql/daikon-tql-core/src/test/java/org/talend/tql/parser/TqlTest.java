package org.talend.tql.parser;

import org.junit.Test;
import org.talend.tql.excp.TqlException;

public class TqlTest {

    @Test
    public void parse() throws Exception {
        String query = "toto = 'hello world'";

        Tql.parse(query);

        // no exception
    }

    @Test(expected = TqlException.class)
    public void parse_element() throws Exception {
        String query = "toto";

        Tql.parse(query);
    }

}