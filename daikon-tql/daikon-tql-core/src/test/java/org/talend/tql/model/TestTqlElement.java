package org.talend.tql.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTqlElement {

    @Test
    public void testEqualsAndExpression() {
        AndExpression andExpression = new AndExpression(new OrExpression(), new OrExpression());

        assertNotNull(andExpression);
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

        assertNotNull(comparisonExpression);
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

        assertNotNull(literalValue);
        assertFalse(literalValue.equals(new LiteralValue(null, null)));
        assertFalse(literalValue.equals(new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "other")));
        assertFalse(literalValue.equals(new LiteralValue(LiteralValue.Enum.DECIMAL, "test")));
        assertTrue(literalValue.equals(new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test")));
    }

    @Test
    public void testEqualsComparisonOperator() {
        ComparisonOperator comparisonOperator = new ComparisonOperator(ComparisonOperator.Enum.EQ);

        assertNotNull(comparisonOperator);
        assertFalse(comparisonOperator.equals(new ComparisonOperator(null)));
        assertFalse(comparisonOperator.equals(new ComparisonOperator(ComparisonOperator.Enum.NEQ)));
        assertTrue(comparisonOperator.equals(new ComparisonOperator(ComparisonOperator.Enum.EQ)));
    }

    @Test
    public void testEqualsFieldBetweenExpression() {
        FieldBetweenExpression fieldBetweenExpression = new FieldBetweenExpression(new FieldReference("field"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"), true, true);

        assertNotNull(fieldBetweenExpression);
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

        assertNotNull(fieldReference);
        assertFalse(fieldReference.equals(new FieldReference(null)));
        assertFalse(fieldReference.equals(new FieldReference("other")));
        assertTrue(fieldReference.equals(new FieldReference("field")));
    }

    @Test
    public void testEqualsFieldCompliesPattern() {
        FieldCompliesPattern fieldCompliesPattern = new FieldCompliesPattern(new FieldReference("field"), "pattern");

        assertNotNull(fieldCompliesPattern);
        assertFalse(fieldCompliesPattern.equals(new FieldCompliesPattern(null, null)));
        assertFalse(fieldCompliesPattern.equals(new FieldCompliesPattern(new FieldReference("field"), "other")));
        assertFalse(fieldCompliesPattern.equals(new FieldCompliesPattern(new FieldReference("other"), "pattern")));
        assertTrue(fieldCompliesPattern.equals(new FieldCompliesPattern(new FieldReference("field"), "pattern")));
    }

    @Test
    public void testEqualsContainsExpression() {
        FieldContainsExpression fieldContainsExpression = new FieldContainsExpression(new FieldReference("field"), "value");

        assertNotNull(fieldContainsExpression);
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(null, null)));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "other")));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("other"), "value")));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "value", false)));
        assertTrue(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "value", true)));
        assertTrue(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "value")));
    }

    @Test
    public void testEqualsContainsIgnoreCaseExpression() {
        FieldContainsExpression fieldContainsExpression = new FieldContainsExpression(new FieldReference("field"), "value",
                false);

        assertNotNull(fieldContainsExpression);
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(null, null, false)));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "other", false)));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("other"), "value", false)));
        assertFalse(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "value", true)));
        assertTrue(fieldContainsExpression.equals(new FieldContainsExpression(new FieldReference("field"), "value", false)));
    }

    @Test
    public void testEqualsFieldInExpression() {
        FieldInExpression fieldInExpression = new FieldInExpression(new FieldReference("field"),
                new LiteralValue[] { new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test"),
                        new LiteralValue(LiteralValue.Enum.QUOTED_VALUE, "test") });

        assertNotNull(fieldInExpression);
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

        assertNotNull(fieldIsEmptyExpression);
        assertFalse(fieldIsEmptyExpression.equals(new FieldIsEmptyExpression(null)));
        assertFalse(fieldIsEmptyExpression.equals(new FieldIsEmptyExpression(new FieldReference("other"))));
        assertTrue(fieldIsEmptyExpression.equals(new FieldIsEmptyExpression(new FieldReference("field"))));
    }

    @Test
    public void testEqualsFieldIsValidExpression() {
        FieldIsValidExpression fieldIsValidExpression = new FieldIsValidExpression(new FieldReference("field"));

        assertNotNull(fieldIsValidExpression);
        assertFalse(fieldIsValidExpression.equals(new FieldIsValidExpression(null)));
        assertFalse(fieldIsValidExpression.equals(new FieldIsValidExpression(new FieldReference("other"))));
        assertTrue(fieldIsValidExpression.equals(new FieldIsValidExpression(new FieldReference("field"))));
    }

    @Test
    public void testEqualsFieldIsInvalidExpression() {
        FieldIsInvalidExpression fieldIsInvalidExpression = new FieldIsInvalidExpression(new FieldReference("field"));

        assertNotNull(fieldIsInvalidExpression);
        assertFalse(fieldIsInvalidExpression.equals(new FieldIsInvalidExpression(null)));
        assertFalse(fieldIsInvalidExpression.equals(new FieldIsInvalidExpression(new FieldReference("other"))));
        assertTrue(fieldIsInvalidExpression.equals(new FieldIsInvalidExpression(new FieldReference("field"))));
    }

    @Test
    public void testEqualsFieldMatchesRegex() {
        FieldMatchesRegex fieldMatchesRegex = new FieldMatchesRegex(new FieldReference("field"), "regex");

        assertNotNull(fieldMatchesRegex);
        assertFalse(fieldMatchesRegex.equals(new FieldMatchesRegex(null, null)));
        assertFalse(fieldMatchesRegex.equals(new FieldMatchesRegex(new FieldReference("other"), "regex")));
        assertFalse(fieldMatchesRegex.equals(new FieldMatchesRegex(new FieldReference("field"), "other")));
        assertTrue(fieldMatchesRegex.equals(new FieldMatchesRegex(new FieldReference("field"), "regex")));
    }

    @Test
    public void testEqualsNotExpression() {
        NotExpression notExpression = new NotExpression(new OrExpression());

        assertNotNull(notExpression);
        assertFalse(notExpression.equals(new NotExpression(null)));
        assertFalse(notExpression.equals(new NotExpression(new AndExpression())));
        assertTrue(notExpression.equals(new NotExpression(new OrExpression())));
    }

    @Test
    public void testEqualsOrExpression() {
        OrExpression orExpression = new OrExpression(new OrExpression());

        assertNotNull(orExpression);
        assertFalse(orExpression.equals(new OrExpression(null)));
        assertFalse(orExpression.equals(new OrExpression(new AndExpression())));
        assertTrue(orExpression.equals(new OrExpression(new OrExpression())));
    }
}