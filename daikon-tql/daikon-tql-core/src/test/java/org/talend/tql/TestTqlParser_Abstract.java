package org.talend.tql;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.talend.tql.model.TqlElement;
import org.talend.tql.parser.TqlExpressionVisitor;

/**
 * Test general method that parses a string query to the target {@link TqlElement} tree,
 * according to the defined lexer and parser.
 */
public abstract class TestTqlParser_Abstract {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected TqlElement doTest(String query) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(query);
        TqlLexer lexer = new TqlLexer(input);
        TqlParser parser = new TqlParser(new CommonTokenStream(lexer));
        TqlParser.ExpressionContext expression = parser.expression();
        return expression.accept(new TqlExpressionVisitor());
    }
}
