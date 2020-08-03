package org.talend.tql.bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;
import org.talend.tql.model.Expression;
import org.talend.tql.parser.Tql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeanPredicateVisitorTest {

    private final Bean bean = new Bean();

    @Test
    public void equalsShouldNotMatchBeanWithInvalidType() {
        // given
        final Expression query = Tql.parse("int > 'obviously not an integer'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void betweenShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int between [0,10]");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void betweenShouldMatchBeanLowerOpen() {
        // given
        final Expression query = Tql.parse("int between ]0,10]");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void betweenShouldNotMatchBeanUpperOpen() {
        // given
        final Expression query = Tql.parse("int between [0,10[");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void betweenShouldNotMatchBeanBothOpen() {
        // given
        final Expression query = Tql.parse("int between ]0,10[");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void notShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("not(int > 0)");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void containsShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value contains 'alu'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void containsShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("value contains 'ALU'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void containsIgnoreCaseShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value containsIgnoreCase 'ALu'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void compliesShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value complies 'aaaaa'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void compliesShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("value complies '99999'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void matchesShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value ~ '\\w*'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void inShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int in [10, 20]");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void emptyShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("value is empty");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void classShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value._class = 'java.lang.String'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void andShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int > 0 and int < 11");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void orShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int > 0 or int > 1");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void gteShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int >= 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void gteShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("int >= 11");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void gtShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int > 0");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void gtShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("int > 20");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void lteShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int <= 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void lteShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("int <= 9");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void ltShouldMatchBean() {
        // given
        final Expression query = Tql.parse("int < 20");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void ltShouldNotMatchBean() {
        // given
        final Expression query = Tql.parse("int < 5");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void notEqualsShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value != 'not a value'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldMatchBean() {
        // given
        final Expression query = Tql.parse("value = 'value'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldMatchBeanOnList() {
        // given
        final Expression query = Tql.parse("nestedBeans.nestedValue = 'nested'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldMatchBeanOnNested() {
        // given
        final Expression query = Tql.parse("nested.nestedValue = 'nested'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsOnAllFields() {
        // given
        final Expression query = Tql.parse("* = 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void equalsShouldNotMatchBeanOnValue() {
        // given
        final Expression query = Tql.parse("value = 'non match'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotMatchBeanOnMissingField() {
        // given
        final Expression query = Tql.parse("wrongField = 'value'");

        // then
        query.accept(new BeanPredicateVisitor<>(Bean.class));
    }

    @Test
    public void shouldMatchOnJsonPropertyName() {
        // given
        final Expression query = Tql.parse("aDifferentName = 'myValue'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void testAccept_parseFieldCompliesPatternword() {
        // given
        final Expression query = Tql.parse("value wordComplies '[word]'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void testParseFieldNotCompliesPatternWord() {
        // given
        final Expression query = Tql.parse("value wordComplies '[Word]'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void testMyMapWithAValidUnaryKey() {
        // given
        final Expression query = Tql.parse("attributes.version = '1.0'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void testMyMapWithAValidUnaryKeyThatNotMatch() {
        // given
        final Expression query = Tql.parse("attributes.version = '2.0'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void testMyMapWithAValidIterableKey() {
        // given
        final Expression query = Tql.parse("attributes.tags = 'Released'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void testMyMapWithAInvalidValidKey() {
        // given
        final Expression query = Tql.parse("attributes.invalid = 'error'");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertFalse(predicate.test(bean));
    }

    @Test
    public void testDoubleIntComparison() {
        // given
        final Expression query = Tql.parse("int = 10.0");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    @Test
    public void testIntComparison() {
        // given
        final Expression query = Tql.parse("int = 10");

        // when
        final Predicate<Bean> predicate = query.accept(new BeanPredicateVisitor<>(Bean.class));

        // then
        assertTrue(predicate.test(bean));
    }

    // Test class
    public static class Bean {

        public List<NestedBean> getNestedBeans() {
            return Arrays.asList(new NestedBean(), new NestedBean());
        }

        public String getValue() {
            return "value";
        }

        public int getInt() {
            return 10;
        }

        public NestedBean getNested() {
            return new NestedBean();
        }

        @JsonProperty("aDifferentName")
        public String getMyValue() {
            return "myValue";
        }

        @JsonProperty("aDifferentName")
        public void setMyValue() {
            // No code needed, just to ensure setters are not detected.
        }

        public Map<String, Object> getAttributes() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("version", "1.0");
            attributes.put("tags", Arrays.asList("Complete", "Released"));
            return attributes;
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