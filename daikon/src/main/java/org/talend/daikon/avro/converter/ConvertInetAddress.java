package org.talend.daikon.avro.converter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

public class ConvertInetAddress implements AvroConverter<InetAddress, byte[]> {

    @Override
    public Schema getSchema() {
        return SchemaBuilder.unionOf().array().prop(SchemaConstants.JAVA_CLASS_FLAG, getDatumClass().getCanonicalName()).items()
                .fixed("Inet4").size(4).and().fixed("Inet6").size(16).endUnion();
    }

    @Override
    public Class<InetAddress> getDatumClass() {
        return InetAddress.class;
    }

    @Override
    public InetAddress convertToDatum(byte[] value) {
        try {
            return value == null ? null : InetAddress.getByAddress(value);
        } catch (UnknownHostException e) {
            // TODO(rskraba): Error handling
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] convertToAvro(InetAddress value) {
        return value == null ? null : value.getAddress();
    }

}