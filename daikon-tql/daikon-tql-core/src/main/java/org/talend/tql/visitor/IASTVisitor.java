package org.talend.tql.visitor;

import org.talend.tql.model.*;

/**
 * Visitor contract to be used whenever the TQL tree should be parsed.
 */

public interface IASTVisitor<T> {

    /**
     * Visits a {@link TqlElement}
     * 
     * @param elt element to visit
     */
    T visit(TqlElement elt);

    /**
     * Visits a {@link ComparisonOperator}
     * 
     * @param elt element to visit
     */
    T visit(ComparisonOperator elt);

    /**
     * Visits a {@link LiteralValue}
     * 
     * @param elt element to visit
     */
    T visit(LiteralValue elt);

    /**
     * Visits a {@link FieldReference}
     * 
     * @param elt element to visit
     */
    T visit(FieldReference elt);

    /**
     * Visits a {@link Expression}
     * 
     * @param elt element to visit
     */
    T visit(Expression elt);

    /**
     * Visits a {@link AndExpression}
     * 
     * @param elt element to visit
     */
    T visit(AndExpression elt);

    /**
     * Visits a {@link OrExpression}
     * 
     * @param elt element to visit
     */
    T visit(OrExpression elt);

    /**
     * Visits a {@link ComparisonExpression}
     * 
     * @param elt element to visit
     */
    T visit(ComparisonExpression elt);

    /**
     * Visits a {@link FieldInExpression}
     * 
     * @param elt element to visit
     */
    T visit(FieldInExpression elt);

    /**
     * Visits a {@link FieldIsEmptyExpression}
     * 
     * @param elt element to visit
     */
    T visit(FieldIsEmptyExpression elt);

    /**
     * Visits a {@link FieldIsValidExpression}
     * 
     * @param elt element to visit
     */
    T visit(FieldIsValidExpression elt);

    /**
     * Visits a {@link FieldIsInvalidExpression}
     * 
     * @param elt element to visit
     */
    T visit(FieldIsInvalidExpression elt);

    /**
     * Visits a {@link FieldIsNullExpression}
     *
     * @param elt element to visit
     */
    T visit(FieldIsNullExpression elt);

    /**
     * Visits a {@link FieldMatchesRegex}
     * 
     * @param elt element to visit
     */
    T visit(FieldMatchesRegex elt);

    /**
     * Visits a {@link FieldCompliesPattern}
     * 
     * @param elt element to visit
     */
    T visit(FieldCompliesPattern elt);

    /**
     * Visits a {@link FieldWordCompliesPattern}
     * 
     * @param elt element to visit
     */
    T visit(FieldWordCompliesPattern elt);

    /**
     * Visits a {@link FieldBetweenExpression}
     * 
     * @param elt element to visit
     */
    T visit(FieldBetweenExpression elt);

    /**
     * Visits a {@link NotExpression}
     * 
     * @param elt element to visit
     */
    T visit(NotExpression elt);

    /**
     * Visits a {@link FieldContainsExpression}
     * 
     * @param elt element to visit
     */
    T visit(FieldContainsExpression elt);

    /**
     * Visits a {@link AllFields}
     * 
     * @param allFields the element that represent all fields.
     */
    T visit(AllFields allFields);
}
