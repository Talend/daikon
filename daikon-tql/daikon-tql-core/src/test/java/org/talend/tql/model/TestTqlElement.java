package org.talend.tql.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTqlElement {

    @Test
    public void testEqualsAndExpression() {
        AndExpression andExpression = new AndExpression(new OrExpression(), new OrExpression());

        assertFalse(andExpression.equals(null));
        assertFalse(andExpression.equals(new AndExpression()));
        assertFalse(andExpression.equals(new AndExpression(new OrExpression())));
        assertFalse(andExpression.equals(new AndExpression(new OrExpression(), new AndExpression())));
        assertTrue(andExpression.equals(new AndExpression(new OrExpression(), new OrExpression())));
    }

    @Test
    public void testEqualsComparisonExpression() {
        ComparisonExpression comparisonExpression = new ComparisonExpression(new ComparisonOperator(ComparisonOperator.Enum.EQ),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"));

        assertFalse(comparisonExpression.equals(null));
        assertFalse(comparisonExpression.equals(new ComparisonExpression(null, null, null)));
        assertFalse(comparisonExpression.equals(new ComparisonExpression(new ComparisonOperator(ComparisonOperator.Enum.NEQ),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"))));
        assertFalse(comparisonExpression.equals(new ComparisonExpression(new ComparisonOperator(ComparisonOperator.Enum.EQ),
                new LiteralValue(LiteralValue.Enum.DECIMAL, "test"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"))));
        assertFalse(comparisonExpression.equals(new ComparisonExpression(new ComparisonOperator(ComparisonOperator.Enum.EQ),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), new LiteralValue(LiteralValue.Enum.DECIMAL, "test"))));
        assertTrue(comparisonExpression.equals(new ComparisonExpression(new ComparisonOperator(ComparisonOperator.Enum.EQ),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"))));
    }

    @Test
    public void testEqualsLiteralValue() {
        LiteralValue literalValue = new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test");

        assertFalse(literalValue.equals(null));
        assertFalse(literalValue.equals(new LiteralValue(null, null)));
        assertFalse(literalValue.equals(new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "other")));
        assertFalse(literalValue.equals(new LiteralValue(LiteralValue.Enum.DECIMAL, "test")));
        assertTrue(literalValue.equals(new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test")));
    }

    @Test
    public void testEqualsComparisonOperator() {
        ComparisonOperator comparisonOperator = new ComparisonOperator(ComparisonOperator.Enum.EQ);

        assertFalse(comparisonOperator.equals(null));
        assertFalse(comparisonOperator.equals(new ComparisonOperator(null)));
        assertFalse(comparisonOperator.equals(new ComparisonOperator(ComparisonOperator.Enum.NEQ)));
        assertTrue(comparisonOperator.equals(new ComparisonOperator(ComparisonOperator.Enum.EQ)));
    }

    @Test
    public void testEqualsFieldBetweenExpression() {
        FieldBetweenExpression fieldBetweenExpression = new FieldBetweenExpression(new FieldReference("field"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), true, true);

        assertFalse(fieldBetweenExpression.equals(null));
        assertFalse(fieldBetweenExpression.equals(new FieldBetweenExpression(null, null, null, false, false)));
        assertFalse(fieldBetweenExpression.equals(
                new FieldBetweenExpression(new FieldReference("field"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), true, false)));
        assertFalse(fieldBetweenExpression.equals(
                new FieldBetweenExpression(new FieldReference("field"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), false, true)));
        assertFalse(fieldBetweenExpression.equals(
                new FieldBetweenExpression(new FieldReference("field"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "other"), true, true)));
        assertFalse(fieldBetweenExpression.equals(
                new FieldBetweenExpression(new FieldReference("field"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "other"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), true, true)));
        assertFalse(fieldBetweenExpression.equals(
                new FieldBetweenExpression(new FieldReference("other"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), true, true)));
        assertTrue(fieldBetweenExpression.equals(
                new FieldBetweenExpression(new FieldReference("field"), new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), true, true)));
    }

    @Test
    public void testEqualsFieldReference() {
        FieldReference fieldReference = new FieldReference("field");

        assertFalse(fieldReference.equals(null));
        assertFalse(fieldReference.equals(new FieldReference(null)));
        assertFalse(fieldReference.equals(new FieldReference("other")));
        assertTrue(fieldReference.equals(new FieldReference("field")));
    }

    @Test
    public void testEqualsFieldCompliesPattern() {
        FieldCompliesPattern fieldCompliesPattern = new FieldCompliesPattern(new FieldReference("field"), "pattern");

        assertFalse(fieldCompliesPattern.equals(null));
        assertFalse(fieldCompliesPattern.equals(new FieldCompliesPattern(null, null)));
        assertFalse(fieldCompliesPattern.equals(new FieldCompliesPattern(new FieldReference("field"), "other")));
        assertFalse(fieldCompliesPattern.equals(new FieldCompliesPattern(new FieldReference("other"), "pattern")));
        assertTrue(fieldCompliesPattern.equals(new FieldCompliesPattern(new FieldReference("field"), "pattern")));
    }

    @Test
    public void testEqualsContainsExpression() {
        FieldContainsExpression fieldContainsExpression = new FieldContainsExpression(new FieldReference("field"), "value");

        assertFalse(fieldContainsExpression.equals(null));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(null, null)));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "other")));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("other"), "value")));
        assertTrue(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "value")));
    }

    @Test
    public void testEqualsFieldInExpression() {
        FieldInExpression fieldInExpression = new FieldInExpression(new FieldReference("field"),
                new LiteralValue[] { new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test") });

        assertFalse(fieldInExpression.equals(null));
        assertFalse(fieldInExpression.equals(new FieldInExpression(null, null)));
        assertFalse(fieldInExpression.equals(new FieldInExpression(new FieldReference("test"),
                new LiteralValue[] { new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test") })));
        assertFalse(fieldInExpression.equals(new FieldInExpression(new FieldReference("test"),
                new LiteralValue[] { new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "other") })));
        assertFalse(fieldInExpression.equals(new FieldInExpression(new FieldReference("other"),
                new LiteralValue[] { new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test") })));
        assertTrue(fieldInExpression.equals(new FieldInExpression(new FieldReference("field"),
                new LiteralValue[] { new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test") })));
    }

    @Test
    public void testEqualsFieldIsEmptyExpression() {
        FieldIsEmptyExpression fieldIsEmptyExpression = new FieldIsEmptyExpression(new FieldReference("field"));

        assertFalse(fieldIsEmptyExpression.equals(null));
        assertFalse(fieldIsEmptyExpression.equals(new FieldIsEmptyExpression(null)));
        assertFalse(fieldIsEmptyExpression.equals(new FieldIsEmptyExpression(new FieldReference("other"))));
        assertTrue(fieldIsEmptyExpression.equals(new FieldIsEmptyExpression(new FieldReference("field"))));
    }

    @Test
    public void testEqualsFieldIsValidExpression() {
        FieldIsValidExpression fieldIsValidExpression = new FieldIsValidExpression(new FieldReference("field"));

        assertFalse(fieldIsValidExpression.equals(null));
        assertFalse(fieldIsValidExpression.equals(new FieldIsValidExpression(null)));
        assertFalse(fieldIsValidExpression.equals(new FieldIsValidExpression(new FieldReference("other"))));
        assertTrue(fieldIsValidExpression.equals(new FieldIsValidExpression(new FieldReference("field"))));
    }

    @Test
    public void testEqualsFieldIsInvalidExpression() {
        FieldIsInvalidExpression fieldIsInvalidExpression = new FieldIsInvalidExpression(new FieldReference("field"));

        assertFalse(fieldIsInvalidExpression.equals(null));
        assertFalse(fieldIsInvalidExpression.equals(new FieldIsInvalidExpression(null)));
        assertFalse(fieldIsInvalidExpression.equals(new FieldIsInvalidExpression(new FieldReference("other"))));
        assertTrue(fieldIsInvalidExpression.equals(new FieldIsInvalidExpression(new FieldReference("field"))));
    }

    @Test
    public void testEqualsFieldMatchesRegex() {
        FieldMatchesRegex fieldMatchesRegex = new FieldMatchesRegex(new FieldReference("field"), "regex");

        assertFalse(fieldMatchesRegex.equals(null));
        assertFalse(fieldMatchesRegex.equals(new FieldMatchesRegex(null, null)));
        assertFalse(fieldMatchesRegex.equals(new FieldMatchesRegex(new FieldReference("other"), "regex")));
        assertFalse(fieldMatchesRegex.equals(new FieldMatchesRegex(new FieldReference("field"), "other")));
        assertTrue(fieldMatchesRegex.equals(new FieldMatchesRegex(new FieldReference("field"), "regex")));
    }

    @Test
    public void testEqualsNotExpression() {
        NotExpression notExpression = new NotExpression(new OrExpression());

        assertFalse(notExpression.equals(null));
        assertFalse(notExpression.equals(new NotExpression(null)));
        assertFalse(notExpression.equals(new NotExpression(new AndExpression())));
        assertTrue(notExpression.equals(new NotExpression(new OrExpression())));
    }

    @Test
    public void testEqualsOrExpression() {
        OrExpression orExpression = new OrExpression(new OrExpression());

        assertFalse(orExpression.equals(null));
        assertFalse(orExpression.equals(new OrExpression(null)));
        assertFalse(orExpression.equals(new OrExpression(new AndExpression())));
        assertTrue(orExpression.equals(new OrExpression(new OrExpression())));
    }
}