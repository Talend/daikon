package org.talend.daikon.messages.serialization;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.messages.MessageKey;

/**
 * Daikon Avro Message Key deserializer.
 * <p>
 * Algorithm come from: https://cwiki.apache.org/confluence/display/AVRO/FAQ
 */
public class DaikonMessageKeyDeserializer implements Deserializer<MessageKey> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaikonMessageKeyDeserializer.class);

    private SpecificDatumReader<MessageKey> reader = new SpecificDatumReader<>(MessageKey.getClassSchema());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // do nothing
    }

    @Override
    public MessageKey deserialize(String topic, byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            LOGGER.error("Cannot deserialize Daikon MessageKey", e);
            throw new AvroRuntimeException(e);
        }
    }

    @Override
    public void close() {
        // do nothing
    }
}
