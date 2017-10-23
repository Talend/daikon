package org.talend.daikon.converter;

import org.talend.daikon.exception.TalendRuntimeException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalTimeConverter extends Converter<LocalTime> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalTime convert(Object value) {
        if (properties.containsKey(LocalDateTimeConverter.FORMATTER)) {
            try {
                return LocalTime.parse(value.toString(), getDateTimeFormatter());
            } catch (DateTimeParseException dtpe) {
                throw TalendRuntimeException.createUnexpectedException("Unable to parse " + value.toString());
            }
        }
        return LocalTime.parse(value.toString());
    }

    public LocalTimeConverter withDateTimeFormatter(DateTimeFormatter formatter) {
        properties.put(LocalTimeConverter.FORMATTER, formatter);
        return this;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return (DateTimeFormatter) properties.get(LocalDateTimeConverter.FORMATTER);
    }
}
