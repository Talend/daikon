package org.talend.daikon.serialize.jsonschema;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.talend.daikon.properties.property.PropertyFactory.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.property.Property;

public class JsonBaseToolTest {

    @Test
    public void findClass() throws Exception {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        assertEquals(String.class, JsonBaseTool.findClass(properties.stringProp.getType()));
        assertEquals(Integer.class, JsonBaseTool.findClass(properties.integerProp.getType()));
        assertEquals(Date.class, JsonBaseTool.findClass(properties.dateProp.getType()));
        assertEquals(Schema.class, JsonBaseTool.findClass(properties.schema.getType()));

        // test inner class, which will has "$" in the real class name but "." from the getType.
        assertEquals(FullExampleProperties.CommonProperties.ColEnum.class,
                JsonBaseTool.findClass(properties.commonProp.colEnum.getType()));
        assertEquals(String.class, JsonBaseTool.findClass(properties.commonProp.colString.getType()));
    }

    @Test
    public void isListClass() throws Exception {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        assertTrue(JsonBaseTool.isListClass(properties.tableProp.colListString.getType()));
        assertFalse(JsonBaseTool.isListClass(properties.stringProp.getType()));
    }

    @Test
    public void getListInnerClassName() throws Exception {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        assertEquals(String.class.getName(), JsonBaseTool.getListInnerClassName(properties.tableProp.colListString.getType()));
        assertEquals(FullExampleProperties.TableProperties.ColEnum.class,
                JsonBaseTool.findClass(JsonBaseTool.getListInnerClassName(properties.tableProp.colListEnum.getType())));
    }

    @Test
    public void getSubProperty() throws Exception {
        ChildClass current = new ChildClass("current");
        List<Property> subPropertyList = JsonBaseTool.getSubProperty(current);
        List<String> subPropertyNames = new ArrayList<>();
        for (Property property : subPropertyList) {
            subPropertyNames.add(property.getName());
        }
        assertEquals(2, subPropertyList.size());
        assertThat(subPropertyNames, containsInAnyOrder("pString", "cString"));
    }

    @Test
    public void getSubProperties() throws Exception {
        ChildClass current = new ChildClass("current");
        List<Properties> subProperties = JsonBaseTool.getSubProperties(current);
        assertEquals(2, subProperties.size());
        List<String> subPropertiesName = new ArrayList<>();
        for (Properties subProperty : subProperties) {
            subPropertiesName.add(subProperty.getName());
        }
        assertThat(subPropertiesName, containsInAnyOrder("pCommon", "cCommon"));
    }

    public class ParentClass extends PropertiesImpl {

        public Property<String> pString = newString("pString");

        public PCommonClass pCommon = new PCommonClass("pCommon");

        public transient PCommonClass pCommonTransient = new PCommonClass("pCommon");

        public ParentClass(String name) {
            super(name);
        }
    }

    public class PCommonClass extends PropertiesImpl {

        public Property<Integer> pcInteger = newInteger("pcInteger");

        public PCommonClass(String name) {
            super(name);
        }
    }

    public class ChildClass extends ParentClass {

        public Property<String> cString = newString("cString");

        public CCommonClass cCommon = new CCommonClass("cCommon");

        public transient Property<Integer> pcIntegerTransient = newInteger("pcInteger");

        public ChildClass(String name) {
            super(name);
        }
    }

    public class CCommonClass extends PropertiesImpl {

        public Property<Integer> ccInteger = newInteger("ccInteger");

        public CCommonClass(String name) {
            super(name);
        }
    }

}
