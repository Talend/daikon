package org.talend.daikon.converter;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * If a number or date formatter can be used in parsing an input from a String or formatting an output to a String, this helper
 * provides the mechanism to interpret the number, date, time or datetime.
 * 
 * @param <ThisT> The final implementing class (used for chaining).
 * @param <T> The type to convert to.
 */
public abstract class WithFormatConverter<ThisT extends WithFormatConverter<ThisT, T>, T> extends Converter<T> {

    /** If converting a String to/from a number using a format. */
    public static final String NUMBER_FORMATTER = "numberFormatter";

    /** If converting a String to/from an Avro date using a format. */
    public static final String DATE_FORMATTER = "dateFormatter";

    /** If converting a String to/from an Avro time-millis using a format. */
    public static final String TIME_MILLIS_FORMATTER = "timeMillisFormatter";

    /** If converting a String to/from an Avro timestamp-millis using a format. */
    public static final String TIMESTAMP_MILLIS_FORMATTER = "timestampMillisFormatter";

    public ThisT withNumberFormatter(DecimalFormat formatter) {
        properties.put(NUMBER_FORMATTER, formatter);
        return (ThisT) this;
    }

    public ThisT withDateFormatter(DateTimeFormatter formatter) {
        properties.put(DATE_FORMATTER, formatter);
        return (ThisT) this;
    }

    public ThisT withTimeMillisFormatter(DateTimeFormatter formatter) {
        properties.put(TIME_MILLIS_FORMATTER, formatter);
        return (ThisT) this;
    }

    public ThisT withTimestampMillisFormatter(DateTimeFormatter formatter) {
        properties.put(TIMESTAMP_MILLIS_FORMATTER, formatter);
        return (ThisT) this;
    }

    public DecimalFormat getNumberFormatter() {
        return (DecimalFormat) properties.get(NUMBER_FORMATTER);
    }

    public DateTimeFormatter getDateFormatter() {
        return (DateTimeFormatter) properties.get(DATE_FORMATTER);
    }

    public DateTimeFormatter getTimeMillisFormatter() {
        return (DateTimeFormatter) properties.get(TIME_MILLIS_FORMATTER);
    }

    public DateTimeFormatter getTimestampMillisFormatter() {
        return (DateTimeFormatter) properties.get(TIMESTAMP_MILLIS_FORMATTER);
    }
}
