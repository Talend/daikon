package org.talend.daikon.version.api;

import java.beans.PropertyDescriptor;
import java.io.IOException;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * A {@link JsonSerializer} to serialize {@link Identifiable} objects. This is necessary as Spring has some trouble finding a
 * generic serializer when using {@link org.talend.daikon.version.service.RepositoryService}.
 *
 * @see Identifiable
 * @see org.talend.daikon.version.service.RepositoryService
 */
public class IdentifiableSerializer extends JsonSerializer {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        try {
            if (value instanceof Identifiable) {
                gen.writeStringField("_id", ((Identifiable) value).id());
            }
            final PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(value.getClass());
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getReadMethod() != null) {
                    final String name = propertyDescriptor.getName();
                    final String fieldValue = String.valueOf(propertyDescriptor.getReadMethod().invoke(value));
                    if ("class".equals(name)) {
                        gen.writeStringField("_class", fieldValue);
                    } else {
                        gen.writeStringField(name, fieldValue);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            gen.writeEndObject();
        }
    }
}
