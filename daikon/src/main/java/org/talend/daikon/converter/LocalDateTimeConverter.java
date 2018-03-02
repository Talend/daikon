package org.talend.daikon.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter extends Converter<LocalDateTime> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalDateTime convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (properties.containsKey(LocalDateTimeConverter.FORMATTER)) {
            try {
                return LocalDateTime.parse(value.toString(), getDateTimeFormatter());
            } catch (DateTimeParseException dtpe) {
                throw TypeConverterErrorCode.createCannotParseWithFormat(dtpe, value.toString(),
                        getDateTimeFormatter().toString());
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
