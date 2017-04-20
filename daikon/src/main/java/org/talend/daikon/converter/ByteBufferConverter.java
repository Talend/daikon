package org.talend.daikon.converter;

import java.nio.ByteBuffer;

public class ByteBufferConverter extends Converter<ByteBuffer> {

    @Override
    public ByteBuffer convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof ByteBuffer) {
            return (ByteBuffer) value;
        } else {
            return ByteBuffer.wrap(value.toString().getBytes());
        }
    }
}
