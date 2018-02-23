package org.talend.daikon.converter;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class TypeConverter {

    public static <T> Converter<T> as(Class<T> clazz) {
        if (clazz.equals(Boolean.class)) {
            return (Converter<T>) asBoolean();
        } else if (clazz.equals(Byte.class)) {
            return (Converter<T>) asByte();
        } else if (clazz.equals(ByteBuffer.class)) {
            return (Converter<T>) asByteByffer();
        } else if (clazz.equals(Character.class)) {
            return (Converter<T>) asCharacter();
        } else if (clazz.equals(Date.class)) {
            return (Converter<T>) asDate();
        } else if (clazz.equals(Double.class)) {
            return (Converter<T>) asDouble();
        } else if (clazz.equals(Float.class)) {
            return (Converter<T>) asFloat();
        } else if (clazz.equals(BigDecimal.class)) {
            return (Converter<T>) asBigDecimal();
        } else if (clazz.equals(Integer.class)) {
            return (Converter<T>) asInteger();
        } else if (clazz.equals(Long.class)) {
            return (Converter<T>) asLong();
        } else if (clazz.equals(Short.class)) {
            return (Converter<T>) asShort();
        } else if (clazz.equals(String.class)) {
            return (Converter<T>) asString();
        } else if (clazz.equals(LocalDate.class)) {
            return (Converter<T>) asLocalDate();
        } else if (clazz.equals(LocalTime.class)) {
            return (Converter<T>) asLocalTime();
        } else if (clazz.equals(LocalDateTime.class)) {
            return (Converter<T>) asLocalDateTime();
        } else { // Object
            return (Converter<T>) asObject();
        }
    }

    public static BooleanConverter asBoolean() {
        return new BooleanConverter();
    }

    public static ByteConverter asByte() {
        return new ByteConverter();
    }

    public static ByteBufferConverter asByteByffer() {
        return new ByteBufferConverter();
    }

    public static CharacterConverter asCharacter() {
        return new CharacterConverter();
    }

    public static DateConverter asDate() {
        return new DateConverter();
    }

    public static NumberConverter<?, Double> asDouble() {
        return NumberConverter.ofDouble();
    }

    public static NumberConverter<?, Float> asFloat() {
        return NumberConverter.ofFloat();
    }

    public static BigDecimalConverter asBigDecimal() {
        return new BigDecimalConverter();
    }

    public static NumberConverter<?, Integer> asInteger() {
        return NumberConverter.ofInteger();
    }

    public static NumberConverter<?, Long> asLong() {
        return NumberConverter.ofLong();
    }

    public static NumberConverter<?, Short> asShort() {
        return NumberConverter.ofShort();
    }

    public static StringConverter asString() {
        return new StringConverter();
    }

    public static ObjectConverter asObject() {
        return new ObjectConverter();
    }

    public static LocalDateConverter asLocalDate() {
        return new LocalDateConverter();
    }

    public static LocalTimeConverter asLocalTime() {
        return new LocalTimeConverter();
    }

    public static LocalDateTimeConverter asLocalDateTime() {
        return new LocalDateTimeConverter();
    }

}
