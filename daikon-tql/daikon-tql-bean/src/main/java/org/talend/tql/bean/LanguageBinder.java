package org.talend.tql.bean;

/**
 * Allow to partially customize the behaviour of the {@link BeanPredicateVisitor}.
 * This can be relevant while using this visitor from other languages such as Scala.
 */
public interface LanguageBinder {

    /**
     * Compute the methods chain to be called to resolve the actual value of the given field. This value will be used
     * to evaluate the expressions involving the given field.
     *
     * @param field the field name (ex: 'entity.owner.firstname')
     * @return an array of {@link MethodAccessor} describing the methods chain allowing to resolve the expected value
     */
    MethodAccessor[] getMethods(String field);

    /**
     * Expressions are evaluated using values resolved from {@link LanguageBinder#getMethods(String)}. Those values
     * can be anything and need a {@link String} representation to be tested.
     *
     * @param value the value to be tested
     * @return a {@link String} representation of the given value, allowing the relevant expressions to be evaluated
     */
    String valueOf(Object value);
}
