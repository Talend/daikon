package org.talend.tql.visitor;

import org.talend.tql.model.*;

/**
 * Visitor contract to be used whenever the TQL tree should be parsed.
 */

public interface IASTVisitor<T> {

    T visit(TqlElement elt);

    T visit(ComparisonOperator elt);

    T visit(LiteralValue elt);

    T visit(FieldReference elt);

    T visit(Expression elt);

    T visit(AndExpression elt);

    T visit(OrExpression elt);

    T visit(ComparisonExpression elt);

    T visit(FieldInExpression elt);

    T visit(FieldIsEmptyExpression elt);

    T visit(FieldIsValidExpression elt);

    T visit(FieldIsInvalidExpression elt);

    T visit(FieldMatchesRegex elt);

    T visit(FieldCompliesPattern elt);

    T visit(FieldBetweenExpression elt);

    T visit(NotExpression elt);

    T visit(FieldContainsExpression elt);
}
