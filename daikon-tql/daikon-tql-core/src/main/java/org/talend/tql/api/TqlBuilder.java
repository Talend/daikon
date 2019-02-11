package org.talend.tql.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.talend.tql.excp.TqlException;
import org.talend.tql.model.AndExpression;
import org.talend.tql.model.Atom;
import org.talend.tql.model.BooleanValue;
import org.talend.tql.model.ComparisonExpression;
import org.talend.tql.model.ComparisonOperator;
import org.talend.tql.model.Expression;
import org.talend.tql.model.FieldBetweenExpression;
import org.talend.tql.model.FieldCompliesPattern;
import org.talend.tql.model.FieldContainsExpression;
import org.talend.tql.model.FieldInExpression;
import org.talend.tql.model.FieldIsEmptyExpression;
import org.talend.tql.model.FieldIsInvalidExpression;
import org.talend.tql.model.FieldIsValidExpression;
import org.talend.tql.model.FieldMatchesRegex;
import org.talend.tql.model.FieldReference;
import org.talend.tql.model.FieldWordCompliesPattern;
import org.talend.tql.model.LiteralValue;
import org.talend.tql.model.NotExpression;
import org.talend.tql.model.OrExpression;

/**
 * Class providing a simple functional API to build TQL expressions
 * <p>
 * Created by achever on 30/06/17.
 */
public class TqlBuilder {

    private TqlBuilder() {
        // Hide implicit public constructor.
    }

    /**
     * Build a "equal" TQL expression with a field and a String value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression eq(String fieldname, String value) {
        return comparisonOperationString(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.EQ));
    }

    /**
     * Build a "equal" TQL expression with a field and a boolean value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression eq(String fieldname, boolean value) {
        return comparisonOperationBoolean(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.EQ));
    }

    /**
     * Build a "equal" TQL expression with a field and a int value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression eq(String fieldname, int value) {
        return comparisonOperationInt(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.EQ));
    }

    /**
     * Build a "equal" TQL expression with a field and a double value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression eq(String fieldname, double value) {
        return comparisonOperationDouble(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.EQ));
    }

    /**
     * Build a "equal" TQL expression with 2 fields
     *
     * @param fieldname1 field
     * @param fieldname2 field
     * @return TQL Expression
     */
    public static Expression eqFields(String fieldname1, String fieldname2) {
        return comparisonOperationField(fieldname1, fieldname2, new ComparisonOperator(ComparisonOperator.Enum.EQ));
    }

    /**
     * Build a "not equal" TQL expression with a field and a String value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression neq(String fieldname, String value) {
        return comparisonOperationString(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.NEQ));
    }

    /**
     * Build a "not equal" TQL expression with a field and a boolean value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression neq(String fieldname, boolean value) {
        return comparisonOperationBoolean(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.NEQ));
    }

    /**
     * Build a "not equal" TQL expression with a field and a int value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression neq(String fieldname, int value) {
        return comparisonOperationInt(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.NEQ));
    }

    /**
     * Build a "not equal" TQL expression with a field and a double value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression neq(String fieldname, double value) {
        return comparisonOperationDouble(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.NEQ));
    }

    /**
     * Build a "equal TQL" expression with 2 fields
     *
     * @param fieldname1 field
     * @param fieldname2 field
     * @return TQL Expression
     */
    public static Expression neqFields(String fieldname1, String fieldname2) {
        return comparisonOperationField(fieldname1, fieldname2, new ComparisonOperator(ComparisonOperator.Enum.NEQ));
    }

    /**
     * Build a "less than" TQL expression with a field and a int value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression lt(String fieldname, int value) {
        return comparisonOperationInt(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.LT));
    }

    /**
     * Build a "less than" TQL expression with a field and a double value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression lt(String fieldname, double value) {
        return comparisonOperationDouble(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.LT));
    }

    /**
     * Build a "less than" TQL expression with 2 fields
     *
     * @param fieldname1 field
     * @param fieldname2 field
     * @return TQL Expression
     */
    public static Expression ltFields(String fieldname1, String fieldname2) {
        return comparisonOperationField(fieldname1, fieldname2, new ComparisonOperator(ComparisonOperator.Enum.LT));
    }

    /**
     * Build a "greater than" TQL expression with a field and a int value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression gt(String fieldname, int value) {
        return comparisonOperationInt(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.GT));
    }

    /**
     * Build a "greater than" TQL expression with a field and a double value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression gt(String fieldname, double value) {
        return comparisonOperationDouble(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.GT));
    }

    /**
     * Build a "greater than" TQL expression with 2 fields
     *
     * @param fieldname1 field
     * @param fieldname2 field
     * @return TQL Expression
     */
    public static Expression gtFields(String fieldname1, String fieldname2) {
        return comparisonOperationField(fieldname1, fieldname2, new ComparisonOperator(ComparisonOperator.Enum.GT));
    }

    /**
     * Build a "less or equal than" TQL expression with a field and a int value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression lte(String fieldname, int value) {
        return comparisonOperationInt(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.LET));
    }

    /**
     * Build a "less or equal than" TQL expression with a field and a double value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression lte(String fieldname, double value) {
        return comparisonOperationDouble(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.LET));
    }

    /**
     * Build a "less or equal than" TQL expression with 2 fields
     *
     * @param fieldname1 field
     * @param fieldname2 field
     * @return TQL Expression
     */
    public static Expression lteFields(String fieldname1, String fieldname2) {
        return comparisonOperationField(fieldname1, fieldname2, new ComparisonOperator(ComparisonOperator.Enum.LET));
    }

    /**
     * Build a "greater or equal than" TQL expression with a field and a int value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression gte(String fieldname, int value) {
        return comparisonOperationInt(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.GET));
    }

    /**
     * Build a "greater or equal than" TQL expression with a field and a double value
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression gte(String fieldname, double value) {
        return comparisonOperationDouble(fieldname, value, new ComparisonOperator(ComparisonOperator.Enum.GET));
    }

    /**
     * Build a "greater or equal than" TQL expression with 2 fields
     *
     * @param fieldname1 field
     * @param fieldname2 field
     * @return TQL Expression
     */
    public static Expression gteFields(String fieldname1, String fieldname2) {
        return comparisonOperationField(fieldname1, fieldname2, new ComparisonOperator(ComparisonOperator.Enum.GET));
    }

    /**
     * Build a "between" TQL expression with int values
     *
     * @param fieldname field
     * @param value1 field
     * @param value2 field
     * @return TQL Expression
     */
    public static Expression between(String fieldname, int value1, int value2) {
        return between(fieldname, String.valueOf(value1), String.valueOf(value2), LiteralValue.Enum.INT);
    }

    /**
     * Build a "between" TQL expression with double values
     *
     * @param fieldname field
     * @param value1 field
     * @param value2 field
     * @return TQL Expression
     */
    public static Expression between(String fieldname, double value1, double value2) {
        return between(fieldname, String.valueOf(value1), String.valueOf(value2), LiteralValue.Enum.DECIMAL);
    }

    /**
     * Build a "between" TQL expression with String values
     *
     * @param fieldname field
     * @param value1 field
     * @param value2 field
     * @return TQL Expression
     */
    public static Expression between(String fieldname, String value1, String value2) {
        return between(fieldname, String.valueOf(value1), String.valueOf(value2), LiteralValue.Enum.QUOTED_VALUE);
    }

    /**
     * Build a "contains" TQL expression
     *
     * @param fieldname field
     * @param value value
     * @return TQL Expression
     */
    public static Expression contains(String fieldname, String value) {

        // Creating simple contains expression
        FieldContainsExpression fieldContainsExpression = new FieldContainsExpression(new FieldReference(fieldname), value);
        Expression[] fieldContainsExpressions = new Expression[] { fieldContainsExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldContainsExpressions);
        return new OrExpression(andExpression);

    }

    public static Expression containsIgnoreCase(String fieldname, String value) {

        // Creating simple contains expression
        FieldContainsExpression fieldContainsExpression = new FieldContainsExpression(new FieldReference(fieldname), value,
                false);
        Expression[] fieldContainsExpressions = new Expression[] { fieldContainsExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldContainsExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "complies" TQL expression
     *
     * @param fieldname field
     * @param pattern pattern
     * @return TQL Expression
     */
    public static Expression complies(String fieldname, String pattern) {

        // Creating simple complies expression
        FieldCompliesPattern fieldCompliesPattern = new FieldCompliesPattern(new FieldReference(fieldname), pattern);
        Expression[] fieldCompliesPatternExpressions = new Expression[] { fieldCompliesPattern };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldCompliesPatternExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "wordComplies" TQL expression
     *
     * @param fieldname field
     * @param pattern pattern
     * @return TQL Expression
     */
    public static Expression wordComplies(String fieldname, String pattern) {

        // Creating simple wordComplies expression
        FieldWordCompliesPattern fieldWordCompliesPattern = new FieldWordCompliesPattern(new FieldReference(fieldname), pattern);
        Expression[] fieldWordCompliesPatternExpressions = new Expression[] { fieldWordCompliesPattern };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldWordCompliesPatternExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "match" TQL expression with a regexp
     *
     * @param fieldname field
     * @param regexp regexp
     * @return TQL Expression
     */
    public static Expression match(String fieldname, String regexp) {

        // Creating simple match expression
        FieldMatchesRegex fieldMatchesRegex = new FieldMatchesRegex(new FieldReference(fieldname), regexp);
        Expression[] fieldCompliesPatternExpressions = new Expression[] { fieldMatchesRegex };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldCompliesPatternExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "is empty" TQL expression
     *
     * @param fieldname field
     * @return TQL Expression
     */
    public static Expression isEmpty(String fieldname) {

        // Creating simple isEmpty expression
        FieldIsEmptyExpression fieldIsEmptyExpression = new FieldIsEmptyExpression(new FieldReference(fieldname));
        Expression[] fieldIsEmptyExpressions = new Expression[] { fieldIsEmptyExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldIsEmptyExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "is invalid" TQL expression
     *
     * @param fieldname field
     * @return TQL Expression
     */
    public static Expression isInvalid(String fieldname) {

        // Creating simple isInvalid expression
        FieldIsInvalidExpression fieldIsInvalidExpression = new FieldIsInvalidExpression(new FieldReference(fieldname));
        Expression[] fieldIsInvalidExpressions = new Expression[] { fieldIsInvalidExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldIsInvalidExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "is valid" TQL expression
     *
     * @param fieldname field
     * @return TQL Expression
     */
    public static Expression isValid(String fieldname) {

        // Creating simple isValid expression
        FieldIsValidExpression fieldIsValidExpression = new FieldIsValidExpression(new FieldReference(fieldname));
        Expression[] fieldIsValidExpressions = new Expression[] { fieldIsValidExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldIsValidExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "in" TQL expression with String values
     *
     * @param fieldname field
     * @param values values
     * @return TQL Expression
     */
    public static Expression in(String fieldname, String... values) {

        // Converting String to literal values
        LiteralValue[] literalValues = Arrays.stream(values).map(v -> new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, v))
                .toArray(LiteralValue[]::new);

        return inLiteralValues(fieldname, literalValues);

    }

    /**
     * Build a "in" TQL expression with Integer values
     *
     * @param fieldname field
     * @param values values
     * @return TQL Expression
     */
    public static Expression in(String fieldname, int... values) {

        LiteralValue[] literalValues = Arrays.stream(values)
                .mapToObj(v -> new LiteralValue(LiteralValue.Enum.INT, String.valueOf(v))).toArray(LiteralValue[]::new);

        return inLiteralValues(fieldname, literalValues);

    }

    /**
     * Build a "in" TQL expression with Double values
     *
     * @param fieldname field
     * @param values values
     * @return TQL Expression
     */
    public static Expression in(String fieldname, double... values) {

        LiteralValue[] literalValues = Arrays.stream(values)
                .mapToObj(v -> new LiteralValue(LiteralValue.Enum.DECIMAL, String.valueOf(v))).toArray(LiteralValue[]::new);

        return inLiteralValues(fieldname, literalValues);

    }

    /**
     * Build a "in" TQL expression with Boolean values
     *
     * @param fieldname field
     * @param values values
     * @return TQL Expression
     */
    public static Expression in(String fieldname, Boolean... values) {

        BooleanValue[] booleanValues = Arrays.stream(values).map(v -> new BooleanValue(String.valueOf(v)))
                .toArray(BooleanValue[]::new);

        return inLiteralValues(fieldname, booleanValues);

    }

    /**
     * Build a "not" TQL expression with parameter TQL expression
     *
     * @param expression tql expression
     * @return TQL Expression
     */
    public static Expression not(Expression expression) {

        // Cloning the expression
        Expression newSubtreeExpression = cloneExpression(expression);

        // Inserting it into a new expression root
        NotExpression newNotExpression = new NotExpression(newSubtreeExpression);
        AndExpression newAndExpression = new AndExpression(newNotExpression);
        return new OrExpression(newAndExpression);

    }

    /**
     * Sub method used to build "In" TQL Expressions
     *
     * @param fieldname field
     * @param literalValues values
     * @return TQL Expression
     */
    private static Expression inLiteralValues(String fieldname, LiteralValue[] literalValues) {

        // Creating simple in expression
        FieldInExpression fieldInExpression = new FieldInExpression(new FieldReference(fieldname), literalValues);
        Expression[] fieldInExpressions = new Expression[] { fieldInExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldInExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "or" TQL expression with parameter TQL expressions
     *
     * @param expressions tql expressions
     * @return TQL Expression
     */
    public static Expression or(Expression... expressions) {

        /*
         * Note :
         * 'current*' variables are coming from the parameter
         * 'new*' the newly created variables to build the new AST
         */

        // First level AndExpressions that will populate the new TQL AST
        List<AndExpression> newAndExpressions = new ArrayList<>();

        // Going through the parameter expressions list
        for (Expression currentExpression : expressions) {

            // Getting nodes (AndExpressions) under the AST root
            List<Expression> currentAndExpressions = Arrays.asList(((OrExpression) currentExpression).getExpressions());

            // Testing if currentExpression is composite or simple
            boolean composite = isComposite(currentExpression);

            // Case 1 : the current expression is a simple one
            if (!composite) {

                // Cloning the first level "andExpr"
                List<Expression> currentAtomsExpression = Arrays
                        .asList(((AndExpression) currentAndExpressions.get(0)).getExpressions());
                Atom[] newAtomTab = currentAtomsExpression.toArray(new Atom[0]);
                // Adding it with the first level andExpressions of the new AST
                AndExpression newAndExpression = new AndExpression(newAtomTab);
                newAndExpressions.add(newAndExpression);
            }
            // Case 2 : the current expression is a composite expression
            else {
                // Cloning the current expression
                Expression newSubtreeOrExpression = cloneExpression(currentExpression);
                // Adding the new subtree under a new AndExpression of the new AST
                newAndExpressions.add(new AndExpression(newSubtreeOrExpression));
            }

        }

        // Creating a new TQL AST from the newly built AndExpressions
        Expression[] tab = newAndExpressions.toArray(new Expression[0]);
        return new OrExpression(tab);

    }

    /**
     * Build a "and" TQL expression with parameter TQL expressions
     *
     * @param expressions tql expressions
     * @return TQL Expression
     */
    public static Expression and(Expression... expressions) {

        /*
         * Note :
         * 'current*' variables are coming from the parameter
         * 'new*' the newly created variables to build the new AST
         */

        // First level Atom expressions that will populate the new TQL AST
        List<Expression> newAtomExpressions = new ArrayList<>();

        // Going through the parameter expressions list
        for (Expression currentExpression : expressions) {

            // Getting nodes (AndExpressions) under the AST root
            List<Expression> currentAndExpressions = Arrays.asList(((OrExpression) currentExpression).getExpressions());

            // Testing if currentExpression is composite or simple
            boolean composite = isComposite(currentExpression);

            // Case 1 : currentExpression is a simple one
            if (!composite) {

                // Cloning the atom expressions
                List<Expression> currentAtomsExpression = Arrays
                        .asList(((AndExpression) currentAndExpressions.get(0)).getExpressions());
                newAtomExpressions.add(currentAtomsExpression.get(0));

            }
            // Case 2: the current Expression is a composite expression (and, or, not)
            else {

                // Cloning the current expression
                Expression newSubtreeOrExpression = cloneExpression(currentExpression);
                // Adding the subtree to a new Atom expression in the newly built AST
                newAtomExpressions.add(newSubtreeOrExpression);
            }
        }

        Expression[] newAtomTabs = newAtomExpressions.toArray(new Expression[0]);

        AndExpression newAndExpression = new AndExpression(newAtomTabs);
        AndExpression[] newAndExpressionTab = new AndExpression[1];
        newAndExpressionTab[0] = newAndExpression;

        // Setting the newAndExpressions into a newly created TQL Expression
        return new OrExpression(newAndExpressionTab);

    }

    /**
     * Tests if the expression is simple (comparison, between, is empty...) or composite (and, or, not)
     *
     * @param expression expression under test
     * @return true if composite else false
     */
    private static boolean isComposite(Expression expression) {

        Expression[] andExpressions = ((OrExpression) expression).getExpressions();

        // No andExpression
        if (andExpressions.length == 0) {
            throw new TqlException("Input TQL expression is invalid " + expression);
        }
        // One single andExpression
        else if (andExpressions.length == 1) {
            Expression[] atoms = ((AndExpression) andExpressions[0]).getExpressions();

            if (atoms.length == 0)
                throw new TqlException("Input TQL expression is invalid " + expression);
            // Simple expression : just one AndExpression and one Atom
            else
                return atoms.length != 1;
        }
        // Several andExpressions
        else {
            return true;
        }

    }

    /**
     * Clone a TQL Expression
     *
     * @param expression TQL to be cloned
     * @return cloned TQL expression
     */
    public static Expression cloneExpression(Expression expression) {

        Expression[] currentAndExpressions = ((OrExpression) expression).getExpressions();

        List<AndExpression> newAndExpressions = new ArrayList<>();

        // Go through the list of andExpressions
        for (Expression currentAndExpression : currentAndExpressions) {

            List<Expression> currentAtomsExpression = Arrays.asList(((AndExpression) currentAndExpression).getExpressions());

            Expression[] newAtomTab = currentAtomsExpression.toArray(new Expression[0]);

            AndExpression newAndExpression = new AndExpression(newAtomTab);

            // Clone the list
            newAndExpressions.add(newAndExpression);
        }

        AndExpression[] newAndExpressionsTab = newAndExpressions.toArray(new AndExpression[0]);

        // Return it as a new TQL Expression
        return new OrExpression(newAndExpressionsTab);
    }

    /**
     * Build a comparison TQL expression with a field and a string value
     *
     * @param fieldname fieldname
     * @param value value
     * @param operator operator
     * @return TQL Expression
     */
    private static Expression comparisonOperationString(String fieldname, String value, ComparisonOperator operator) {

        // Creating simple between expression
        LiteralValue literalValue = new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, value);
        ComparisonExpression comparisonExpression = new ComparisonExpression(operator, new FieldReference(fieldname),
                literalValue);
        Expression[] fieldEqualsExpressions = new Expression[] { comparisonExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldEqualsExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a comparison TQL expression with a field and a int value
     *
     * @param fieldname fieldname
     * @param value value
     * @param operator operator
     * @return TQL Expression
     */
    private static Expression comparisonOperationInt(String fieldname, int value, ComparisonOperator operator) {

        // Creating simple between expression
        LiteralValue literalValue = new LiteralValue(LiteralValue.Enum.INT, String.valueOf(value));
        ComparisonExpression comparisonExpression = new ComparisonExpression(operator, new FieldReference(fieldname),
                literalValue);
        Expression[] fieldEqualsExpressions = new Expression[] { comparisonExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldEqualsExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a comparison TQL expression with a field and a double value
     *
     * @param fieldname fieldname
     * @param value value
     * @param operator operator
     * @return TQL Expression
     */
    private static Expression comparisonOperationDouble(String fieldname, double value, ComparisonOperator operator) {

        // Creating simple between expression
        LiteralValue literalValue = new LiteralValue(LiteralValue.Enum.DECIMAL, String.valueOf(value));
        ComparisonExpression comparisonExpression = new ComparisonExpression(operator, new FieldReference(fieldname),
                literalValue);
        Expression[] fieldEqualsExpressions = new Expression[] { comparisonExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldEqualsExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "comparison" TQL expression with 2 fields
     *
     * @param fieldname1 fieldname1
     * @param fieldname2 fieldname2
     * @param operator operator
     * @return TQL Expression
     */
    private static Expression comparisonOperationField(String fieldname1, String fieldname2, ComparisonOperator operator) {

        // Creating simple between expression
        FieldReference fieldReference = new FieldReference(fieldname2);
        ComparisonExpression comparisonExpression = new ComparisonExpression(operator, new FieldReference(fieldname1),
                fieldReference);
        Expression[] fieldEqualsExpressions = new Expression[] { comparisonExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldEqualsExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "comparison" TQL expression with a field and a boolean value
     *
     * @param fieldname fieldname1
     * @param value value
     * @param operator operator
     * @return TQL Expression
     */
    private static Expression comparisonOperationBoolean(String fieldname, boolean value, ComparisonOperator operator) {

        // Creating simple between expression
        ComparisonExpression comparisonExpression = new ComparisonExpression(operator, new FieldReference(fieldname),
                new BooleanValue(String.valueOf(value)));
        Expression[] fieldEqualsExpressions = new Expression[] { comparisonExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldEqualsExpressions);
        return new OrExpression(andExpression);

    }

    /**
     * Build a "between" TQL expression with a field and 2 String values
     *
     * @param fieldname fieldname
     * @param value1 value1
     * @param value2 value2
     * @param litteralType type
     * @return TQL Expression
     */
    private static Expression between(String fieldname, String value1, String value2, LiteralValue.Enum litteralType) {

        // Creating simple between expression
        LiteralValue left = new LiteralValue(litteralType, value1);
        LiteralValue right = new LiteralValue(litteralType, value2);
        FieldBetweenExpression fieldBetweenExpression = new FieldBetweenExpression(new FieldReference(fieldname), left, right,
                false, false);
        Expression[] fieldBetweenExpressions = new Expression[] { fieldBetweenExpression };

        // Adding it to a new AST
        AndExpression andExpression = new AndExpression(fieldBetweenExpressions);
        return new OrExpression(andExpression);

    }

}
