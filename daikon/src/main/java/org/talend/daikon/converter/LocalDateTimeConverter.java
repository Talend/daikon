package org.talend.daikon.converter;

import org.talend.daikon.exception.TalendRuntimeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter extends Converter<LocalDateTime> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalDateTime convert(Object value) {
        if (properties.containsKey(LocalDateTimeConverter.FORMATTER)) {
            try {
                return LocalDateTime.parse(value.toString(), getDateTimeFormatter());
            } catch (DateTimeParseException dtpe) {
                throw TalendRuntimeException.createUnexpectedException("Unable to parse " + value.toString());
            }
        }
        return LocalDateTime.parse(value.toString());
    }

    public LocalDateTimeConverter withDateTimeFormatter(DateTimeFormatter formatter) {
        properties.put(LocalDateTimeConverter.FORMATTER, formatter);
        return this;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return (DateTimeFormatter) properties.get(LocalDateTimeConverter.FORMATTER);
    }
}
