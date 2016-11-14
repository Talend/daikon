package org.talend.daikon.serialize.jsonschema;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.daikon.avro.SchemaConstants;

public class JsonDataGeneratorTest {

    @Test
    public void genData() throws Exception {
        String jsonStr = JsonSchemaUtilTest.readJson("FullExampleJsonData.json");
        FullExampleProperties properties = createASetupFullExampleProperties();

        JsonDataGenerator generator = new JsonDataGenerator();
        assertEquals(jsonStr, generator.genData(properties).toString());
    }

    static public FullExampleProperties createASetupFullExampleProperties() throws ParseException {
        FullExampleProperties properties = (FullExampleProperties) new FullExampleProperties("fullexample").init();
        properties.stringProp.setValue("abc");
        properties.integerProp.setValue(1);
        properties.hideStringPropProp.setValue(false);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        properties.dateProp.setValue(df.parse("2016-10-05T01:23:45.000Z"));
        properties.schema.setValue(
                SchemaBuilder.builder().record("test").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields().endRecord());

        properties.commonProp.colEnum.setValue(FullExampleProperties.CommonProperties.ColEnum.FOO);
        properties.commonProp.colBoolean.setValue(true);
        properties.commonProp.colString.setValue("common_abc");

        properties.tableProp.colListBoolean.setValue(Arrays.asList(new Boolean[] { true, false, true }));
        properties.tableProp.colListEnum.setValue(Arrays
                .asList(new FullExampleProperties.TableProperties.ColEnum[] { FullExampleProperties.TableProperties.ColEnum.FOO,
                        FullExampleProperties.TableProperties.ColEnum.BAR, FullExampleProperties.TableProperties.ColEnum.FOO }));
        properties.tableProp.colListString.setValue(Arrays.asList(new String[] { "a", "b", "c" }));
        return properties;
    }

    static public ReferenceExampleProperties createASetupReferenceExampleProperties() throws ParseException {
        ReferenceExampleProperties properties = new ReferenceExampleProperties("refexample");
        properties.init();
        properties.parentProp.setValue("testParentValue");
        properties.reference.childProp.setValue("testChildValue");
        return properties;
    }

}
