package org.talend.daikon.avro.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.daikon.avro.AvroConverter;

/**
 * Unit tests for {ConvertAvroMap}.
 */
@SuppressWarnings("nls")
public class ConvertAvroMapTest {

    /**
     * Tests the basic usage of {ConvertAvroMap}.
     */
    @Test
    public void testBasic() {
        // Some basic input containing non-Avro compatible objects.
        Map<String, UUID> input = new HashMap<>();
        input.put("1", UUID.fromString("11111111-1111-1111-1111-111111111111"));
        input.put("2", UUID.fromString("12341234-1234-1234-1234-123412341234"));

        // Set up the converter to test.
        AvroConverter<UUID, String> elementConverter = new ConvertUUID();
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ConvertAvroMap<UUID, String> ac = new ConvertAvroMap(input.getClass(), SchemaBuilder.builder().map()
                .values(elementConverter.getSchema()), elementConverter);

        // Check that the converter can wrap the input list to look like a list of Avro compatible objects (String, in
        // this case).
        Map<String, String> avroValue = ac.convertToAvro(input);
        assertThat(avroValue.entrySet(), hasSize(2));
        assertThat(avroValue.get("1"), is("11111111-1111-1111-1111-111111111111"));
        assertThat(avroValue.get("2"), is("12341234-1234-1234-1234-123412341234"));

        // Check that the converter can convert backwards.
        avroValue = new HashMap<>();
        avroValue.put("3", "22222222-2222-2222-2222-222222222222");
        avroValue.put("4", "43211234-1234-1234-1234-123412341234");
        Map<String, UUID> datumValue = ac.convertToDatum(avroValue);

        assertThat(datumValue.entrySet(), hasSize(2));
        assertThat(datumValue, hasEntry("3", UUID.fromString("22222222-2222-2222-2222-222222222222")));
        assertThat(datumValue, hasEntry("4", UUID.fromString("43211234-1234-1234-1234-123412341234")));
    }
}
