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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.talend.tql.model.LiteralValue;
import org.talend.tql.model.NotExpression;
import org.talend.tql.model.OrExpression;
import org.talend.tql.model.TqlElement;
import org.talend.tql.visitor.IASTVisitor;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;
import static java.util.Collections.singleton;
import static java.util.Optional.of;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.talend.tql.bean.MethodAccessorFactory.build;

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

    public BeanPredicateVisitor(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    private static Stream<Object> invoke(Object o, MethodAccessor[] methods) {
        try {
            Set<Object> currentObject = Collections.singleton(o);
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
     * @param value   A string value. May be null.
     * @param pattern A pattern as returned in value analysis.
     * @return <code>true</code> if value complies, <code>false</code> otherwise.
     */
    private static boolean complies(String value, String pattern) {
        if (value == null && pattern == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        // Character based patterns
        if (StringUtils.containsAny(pattern, new char[] { 'A', 'a', '9' })) {
            if (value.length() != pattern.length()) {
                return false;
            }
            final char[] valueArray = value.toCharArray();
            final char[] patternArray = pattern.toCharArray();
            for (int i = 0; i < valueArray.length; i++) {
                if (patternArray[i] == 'A') {
                    if (!Character.isUpperCase(valueArray[i])) {
                        return false;
                    }
                } else if (patternArray[i] == 'a') {
                    if (!Character.isLowerCase(valueArray[i])) {
                        return false;
                    }
                } else if (patternArray[i] == '9') {
                    if (!Character.isDigit(valueArray[i])) {
                        return false;
                    }
                } else {
                    if (valueArray[i] != patternArray[i]) {
                        return false;
                    }
                }
            }
        } else {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            try {
                formatter.toFormat().parseObject(value);
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    private static <T> Predicate<T> unchecked(Predicate<T> predicate) {
        return of(predicate).map(Unchecked::new).orElseGet(() -> new Unchecked<>(o -> false));
    }

    private static <T> Predicate<T> anyMatch(MethodAccessor[] getters, Predicate<T> predicate) {
        return root -> invoke(root, getters).map(o -> (T) o).anyMatch(unchecked(predicate));
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
        return getMethods(fieldReference.getPath());
    }

    private MethodAccessor[] getMethods(String field) {
        StringTokenizer tokenizer = new StringTokenizer(field, ".");
        List<String> methodNames = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            methodNames.add(tokenizer.nextToken());
        }

        Class currentClass = targetClass;
        LinkedList<MethodAccessor> methods = new LinkedList<>();
        for (String methodName : methodNames) {
            if ("_class".equals(methodName)) {
                try {
                    methods.add(build(Class.class.getMethod("getClass")));
                    methods.add(build(Class.class.getMethod("getName")));
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("Unable to get methods for class' name.", e);
                }
            } else {
                String[] getterCandidates = new String[] { "get" + WordUtils.capitalize(methodName), //
                        methodName, //
                        "is" + WordUtils.capitalize(methodName) };

                final int beforeFind = methods.size();
                for (String getterCandidate : getterCandidates) {
                    try {
                        methods.add(build(currentClass.getMethod(getterCandidate)));
                        break;
                    } catch (Exception e) {
                        LOGGER.debug("Can't find getter '{}'.", field, e);
                    }
                }

                // No method found, try using @JsonProperty
                if (beforeFind == methods.size()) {
                    LOGGER.debug("Unable to find method, try using @JsonProperty for '{}'.", methodName);
                    final Method[] currentClassMethods = currentClass.getMethods();
                    for (Method currentClassMethod : currentClassMethods) {
                        final JsonProperty jsonProperty = currentClassMethod.getAnnotation(JsonProperty.class);
                        if (jsonProperty != null && methodName.equals(jsonProperty.value())
                                && !void.class.equals(currentClassMethod.getReturnType())) {
                            LOGGER.debug("Found method '{}' using @JsonProperty.", currentClassMethod);
                            methods.add(build(currentClassMethod));
                        }
                    }
                }

                // Check before continue
                if (beforeFind == methods.size()) {
                    throw new UnsupportedOperationException("Can't find getter '" + field + "'.");
                } else {
                    currentClass = methods.getLast().getReturnType();
                }
            }
        }
        return methods.toArray(new MethodAccessor[0]);
    }

    @Override
    public Predicate<T> visit(Expression expression) {
        // Very generic method: prefer an unsupported exception iso. erratic behavior.
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<T> visit(AndExpression andExpression) {
        final Expression[] expressions = andExpression.getExpressions();
        if (expressions.length > 0) {
            Predicate<T> predicate = expressions[0].accept(this);
            for (int i = 1; i < expressions.length; i++) {
                predicate = predicate.and(expressions[i].accept(this));
            }
            return predicate;
        } else {
            return m -> true;
        }
    }

    @Override
    public Predicate<T> visit(OrExpression orExpression) {
        final Expression[] expressions = orExpression.getExpressions();
        if (expressions.length > 0) {
            Predicate<T> predicate = expressions[0].accept(this);
            for (int i = 1; i < expressions.length; i++) {
                predicate = predicate.or(expressions[i].accept(this));
            }
            return predicate;
        } else {
            return m -> true;
        }
    }

    @Override
    public Predicate<T> visit(ComparisonExpression comparisonExpression) {
        comparisonExpression.getValueOrField().accept(this);
        final Object value = literals.pop();

        comparisonExpression.getField().accept(this);
        if (!currentMethods.isEmpty()) {
            Predicate<T> predicate = getComparisonPredicate(currentMethods.pop(), comparisonExpression, value);
            while (!currentMethods.isEmpty()) {
                predicate = predicate.or(getComparisonPredicate(currentMethods.pop(), comparisonExpression, value));
            }
            return predicate;
        } else {
            return o -> true;
        }
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
        if (values.length > 0) {
            Predicate<T> predicate = eq(values[0].accept(this), methods);
            for (LiteralValue value : values) {
                value.accept(this);
                predicate = predicate.or(eq(literals.pop(), methods));
            }
            return predicate;
        } else {
            return m -> true;
        }
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

        return anyMatch(methods, o -> StringUtils.containsIgnoreCase(valueOf(o), fieldContainsExpression.getValue()));
    }

    @Override
    public Predicate<T> visit(AllFields allFields) {
        final Set<Class> initialClasses = new HashSet<>(singleton(targetClass));
        visitClassMethods(targetClass, initialClasses);

        return null;
    }

    private void visitClassMethods(Class targetClass, Set<Class> visitedClasses, MethodAccessor... previous) {
        List<MethodAccessor> previousMethods = Arrays.asList(previous);
        for (Method method : targetClass.getMethods()) {
            if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
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
