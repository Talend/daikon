package org.talend.daikon.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateConverter extends Converter<LocalDate> {

    public static final String FORMATTER = "formatter";

    @Override
    public LocalDate convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (properties.containsKey(LocalDateConverter.FORMATTER)) {
            try {
                return LocalDate.parse(value.toString(), getDateTimeFormatter());
            } catch (DateTimeParseException dtpe) {
                throw TypeConverterErrorCode.createCannotParseWithFormat(dtpe, value.toString(),
                        getDateTimeFormatter().toString());
            }
        }
        return LocalDate.parse(value.toString());
    }

    public LocalDateConverter withDateTimeFormatter(DateTimeFormatter formatter) {
        properties.put(LocalDateConverter.FORMATTER, formatter);
        return this;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return (DateTimeFormatter) properties.get(LocalDateTimeConverter.FORMATTER);
    }

}
