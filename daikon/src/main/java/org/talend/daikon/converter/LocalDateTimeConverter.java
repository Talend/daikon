package org.talend.daikon.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends Converter<LocalDateTime> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalDateTime convert(Object value) {
        return LocalDateTime.parse(value.toString());
    }

    public LocalDateTimeConverter withDateTimeFormatter(DateTimeFormatter formatter) {
        properties.put(LocalDateTimeConverter.FORMATTER, formatter);
        return this;
    }
}
