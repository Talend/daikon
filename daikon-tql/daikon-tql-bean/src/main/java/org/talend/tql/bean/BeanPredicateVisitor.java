// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.tql.bean;

import static java.lang.Double.parseDouble;
import static java.util.Collections.singleton;
import static java.util.Optional.of;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.talend.tql.bean.MethodAccessorFactory.build;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.pattern.character.CharPatternToRegex;
import org.talend.daikon.pattern.word.WordPatternToRegex;
import org.talend.tql.model.AllFields;
import org.talend.tql.model.AndExpression;
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
import org.talend.tql.model.TqlElement;
import org.talend.tql.visitor.IASTVisitor;

/**
 * A {@link IASTVisitor} implementation that generates a {@link Predicate predicate} that allows matching on a
 * <code>T</code> instance.
 *
 * @param <T> The bean class.
 */
public class BeanPredicateVisitor<T> implements IASTVisitor<Predicate<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanPredicateVisitor.class);

    private final Class<T> targetClass;

    private final Deque<String> literals = new ArrayDeque<>();

    private final Deque<MethodAccessor[]> currentMethods = new ArrayDeque<>();

    private final LanguageBinder languageBinder;

    public BeanPredicateVisitor(Class<T> targetClass) {
        this(targetClass, new DefaultLanguageBinder(targetClass));
    }

    public BeanPredicateVisitor(Class<T> targetClass, LanguageBinder languageBinder) {
        this.targetClass = targetClass;
        this.languageBinder = languageBinder;
    }

    private static Stream<Object> invoke(Object o, MethodAccessor[] methods) {
        try {
            Set<Object> currentObject = singleton(o);
            for (MethodAccessor method : methods) {
                currentObject = method.getValues(currentObject);
            }
            return currentObject.stream();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to invoke methods on '" + o + "'.", e);
        }
    }

    /**
     * Test a string value against a pattern returned during value analysis.
     *
     * @param value A string value. May be null.
     * @param pattern A pattern as returned in value analysis.
     * @return <code>true</code> if value complies, <code>false</code> otherwise.
     */
    private static boolean complies(String value, String pattern) {
        return value != null && pattern != null && value.matches(CharPatternToRegex.toRegex(pattern));
    }

    private static boolean wordComplies(String value, String pattern) {
        return value != null && pattern != null && value.matches(WordPatternToRegex.toRegex(pattern, true));
    }

    private static <T> Predicate<T> unchecked(Predicate<T> predicate) {
        return of(predicate).map(Unchecked::new).orElseGet(() -> new Unchecked<>(o -> false));
    }

    private static <T> Predicate<T> anyMatch(MethodAccessor[] getters, Predicate<T> predicate) {
        return root -> invoke(root, getters).map(o -> (T) o).anyMatch(unchecked(predicate));
    }

    // Utility method to drain all queue and return a stream to iterate over drained items.
    private static <T> Stream<T> drainAll(final Queue<T> queue) {
        final List<T> dest = new ArrayList<>(queue);
        queue.clear();
        return dest.stream();
    }

    @Override
    public Predicate<T> visit(TqlElement tqlElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<T> visit(ComparisonOperator comparisonOperator) {
        // No need to implement this (handled in ComparisonExpression).
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<T> visit(LiteralValue literalValue) {
        literals.push(literalValue.getValue());
        return null;
    }

    @Override
    public Predicate<T> visit(FieldReference fieldReference) {
        currentMethods.push(getMethods(fieldReference));
        return null;
    }

    private MethodAccessor[] getMethods(FieldReference fieldReference) {
        return languageBinder.getMethods(fieldReference.getPath());
    }

    @Override
    public Predicate<T> visit(Expression expression) {
        // Very generic method: prefer an unsupported exception iso. erratic behavior.
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<T> visit(AndExpression andExpression) {
        final Expression[] expressions = andExpression.getExpressions();
        return Stream.of(expressions) //
                .map(e -> e.accept(this)) //
                .reduce(Predicate::and) //
                .orElseGet(() -> m -> true);
    }

    @Override
    public Predicate<T> visit(OrExpression orExpression) {
        final Expression[] expressions = orExpression.getExpressions();
        return Stream.of(expressions) //
                .map(e -> e.accept(this)) //
                .reduce(Predicate::or) //
                .orElseGet(() -> m -> true);
    }

    @Override
    public Predicate<T> visit(ComparisonExpression comparisonExpression) {
        comparisonExpression.getValueOrField().accept(this);
        final Object value = literals.pop();
        comparisonExpression.getField().accept(this);

        return drainAll(currentMethods) //
                .map(m -> getComparisonPredicate(m, comparisonExpression, value)) //
                .reduce(Predicate::or) //
                .orElseGet(() -> o -> true);
    }

    private Predicate<T> getComparisonPredicate(MethodAccessor[] getters, ComparisonExpression comparisonExpression,
            Object value) {
        // Standard methods
        final ComparisonOperator operator = comparisonExpression.getOperator();
        switch (operator.getOperator()) {
        case EQ:
            return eq(value, getters);
        case LT:
            return lt(value, getters);
        case GT:
            return gt(value, getters);
        case NEQ:
            return neq(value, getters);
        case LET:
            return lte(value, getters);
        case GET:
            return gte(value, getters);
        default:
            throw new UnsupportedOperationException();
        }
    }

    private Predicate<T> neq(Object value, MethodAccessor[] accessors) {
        return anyMatch(accessors, o -> !ObjectUtils.equals(o, value));
    }

    private Predicate<T> gt(Object value, MethodAccessor[] accessors) {
        return anyMatch(accessors, o -> parseDouble(valueOf(o)) > parseDouble(valueOf(value)));
    }

    private Predicate<T> gte(Object value, MethodAccessor[] accessors) {
        return anyMatch(accessors, o -> parseDouble(valueOf(o)) >= parseDouble(valueOf(value)));
    }

    private Predicate<T> lt(Object value, MethodAccessor[] accessors) {
        return anyMatch(accessors, o -> parseDouble(valueOf(o)) < parseDouble(valueOf(value)));
    }

    private Predicate<T> lte(Object value, MethodAccessor[] accessors) {
        return anyMatch(accessors, o -> parseDouble(valueOf(o)) <= parseDouble(valueOf(value)));
    }

    private Predicate<T> eq(Object value, MethodAccessor[] accessors) {
        return anyMatch(accessors, o -> equalsIgnoreCase(valueOf(o), valueOf(value)));
    }

    @Override
    public Predicate<T> visit(FieldInExpression fieldInExpression) {
        fieldInExpression.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();

        final LiteralValue[] values = fieldInExpression.getValues();
        return Stream.of(values) //
                .map(v -> {
                    v.accept(this);
                    return eq(literals.pop(), methods);
                }) //
                .reduce(Predicate::or) //
                .orElseGet(() -> m -> true);
    }

    @Override
    public Predicate<T> visit(FieldIsEmptyExpression fieldIsEmptyExpression) {
        fieldIsEmptyExpression.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();
        return unchecked(o -> StringUtils.isEmpty(valueOf(invoke(o, methods))));
    }

    @Override
    public Predicate<T> visit(FieldIsValidExpression fieldIsValidExpression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<T> visit(FieldIsInvalidExpression fieldIsInvalidExpression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<T> visit(FieldMatchesRegex fieldMatchesRegex) {
        fieldMatchesRegex.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();

        final Pattern pattern = Pattern.compile(fieldMatchesRegex.getRegex());
        return anyMatch(methods, o -> pattern.matcher(valueOf(o)).matches());
    }

    @Override
    public Predicate<T> visit(FieldCompliesPattern fieldCompliesPattern) {
        fieldCompliesPattern.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();

        final String pattern = fieldCompliesPattern.getPattern();
        return anyMatch(methods, o -> complies(valueOf(o), pattern));
    }

    @Override
    public Predicate<T> visit(FieldWordCompliesPattern fieldWordCompliesPattern) {
        fieldWordCompliesPattern.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();

        final String pattern = fieldWordCompliesPattern.getPattern();
        return anyMatch(methods, o -> wordComplies(valueOf(o), pattern));
    }

    @Override
    public Predicate<T> visit(FieldBetweenExpression fieldBetweenExpression) {
        fieldBetweenExpression.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();

        fieldBetweenExpression.getLeft().accept(this);
        fieldBetweenExpression.getRight().accept(this);
        final String right = literals.pop();
        final String left = literals.pop();

        Predicate<T> predicate;
        if (fieldBetweenExpression.isLowerOpen()) {
            predicate = gt(left, methods);
        } else {
            predicate = gte(left, methods);
        }
        if (fieldBetweenExpression.isUpperOpen()) {
            predicate = predicate.and(lt(right, methods));
        } else {
            predicate = predicate.and(lte(right, methods));
        }
        return predicate;
    }

    @Override
    public Predicate<T> visit(NotExpression notExpression) {
        final Predicate<T> accept = notExpression.getExpression().accept(this);
        return accept.negate();
    }

    @Override
    public Predicate<T> visit(FieldContainsExpression fieldContainsExpression) {
        fieldContainsExpression.getField().accept(this);
        final MethodAccessor[] methods = currentMethods.pop();
        return anyMatch(methods, o -> {
            String invokeResultString = valueOf(o);
            String expressionValue = fieldContainsExpression.getValue();
            return fieldContainsExpression.isCaseSensitive() ? StringUtils.contains(invokeResultString, expressionValue)
                    : StringUtils.containsIgnoreCase(invokeResultString, expressionValue);
        });
    }

    @Override
    public Predicate<T> visit(AllFields allFields) {
        final Set<Class> initialClasses = new HashSet<>(singleton(targetClass));
        visitClassMethods(targetClass, initialClasses);

        return null;
    }

    private String valueOf(Object value) {
        return languageBinder.valueOf(value);
    }

    private void visitClassMethods(Class targetClass, Set<Class> visitedClasses, MethodAccessor... previous) {
        List<MethodAccessor> previousMethods = Arrays.asList(previous);
        for (Method method : targetClass.getMethods()) {
            if (Map.class.isAssignableFrom(targetClass)
                    && (method.getName().startsWith("get") || method.getName().startsWith("is"))) {
                final MethodAccessor methodAccessor = build(method);
                final MethodAccessor[] path = concat(previousMethods.stream(), Stream.of(methodAccessor))
                        .toArray(MethodAccessor[]::new);
                currentMethods.push(path);

                // Recursively get methods to nested classes (and prevent infinite recursions).
                final Class<?> returnType = methodAccessor.getReturnType();
                if (!returnType.isPrimitive() && visitedClasses.add(returnType)) {
                    visitClassMethods(returnType, visitedClasses, path);
                }
            }
        }
    }

    private static class Unchecked<T> implements Predicate<T> {

        private Predicate<T> delegate;

        private Unchecked(Predicate<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean test(T t) {
            try {
                return delegate.test(t);
            } catch (Exception e) {
                LOGGER.error("Unable to evaluate.", e);
                return false;
            }
        }

        @Override
        public Predicate<T> and(Predicate<? super T> other) {
            return new Unchecked<>(delegate.and(other));
        }

        @Override
        public Predicate<T> negate() {
            return new Unchecked<>(delegate.negate());
        }

        @Override
        public Predicate<T> or(Predicate<? super T> other) {
            return new Unchecked<>(delegate.or(other));
        }
    }
}
