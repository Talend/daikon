package org.talend.daikon.messages.serialization;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.messages.MessageKey;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Daikon Avro Message Key serializer.
 * <p>
 * Algorithm come from: https://cwiki.apache.org/confluence/display/AVRO/FAQ
 */
public class DaikonMessageKeySerializer implements Serializer<MessageKey> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaikonMessageKeySerializer.class);

    private DatumWriter<MessageKey> writer = new SpecificDatumWriter<>(MessageKey.getClassSchema());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // do nothing
    }

    @Override
    public byte[] serialize(String topic, MessageKey data) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Cannot serialize Daikon MessageKey", e);
            throw new AvroRuntimeException(e);
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
