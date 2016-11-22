package org.talend.daikon.avro.converter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Test;

/**
 * Unit tests for {@link SingleColumnIndexedRecordConverter}
 */
public class SingleColumnIndexedRecordConverterTest {

    @Test
    public void testByteArrayConversion() {
        // Create a converter based on the byte array.
        SingleColumnIndexedRecordConverter<byte[]> converter = new SingleColumnIndexedRecordConverter<>(byte[].class,
                Schema.create(Schema.Type.BYTES));
        assertThat(converter.getDatumClass(), equalTo(byte[].class));
        assertThat(converter.getSchema().getType(), is(Schema.Type.RECORD));
        assertThat(converter.getSchema().getFields(), hasSize(1));
        assertThat(converter.getSchema().getFields().get(0).name(), is("field1"));
        assertThat(converter.getSchema().getFields().get(0).schema().getType(), is(Schema.Type.BYTES));

        // Check the results of the conversion.
        IndexedRecord converted = converter.convertToAvro(new byte[] { 1, 2, 3 });
        assertThat(converted.getSchema(), is(converter.getSchema()));
        assertThat(converted.get(0), instanceOf(byte[].class));
        assertArrayEquals(new byte[] { 1, 2, 3 }, (byte[]) converted.get(0));
    }

}