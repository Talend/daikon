package org.talend.daikon.serialize.jsonschema;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.*;
import static org.talend.daikon.properties.property.PropertyFactory.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.serialize.FullExampleProperties;

public class JsonBaseToolTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // these are the byte codes of a class called org.talend.daikon.serialize.jsonschema.AnotherClassloaderClass
    // package org.talend.daikon.serialize.jsonschema;
    // public class AnotherClassloaderClass {
    // }
    private int[] anotherClassBytes = { -54, -2, -70, -66, 0, 0, 0, 51, 0, 16, 7, 0, 2, 1, 0, 62, 111, 114, 103, 47, 116, 97, 108,
            101, 110, 100, 47, 100, 97, 105, 107, 111, 110, 47, 115, 101, 114, 105, 97, 108, 105, 122, 101, 47, 106, 115, 111,
            110, 115, 99, 104, 101, 109, 97, 47, 65, 110, 111, 116, 104, 101, 114, 67, 108, 97, 115, 115, 108, 111, 97, 100, 101,
            114, 67, 108, 97, 115, 115, 7, 0, 4, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116,
            1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 10, 0, 3, 0, 9, 12, 0, 5, 0, 6,
            1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97,
            114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 64, 76, 111, 114, 103, 47, 116,
            97, 108, 101, 110, 100, 47, 100, 97, 105, 107, 111, 110, 47, 115, 101, 114, 105, 97, 108, 105, 122, 101, 47, 106, 115,
            111, 110, 115, 99, 104, 101, 109, 97, 47, 65, 110, 111, 116, 104, 101, 114, 67, 108, 97, 115, 115, 108, 111, 97, 100,
            101, 114, 67, 108, 97, 115, 115, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 28, 65, 110, 111,
            116, 104, 101, 114, 67, 108, 97, 115, 115, 108, 111, 97, 100, 101, 114, 67, 108, 97, 115, 115, 46, 106, 97, 118, 97,
            0, 33, 0, 1, 0, 3, 0, 0, 0, 0, 0, 1, 0, 1, 0, 5, 0, 6, 0, 1, 0, 7, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 8,
            -79, 0, 0, 0, 2, 0, 10, 0, 0, 0, 6, 0, 1, 0, 0, 0, 4, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 12, 0, 13, 0, 0, 0, 1,
            0, 14, 0, 0, 0, 2, 0, 15 };

    // used to generate a text file representing the bytes of a given class file bytecode.
    public static void main(String args[]) throws Exception {
        int _offset = 0;
        int _read = 0;

        File fileName = new File(args[0]);
        InputStream fileInputStream = new FileInputStream(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(args[1]);
        PrintStream printStream = new PrintStream(fileOutputStream);
        StringBuffer bytesStringBuffer = new StringBuffer();

        byte[] byteArray = new byte[(int) fileName.length()];
        while (_offset < byteArray.length && (_read = fileInputStream.read(byteArray, _offset, byteArray.length - _offset)) >= 0)
            _offset += _read;

        fileInputStream.close();
        for (int index = 0; index < byteArray.length; index++)
            bytesStringBuffer.append(byteArray[index] + ",");

        printStream.print(bytesStringBuffer.length() == 0 ? "" : bytesStringBuffer.substring(0, bytesStringBuffer.length() - 1));
    }

    @Test
    public void findClass() throws ClassNotFoundException {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        assertEquals(String.class,
                JsonBaseTool.findClass(properties.getClass().getClassLoader(), properties.stringProp.getType()));
        assertEquals(Integer.class,
                JsonBaseTool.findClass(properties.getClass().getClassLoader(), properties.integerProp.getType()));
        assertEquals(Date.class, JsonBaseTool.findClass(properties.getClass().getClassLoader(), properties.dateProp.getType()));
        assertEquals(Schema.class, JsonBaseTool.findClass(properties.getClass().getClassLoader(), properties.schema.getType()));

        // test inner class, which will has "$" in the real class name but "." from the getType.
        assertEquals(FullExampleProperties.CommonProperties.ColEnum.class,
                JsonBaseTool.findClass(properties.getClass().getClassLoader(), properties.commonProp.colEnum.getType()));
        assertEquals(String.class,
                JsonBaseTool.findClass(properties.getClass().getClassLoader(), properties.commonProp.colString.getType()));

        // given
        ClassLoader anotherCL = new AnotherClassLoader();
        // make sure the class is loaded by the class loader and not the current CL.
        assertNotNull(anotherCL.loadClass("org.talend.daikon.serialize.jsonschema.AnotherClassloaderClass"));
        try {
            this.getClass().getClassLoader().loadClass("org.talend.daikon.serialize.jsonschema.AnotherClassloaderClass");
            fail("should have thrown an exception");
        } catch (ClassNotFoundException cnfe) {
            // expected
        }

        // when
        Class classfound = JsonBaseTool.findClass(anotherCL, "org.talend.daikon.serialize.jsonschema.AnotherClassloaderClass");

        // assert
        assertNotNull(classfound);
        // also make sure an exception is thrown when the class is not found
        thrown.expect(RuntimeException.class);
        thrown.expectCause(isA(ClassNotFoundException.class));
        JsonBaseTool.findClass(this.getClass().getClassLoader(),
                "org.talend.daikon.serialize.jsonschema.AnotherClassloaderClass");
    }

    @Test
    public void isListClass() throws Exception {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        assertTrue(JsonBaseTool.isListClass(properties.tableProp.colListString.getType()));
        assertFalse(JsonBaseTool.isListClass(properties.stringProp.getType()));
    }

    @Test
    public void isStringListClass() throws Exception {
        Property<List<String>> stringListProperty = newStringList("selectColumnIds");
        Property<String> stringProperty = newString("toto");
        assertTrue(JsonBaseTool.isListClass(stringListProperty.getType()));
        assertFalse(JsonBaseTool.isListClass(stringProperty.getType()));
    }

    @Test
    public void getListInnerClassName() throws Exception {
        FullExampleProperties properties = new FullExampleProperties("fullexample");
        assertEquals(String.class.getName(), JsonBaseTool.getListInnerClassName(properties.tableProp.colListString.getType()));
        assertEquals(FullExampleProperties.TableProperties.ColEnum.class,
                JsonBaseTool.findClass(properties.getClass().getClassLoader(),
                        JsonBaseTool.getListInnerClassName(properties.tableProp.colListEnum.getType())));
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

    private class AnotherClassLoader extends ClassLoader {

        public AnotherClassLoader() {
            byte[] rawBytes = new byte[anotherClassBytes.length];
            for (int index = 0; index < rawBytes.length; index++)
                rawBytes[index] = (byte) anotherClassBytes[index];
            defineClass("org.talend.daikon.serialize.jsonschema.AnotherClassloaderClass", rawBytes, 0, rawBytes.length);
        }
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
