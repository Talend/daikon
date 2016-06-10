package org.talend.daikon.avro.converter;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;

/**
 * Unit tests for {ConvertAvroMap}.
 */
@SuppressWarnings("nls")
public class ConvertAvroMapTest {

    /**
     * Tests the basic usage of {ConvertAvroMap}.
     */
    @Test
    public void testBasicConvertToAvro() {
        // Some basic input containing non-Avro compatible objects.
        Map<String, UUID> input = new HashMap<>();
        input.put("1", UUID.fromString("11111111-1111-1111-1111-111111111111"));
        input.put("2", UUID.fromString("12341234-1234-1234-1234-123412341234"));

        // Set up the converter to test.
        AvroConverter<UUID, String> elementConverter = new ConvertUUID();
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ConvertAvroMap<UUID, String> ac = new ConvertAvroMap(input.getClass(),
                SchemaBuilder.builder().map().values(elementConverter.getSchema()), elementConverter);

        // Check that the converter can wrap the input list to look like a list of Avro compatible objects (String, in
        // this case).
        Map<String, String> avroValue = ac.convertToAvro(input);
        assertThat(avroValue.entrySet(), hasSize(2));
        assertThat(avroValue.get("1"), is("11111111-1111-1111-1111-111111111111"));
        assertThat(avroValue.get("2"), is("12341234-1234-1234-1234-123412341234"));

        // Explicitly test these accessor methods on the map for completeness.
        assertThat(avroValue.containsKey("1"), is(true));
        assertThat(avroValue.containsKey("no"), is(false));
        assertThat(avroValue.containsValue("11111111-1111-1111-1111-111111111111"), is(true));
        assertThat(avroValue.containsValue("99999999-1111-1111-1111-111111111111"), is(false));
        assertThat(avroValue.keySet(), containsInAnyOrder("1", "2"));
        assertThat(avroValue.values(),
                containsInAnyOrder("11111111-1111-1111-1111-111111111111", "12341234-1234-1234-1234-123412341234"));

        // Check that the entrySet is comparable to other Map.Entry implementations.
        Set<Map.Entry<String, String>> expectedEntries = new HashSet();
        expectedEntries.add(new AbstractMap.SimpleEntry<>("1", "11111111-1111-1111-1111-111111111111"));
        expectedEntries.add(new AbstractMap.SimpleEntry<>("2", "12341234-1234-1234-1234-123412341234"));
        assertThat(avroValue.entrySet(), is(expectedEntries));
    }

    /**
     * Tests the basic usage of {ConvertAvroMap} for converting back from Avro to a datum.
     */
    @Test
    public void testBasicConvertToDatum() {

        Map<String, String> avroValue = new HashMap<>();
        avroValue.put("3", "22222222-2222-2222-2222-222222222222");
        avroValue.put("4", "43211234-1234-1234-1234-123412341234");

        // Set up the converter to test.
        AvroConverter<UUID, String> elementConverter = new ConvertUUID();
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ConvertAvroMap<UUID, String> ac = new ConvertAvroMap(avroValue.getClass(),
                SchemaBuilder.builder().map().values(elementConverter.getSchema()), elementConverter);

        Map<String, UUID> datumValue = ac.convertToDatum(avroValue);
        assertThat(datumValue.entrySet(), hasSize(2));
        assertThat(datumValue, hasEntry("3", UUID.fromString("22222222-2222-2222-2222-222222222222")));
        assertThat(datumValue, hasEntry("4", UUID.fromString("43211234-1234-1234-1234-123412341234")));

        assertThat(datumValue.containsKey("3"), is(true));
        assertThat(datumValue.containsKey("no"), is(false));
        assertThat(datumValue.containsValue(UUID.fromString("22222222-2222-2222-2222-222222222222")), is(true));
        assertThat(datumValue.containsValue(UUID.fromString("99999999-2222-2222-2222-222222222222")), is(false));
        assertThat(datumValue.keySet(), containsInAnyOrder("3", "4"));
        assertThat(datumValue.values(), containsInAnyOrder(UUID.fromString("22222222-2222-2222-2222-222222222222"),
                UUID.fromString("43211234-1234-1234-1234-123412341234")));

        // Check that the entrySet is comparable to other Map.Entry implementations.
        Set<Map.Entry<String, UUID>> expectedEntries = new HashSet<>();
        expectedEntries.add(new AbstractMap.SimpleEntry<>("3", UUID.fromString("22222222-2222-2222-2222-222222222222")));
        expectedEntries.add(new AbstractMap.SimpleEntry<>("4", UUID.fromString("43211234-1234-1234-1234-123412341234")));
        assertThat(datumValue.entrySet(), is(expectedEntries));
    }
}
