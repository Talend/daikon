package org.talend.tql.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.talend.tql.TqlLexer;
import org.talend.tql.TqlParser;
import org.talend.tql.model.TqlElement;

/**
 * Provides utility to parse TQL queries.
 * @see #parse(String)
 */
public class Tql {

    private Tql() {
    }

    /**
     * Parses the query text and returns a {@link TqlElement AST} of the query.
     * @param query A valid query text.
     * @return A {@link TqlElement AST} of the query.
     */
    public static TqlElement parse(String query) {
        ANTLRInputStream input = new ANTLRInputStream(query);
        TqlLexer lexer = new TqlLexer(input);
        TqlParser parser = new TqlParser(new CommonTokenStream(lexer));
        TqlParser.ExpressionContext expression = parser.expression();
        return expression.accept(new TqlExpressionVisitor());
    }
}
