package org.talend.tql.parser;

import java.util.stream.IntStream;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.tql.TqlLexer;
import org.talend.tql.TqlParser;
import org.talend.tql.TqlParserVisitor;
import org.talend.tql.excp.TqlException;
import org.talend.tql.model.*;

/**
 * Visitor for building the AST Tql tree.
 */
public class TqlExpressionVisitor implements TqlParserVisitor<TqlElement> {

    private static final Logger LOG = LoggerFactory.getLogger(TqlExpressionVisitor.class);

    @Override
    public TqlElement visitTerminal(TerminalNode node) {
        LOG.debug("Visit terminal node: " + node.getText());
        FieldReference fieldReference = new FieldReference(node.getSymbol().getText());
        LOG.debug("End visit terminal node: " + node.getText());
        return fieldReference;
    }

    @Override
    public TqlElement visitAllFields(TqlParser.AllFieldsContext ctx) {
        LOG.debug("Visit all fields.");
        AllFields allFields = new AllFields();
        LOG.debug("End visit all fields.");
        return allFields;
    }

    @Override
    public TqlElement visitComparisonOperator(TqlParser.ComparisonOperatorContext ctx) {
        LOG.debug("Visit comparison operator: " + ctx.getText());
        TerminalNode child = ctx.getChild(TerminalNode.class, 0);
        Token symbol = child.getSymbol();
        String symbolicName = TqlLexer.VOCABULARY.getSymbolicName(symbol.getType());
        ComparisonOperator.Enum operator = ComparisonOperator.Enum.valueOf(symbolicName);
        LOG.debug("Found operator " + operator);
        ComparisonOperator comparisonOperator = new ComparisonOperator(operator);
        LOG.debug("End visit comparison operator: " + ctx.getText());
        return comparisonOperator;
    }

    @Override
    public TqlElement visitLiteralValue(TqlParser.LiteralValueContext ctx) {
        LOG.debug("Visit literal value: " + ctx.getText());
        TerminalNode child = ctx.getChild(TerminalNode.class, 0);
        Token symbol = child.getSymbol();
        String symbolicName = TqlLexer.VOCABULARY.getSymbolicName(symbol.getType());
        LiteralValue.Enum literalValue = LiteralValue.Enum.valueOf(symbolicName);
        LOG.debug("Found literal value " + literalValue);
        String v = symbol.getText();
        String value = literalValue.equals(LiteralValue.Enum.QUOTED_VALUE) ? v.substring(1, v.length() - 1) : v;
        LiteralValue lv = new LiteralValue(literalValue, value);
        LOG.debug("End visit literal value: " + ctx.getText());
        return lv;
    }

    @Override
    public TqlElement visitFieldReference(TqlParser.FieldReferenceContext ctx) {
        LOG.debug("Visit field reference value: " + ctx.getText());
        TerminalNode child = ctx.getChild(TerminalNode.class, 2);
        Token symbol = child.getSymbol();
        return new FieldReference(symbol.getText());
    }

    @Override
    public TqlElement visitBooleanValue(TqlParser.BooleanValueContext ctx) {
        LOG.debug("Visit boolean value: " + ctx.getText());
        TerminalNode child = ctx.getChild(TerminalNode.class, 0);
        Token symbol = child.getSymbol();
        BooleanValue v = new BooleanValue(symbol.getText());
        LOG.debug("End visit boolean value: " + ctx.getText());
        return v;
    }

    @Override
    public TqlElement visitLiteralComparison(TqlParser.LiteralComparisonContext ctx) {
        LOG.debug("Visit literal comparison: " + ctx.getText());
        TqlElement fieldTqlElement = ctx.getChild(0).accept(this);
        TqlParser.ComparisonOperatorContext comparisonOperator = ctx.getChild(TqlParser.ComparisonOperatorContext.class, 0);
        ComparisonOperator comparisonOperatorTqlElement = (ComparisonOperator) comparisonOperator.accept(this);
        TqlParser.LiteralValueContext literalValue = ctx.getChild(TqlParser.LiteralValueContext.class, 0);
        LiteralValue literalValueTqlElement = (LiteralValue) literalValue.accept(this);
        ComparisonExpression comparisonExpression = new ComparisonExpression(comparisonOperatorTqlElement, fieldTqlElement,
                literalValueTqlElement);
        LOG.debug("End visit literal comparison: " + ctx.getText());
        return comparisonExpression;
    }

    @Override
    public TqlElement visitBooleanComparison(TqlParser.BooleanComparisonContext ctx) {
        LOG.debug("Visit boolean comparison: " + ctx.getText());
        TqlElement fieldTqlElement = ctx.getChild(0).accept(this);
        TerminalNode comparisonOperator = ctx.getChild(TerminalNode.class, 1);
        Token symbol = comparisonOperator.getSymbol();
        String symbolicName = TqlLexer.VOCABULARY.getSymbolicName(symbol.getType());
        TqlParser.BooleanValueContext booleanValue = ctx.getChild(TqlParser.BooleanValueContext.class, 0);
        BooleanValue booleanValueTqlElement = (BooleanValue) booleanValue.accept(this);
        ComparisonExpression comparisonExpression = new ComparisonExpression(
                new ComparisonOperator(ComparisonOperator.Enum.valueOf(symbolicName)), fieldTqlElement, booleanValueTqlElement);
        LOG.debug("End visit boolean comparison: " + ctx.getText());
        return comparisonExpression;
    }

    @Override
    public TqlElement visitFieldComparison(TqlParser.FieldComparisonContext ctx) {
        LOG.debug("Visit field comparison: " + ctx.getText());
        TqlElement field1TqlElement = ctx.getChild(0).accept(this);
        TqlParser.ComparisonOperatorContext comparisonOperator = ctx.getChild(TqlParser.ComparisonOperatorContext.class, 0);
        ComparisonOperator comparisonOperatorTqlElement = (ComparisonOperator) comparisonOperator.accept(this);
        TqlParser.FieldReferenceContext field2 = ctx.getChild(TqlParser.FieldReferenceContext.class, 0);
        FieldReference field2TqlElement = (FieldReference) field2.accept(this);
        ComparisonExpression comparisonExpression = new ComparisonExpression(comparisonOperatorTqlElement, field1TqlElement,
                field2TqlElement);
        LOG.debug("End visit field comparison: " + ctx.getText());
        return comparisonExpression;
    }

    @Override
    public TqlElement visitFieldIsEmpty(TqlParser.FieldIsEmptyContext ctx) {
        LOG.debug("Visit is field empty expression: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        FieldIsEmptyExpression isEmptyExpression = new FieldIsEmptyExpression(fieldName);
        LOG.debug("End visit is field empty expression: " + ctx.getText());
        return isEmptyExpression;
    }

    @Override
    public TqlElement visitFieldIsValid(TqlParser.FieldIsValidContext ctx) {
        LOG.debug("Visit is field valid expression: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        FieldIsValidExpression isValidExpression = new FieldIsValidExpression(fieldName);
        LOG.debug("End visit is field valid expression: " + ctx.getText());
        return isValidExpression;
    }

    @Override
    public TqlElement visitFieldIsInvalid(TqlParser.FieldIsInvalidContext ctx) {
        LOG.debug("Visit is field invalid expression: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        FieldIsInvalidExpression isInvalidExpression = new FieldIsInvalidExpression(fieldName);
        LOG.debug("End visit is field invalid expression: " + ctx.getText());
        return isInvalidExpression;
    }

    @Override
    public TqlElement visitFieldContains(TqlParser.FieldContainsContext ctx) {
        LOG.debug("Visit field contains: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        TerminalNode valueNode = ctx.getChild(TerminalNode.class, 2);

        if (valueNode instanceof ErrorNode)
            throw new TqlException(valueNode.getText());

        String quotedValue = valueNode.getSymbol().getText();
        String value = quotedValue.substring(1, quotedValue.length() - 1);
        FieldContainsExpression fieldContainsExpression = new FieldContainsExpression(fieldName, value);
        LOG.debug("End visit field contains: " + ctx.getText());
        return fieldContainsExpression;
    }

    @Override
    public TqlElement visitFieldMatchesRegexp(TqlParser.FieldMatchesRegexpContext ctx) {
        LOG.debug("Visit field matches: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        TerminalNode regexNode = ctx.getChild(TerminalNode.class, 2);

        if (regexNode instanceof ErrorNode)
            throw new TqlException(regexNode.getText());

        String quotedRegex = regexNode.getSymbol().getText();
        String regex = quotedRegex.substring(1, quotedRegex.length() - 1);
        FieldMatchesRegex fieldMatchesRegex = new FieldMatchesRegex(fieldName, regex);
        LOG.debug("End visit field matches: " + ctx.getText());
        return fieldMatchesRegex;
    }

    @Override
    public TqlElement visitFieldCompliesPattern(TqlParser.FieldCompliesPatternContext ctx) {
        LOG.debug("Visit field complies: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        TerminalNode patternNode = ctx.getChild(TerminalNode.class, 2);

        if (patternNode instanceof ErrorNode)
            throw new TqlException(patternNode.getText());

        String quotedPattern = patternNode.getSymbol().getText();
        String pattern = quotedPattern.substring(1, quotedPattern.length() - 1);
        FieldCompliesPattern fieldCompliesPattern = new FieldCompliesPattern(fieldName, pattern);
        LOG.debug("End visit field complies: " + ctx.getText());
        return fieldCompliesPattern;
    }

    @Override
    public TqlElement visitFieldBetween(TqlParser.FieldBetweenContext ctx) {
        LOG.debug("Visit field between: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        TqlParser.LiteralValueContext value1Node = ctx.getChild(TqlParser.LiteralValueContext.class, 0);
        TqlParser.LiteralValueContext value2Node = ctx.getChild(TqlParser.LiteralValueContext.class, 1);
        LiteralValue v1 = (LiteralValue) value1Node.accept(this);
        LiteralValue v2 = (LiteralValue) value2Node.accept(this);
        FieldBetweenExpression fieldBetween = new FieldBetweenExpression(fieldName, v1, v2);
        LOG.debug("End visit field between: " + ctx.getText());
        return fieldBetween;
    }

    @Override
    public TqlElement visitFieldIn(TqlParser.FieldInContext ctx) {
        LOG.debug("Visit field in: " + ctx.getText());
        TerminalNode field = ctx.getChild(TerminalNode.class, 0);
        String fieldName = field.getSymbol().getText();
        // All children which are not terminal values are the needed literal values (see syntax)
        LiteralValue[] literalValues = ctx.children.stream().filter(c -> c instanceof TqlParser.LiteralValueContext
                || c instanceof TqlParser.BooleanValueContext || c instanceof ErrorNode).map(c -> (LiteralValue) c.accept(this))
                .toArray(LiteralValue[]::new);
        FieldInExpression fieldIn = new FieldInExpression(fieldName, literalValues);
        LOG.debug("End visit field in: " + ctx.getText());
        return fieldIn;
    }

    @Override
    public TqlElement visitNotExpression(TqlParser.NotExpressionContext ctx) {
        LOG.debug("Visit not expression: " + ctx.getText());
        TqlParser.ExpressionContext expressionNode = ctx.getChild(TqlParser.ExpressionContext.class, 0);
        Expression expression = (Expression) expressionNode.accept(this);
        NotExpression notExpression = new NotExpression(expression);
        LOG.debug("End visit not expression: " + ctx.getText());
        return notExpression;
    }

    @Override
    public TqlElement visitExpression(TqlParser.ExpressionContext ctx) {
        TqlParser.OrExpressionContext child = ctx.getChild(TqlParser.OrExpressionContext.class, 0);
        OrExpression node = (OrExpression) child.accept(this);

        LOG.debug("End visiting expression: " + ctx.getText());
        return node;
    }

    @Override
    public TqlElement visitOrExpression(TqlParser.OrExpressionContext ctx) {
        int childCount = ctx.getChildCount();
        Expression[] objects = IntStream // NOSONAR
                .range(0, childCount).filter(i -> i % 2 == 0).mapToObj(i -> {
                    ParseTree child = ctx.getChild(i);
                    return (Expression) child.accept(this);
                }).toArray(Expression[]::new);
        return new OrExpression(objects);
    }

    @Override
    public TqlElement visitAndExpression(TqlParser.AndExpressionContext ctx) {
        int childCount = ctx.getChildCount();
        Expression[] objects = IntStream // NOSONAR
                .range(0, childCount).filter(i -> i % 2 == 0).mapToObj(i -> {
                    ParseTree child = ctx.getChild(i);
                    return (Expression) child.accept(this);
                }).toArray(Expression[]::new);
        return new AndExpression(objects);
    }

    @Override
    public TqlElement visitAtom(TqlParser.AtomContext ctx) {
        LOG.debug("Visit expression: " + ctx.getText());
        int childCount = ctx.getChildCount();
        if (childCount == 1) {
            ParseTree child = ctx.getChild(0);
            TqlElement tqlElement = child.accept(this);
            LOG.debug("End visit expression: " + ctx.getText());
            return tqlElement;
        }
        if (childCount == 3) {
            // ( expression )
            ParseTree child = ctx.getChild(1);
            TqlElement tqlElement = child.accept(this);
            LOG.debug("End visiting expression: " + ctx.getText());
            return tqlElement;
        }
        throw new TqlException("Unexpected expression: " + ctx.getText());
    }

    @Override
    public TqlElement visitErrorNode(ErrorNode node) {
        throw new TqlException("Error: " + node.getText());
    }

    @Override
    public TqlElement visit(ParseTree tree) {
        throw new TqlException("Unhandled tree: " + tree.getText());
    }

    @Override
    public TqlElement visitChildren(RuleNode node) {
        throw new TqlException("Unhandled children node: " + node.getText());
    }
}
