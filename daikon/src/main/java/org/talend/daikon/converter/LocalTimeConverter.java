package org.talend.daikon.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter extends Converter<LocalTime> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalTime convert(Object value) {
        return LocalTime.parse(value.toString());
    }

    public LocalTimeConverter withDateTimeFormatter(DateTimeFormatter formatter) {
        properties.put(LocalTimeConverter.FORMATTER, formatter);
        return this;
    }
}
