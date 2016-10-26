package org.talend.daikon.serialize.jsonschema;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.daikon.avro.SchemaConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonPropertiesResolverTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void resolveJson() throws Exception {
        String jsonDataStr = JsonSchemaUtilTest.readJson("FullExampleJsonData.json");
        JsonPropertiesResolver resolver = new JsonPropertiesResolver();
        FullExampleProperties properties = (FullExampleProperties) resolver.resolveJson((ObjectNode) mapper.readTree(jsonDataStr),
                new FullExampleProperties("").init());

        assertEquals("abc", properties.stringProp.getValue());
        assertThat(1, is(equalTo(properties.integerProp.getValue())));
        assertFalse(properties.hideStringPropProp.getValue());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals("2016-10-05T01:23:45.000Z", df.format(properties.dateProp.getValue()));

        assertEquals(SchemaBuilder.builder().record("test").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields().endRecord()
                .toString(), properties.schema.getValue().toString());

        assertEquals(FullExampleProperties.CommonProperties.ColEnum.FOO, properties.commonProp.colEnum.getValue());
        assertTrue(properties.commonProp.colBoolean.getValue());
        assertEquals("common_abc", properties.commonProp.colString.getValue());

        assertEquals(Arrays.asList(new Boolean[] { true, false, true }), properties.tableProp.colListBoolean.getValue());
        assertEquals(
                Arrays.asList(new FullExampleProperties.TableProperties.ColEnum[] {
                        FullExampleProperties.TableProperties.ColEnum.FOO, FullExampleProperties.TableProperties.ColEnum.BAR,
                        FullExampleProperties.TableProperties.ColEnum.FOO }),
                properties.tableProp.colListEnum.getValue());
        assertEquals(Arrays.asList(new String[] { "a", "b", "c" }), properties.tableProp.colListString.getValue());
    }

}