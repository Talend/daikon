package org.talend.daikon.avro.util;

import org.talend.daikon.avro.AvroConverter;

/**
 * Marks that an AvroConverter can contain other {@link AvroConverter}s (for processing elements or fields).
 */
public interface HasNestedAvroConverter<DatumT, AvroT> extends AvroConverter<DatumT, AvroT> {

    Iterable<AvroConverter<?, ?>> getNestedAvroConverters();
}
