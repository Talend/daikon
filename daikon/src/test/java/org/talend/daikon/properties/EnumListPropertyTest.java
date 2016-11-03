package org.talend.daikon.properties;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.junit.Test;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.property.EnumListProperty;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.PropertyValueEvaluator;

public class EnumListPropertyTest {

    public static final TypeLiteral<List<TestEnum>> LIST_ENUM_TYPE = new TypeLiteral<List<TestEnum>>() {// empty
    };

    @Test
    public void testGetValue() {
        EnumListProperty<TestEnum> prop = PropertyFactory.newEnumList("prop", LIST_ENUM_TYPE);
        assertThat((List<TestEnum>) prop.getPossibleValues(), contains(TestEnum.FOO, TestEnum.BAR, TestEnum.FOOBAR));
        // test the Stored value as a String
        prop.setStoredValue(Arrays.asList("FOO", "FOO", "BAR", "FOOBAR", "BAR"));
        assertEquals(Arrays.asList(TestEnum.FOO, TestEnum.FOO, TestEnum.BAR, TestEnum.FOOBAR, TestEnum.BAR), prop.getValue());
    }

    @Test
    public void testGetValueWithEvaluator() {
        EnumListProperty<TestEnum> prop = PropertyFactory.newEnumList("prop", LIST_ENUM_TYPE);
        prop.setValueEvaluator(new PropertyValueEvaluator() {

            @Override
            public <T> T evaluate(Property<T> property, Object storedValue) {
                List convertedValues = new ArrayList();
                List values = (List) storedValue;
                for (Object value : values) {
                    convertedValues.add(TestEnum.valueOf((String) value));
                }
                return (T) convertedValues;
            }
        });
        prop.setStoredValue(Arrays.asList("FOO", "BAR", "FOOBAR", "FOO", "BAR"));
        assertEquals(Arrays.asList(TestEnum.FOO, TestEnum.BAR, TestEnum.FOOBAR, TestEnum.FOO, TestEnum.BAR), prop.getValue());
    }

    @Test
    public void testI18nForEnums() {
        EnumListProperty<TestEnum> prop = new EnumListPropForI18NTest("prop");
        assertEquals("fo o", prop.getPossibleValuesDisplayName(TestEnum.FOO));
        assertEquals("ba r", prop.getPossibleValuesDisplayName(TestEnum.BAR));
        assertEquals("fooba r", prop.getPossibleValuesDisplayName(TestEnum.FOOBAR));
        try {
            prop.getPossibleValuesDisplayName("I am your father");
            fail("exception should have been thrown.");
        } catch (TalendRuntimeException e) {
            assertEquals(CommonErrorCodes.UNEXPECTED_ARGUMENT, e.getCode());
        }
    }

    public enum TestEnum {
        FOO,
        BAR,
        FOOBAR
    }

    private final class EnumListPropForI18NTest extends EnumListProperty {

        private EnumListPropForI18NTest(String name) {
            super(LIST_ENUM_TYPE, name);
        }

    }
}
