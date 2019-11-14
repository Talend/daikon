package org.talend.daikon.avro;

import com.cedarsoftware.util.io.JsonWriter;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;

import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class SchemaTest {

    @Test
    public void testSchemaSerialized() throws Throwable {
        Schema main = SchemaBuilder.record("Main").fields().name("C").type().stringType().noDefault().name("D").type()
                .stringType().noDefault().endRecord();

        Property schemaMain = PropertyFactory.newSchema("main");
        schemaMain.setValue(main);

        JsonWriter.objectToJson(schemaMain);
    }

}
