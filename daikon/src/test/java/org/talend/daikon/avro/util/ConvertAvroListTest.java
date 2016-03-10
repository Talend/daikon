package org.talend.daikon.avro.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.daikon.avro.AvroConverter;

/**
 * Unit tests for {ConvertAvroList}.
 */
@SuppressWarnings("nls")
public class ConvertAvroListTest {

    /**
     * Tests the basic usage of {ConvertAvroList}.
     */
    @Test
    public void testBasic() {
        // Some basic input containing non-Avro compatible objects.
        List<UUID> input = Arrays.asList(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("12341234-1234-1234-1234-123412341234"));

        // Set up the converter to test.
        AvroConverter<UUID, String> elementConverter = new ConvertUUID();
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ConvertAvroList<UUID, String> ac = new ConvertAvroList(input.getClass(), SchemaBuilder.builder().array()
                .items(elementConverter.getSchema()), elementConverter);

        // Check that the converter can wrap the input list to look like a list of Avro compatible objects (String, in
        // this case).
        List<String> avroValue = ac.convertToAvro(input);
        assertThat(avroValue, hasSize(2));
        assertThat(avroValue, contains("11111111-1111-1111-1111-111111111111", "12341234-1234-1234-1234-123412341234"));

        // Check that the converter can convert backwards.
        List<UUID> datumValue = ac.convertToDatum(Arrays.asList("22222222-2222-2222-2222-222222222222",
                "43211234-1234-1234-1234-123412341234"));
        assertThat(datumValue, hasSize(2));
        assertThat(
                datumValue,
                contains(UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        UUID.fromString("43211234-1234-1234-1234-123412341234")));
    }
}
