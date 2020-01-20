package org.talend.tql.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.talend.tql.TqlLexer;
import org.talend.tql.TqlParser;
import org.talend.tql.excp.TqlException;
import org.talend.tql.model.Expression;
import org.talend.tql.model.TqlElement;

/**
 * Provides utility to parse TQL queries.
 * 
 * @see #parse(String)
 */
public class Tql {

    private Tql() {
    }

    /**
     * Parses the query text and returns a {@link TqlElement AST} of the query.
     * 
     * @param query A valid query text.
     * @return A {@link TqlElement AST} of the query.
     */
    public static Expression parse(String query) {
        CharStream input = CharStreams.fromString(query);
        TqlLexer lexer = new TqlLexer(input);
        TqlParser parser = new TqlParser(new CommonTokenStream(lexer));
        TqlParser.ExpressionContext expression = parser.expression();
        TqlElement accept = expression.accept(new TqlExpressionVisitor());
        try {
            return (Expression) accept;
        } catch (ClassCastException e) {
            // we do not know what kind of user data may be in the query so not logging it.
            throw new TqlException("Supplied query is not an expression.", e);
        }
    }
}
