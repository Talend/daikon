package org.talend.tql.bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;

import org.junit.Test;
import org.talend.tql.model.Expression;
import org.talend.tql.parser.Tql;

public class BeanPredicateVisitorTest {

    private final Bean bean = new Bean();

    @Test
    public void equalsShouldNotMatchBeanWithInvalidType() throws Exception {
        // given
        final Expression query = Tql.parse("int > 'obviously not an integer'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void betweenShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int between [0,11]");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void notShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("not(int > 0)");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void containsShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value contains 'ALU'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void compliesShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value complies 'aaaaa'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void compliesShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value complies '99999'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void matchesShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value ~ '\\w*'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void inShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int in [10, 20]");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void emptyShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value is empty");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void classShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value._class = 'java.lang.String'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void andShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int > 0 and int < 11");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void orShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int > 0 or int > 1");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void gteShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int >= 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void gteShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int >= 11");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void gtShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int > 0");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void gtShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int > 20");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void lteShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int <= 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void lteShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int <= 9");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void ltShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int < 20");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void ltShouldNotMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("int < 5");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void notEqualsShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value != 'not a value'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldMatchBean() throws Exception {
        // given
        final Expression query = Tql.parse("value = 'value'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldMatchBeanOnNested() throws Exception {
        // given
        final Expression query = Tql.parse("nested.nestedValue = 'nested'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsOnAllFields() throws Exception {
        // given
        final Expression query = Tql.parse("* = 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldNotMatchBeanOnValue() throws Exception {
        // given
        final Expression query = Tql.parse("value = 'non match'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotMatchBeanOnMissingField() throws Exception {
        // given
        final Expression query = Tql.parse("wrongField = 'value'");

        // then
        query.accept(new BeanPredicateVisitor<>(Bean.class));
    }

    // Test class
    public static class Bean {

        public String getValue() {
            return "value";
        }

        public int getInt() {
            return 10;
        }

        public NestedBean getNested() {
            return new NestedBean();
        }
    }

    // Test class
    public static class NestedBean {

        public int getNestedInt() {
            return 10;
        }

        public String getNestedValue() {
            return "nested";
        }
    }
}