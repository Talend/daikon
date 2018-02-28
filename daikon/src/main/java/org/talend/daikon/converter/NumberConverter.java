package org.talend.daikon.converter;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles conversions for the boxed Java numeric primitives, except for Byte (which has some special semantics and is handled by
 * the {@link ByteConverter}.)
 */
public abstract class NumberConverter<ThisT extends NumberConverter<ThisT, T>, T extends Number>
        extends WithFormatConverter<ThisT, T> {

    public static ShortConverter ofShort() {
        return new ShortConverter();
    }

    public static IntegerConverter ofInteger() {
        return new IntegerConverter();
    }

    public static LongConverter ofLong() {
        return new LongConverter();
    }

    public static FloatConverter ofFloat() {
        return new FloatConverter();
    }

    public static DoubleConverter ofDouble() {
        return new DoubleConverter();
    }

    @Override
    public T convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof Number) {
            return getFromNumber((Number) value);
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? getFromNumber(1) : getFromNumber(0);
        } else if (value instanceof ByteBuffer) {
            return getFromByteBuffer(((ByteBuffer) value).slice());
        } else if (value instanceof CharSequence) {
            CharSequence in = (CharSequence) value;
            if (getNumberFormatter() != null) {
                DecimalFormat format = getNumberFormatter();
                try {
                    return getFromNumber(format.parse(in.toString()));
                } catch (ParseException e) {
                    throw TypeConverterErrorCode.createCannotParseWithFormat(e, in.toString(), getNumberFormatter().toString());
                }
            } else if (getDateFormatter() != null) {
                DateTimeFormatter format = getDateFormatter();
                try {
                    return getFromNumber(LocalDate.parse(in, format).toEpochDay());
                } catch (DateTimeParseException e) {
                    throw TypeConverterErrorCode.createCannotParseWithFormat(e, in.toString(), format.toString());
                }
            } else if (getTimeMillisFormatter() != null) {
                DateTimeFormatter format = getTimeMillisFormatter();
                try {
                    return getFromNumber(LocalTime.parse(in, format).toNanoOfDay() / 1000000L);
                } catch (DateTimeParseException e) {
                    throw TypeConverterErrorCode.createCannotParseWithFormat(e, in.toString(), format.toString());
                }
            } else if (getTimestampMillisFormatter() != null) {
                DateTimeFormatter format = getTimestampMillisFormatter();
                try {
                    return getFromNumber(
                            LocalDateTime.parse(in, format).atZone(ZoneOffset.ofTotalSeconds(0)).toInstant().toEpochMilli());
                } catch (DateTimeParseException e) {
                    throw TypeConverterErrorCode.createCannotParseWithFormat(e, in.toString(), format.toString());
                }
            }
        }
        return getFromString(value.toString());
    }

    /**
     * Use the correct method to interpret the input number as the output type.
     */
    abstract protected T getFromNumber(Number in);

    /**
     * Read the number from bytes.
     * 
     * @param bb The buffer to read from. This parameter is save to read from without affecting the original positions.
     * @return The number.
     */
    abstract protected T getFromByteBuffer(ByteBuffer bb);

    /**
     * Use the correct method to interpret the input string as the output type.
     */
    abstract protected T getFromString(String in);

    private static class ShortConverter extends NumberConverter<ShortConverter, Short> {

        @Override
        protected Short getFromNumber(Number in) {
            return in.shortValue();
        }

        @Override
        protected Short getFromByteBuffer(ByteBuffer in) {
            return in.getShort();
        }

        @Override
        protected Short getFromString(String in) {
            return Short.parseShort(in);
        }
    }

    private static class IntegerConverter extends NumberConverter<IntegerConverter, Integer> {

        @Override
        protected Integer getFromNumber(Number in) {
            return in.intValue();
        }

        @Override
        protected Integer getFromByteBuffer(ByteBuffer in) {
            return in.getInt();
        }

        @Override
        protected Integer getFromString(String in) {
            return Integer.parseInt(in);
        }
    }

    private static class LongConverter extends NumberConverter<LongConverter, Long> {

        @Override
        protected Long getFromNumber(Number in) {
            return in.longValue();
        }

        @Override
        protected Long getFromByteBuffer(ByteBuffer in) {
            return in.getLong();
        }

        @Override
        protected Long getFromString(String in) {
            return Long.parseLong(in);
        }
    }

    private static class FloatConverter extends NumberConverter<FloatConverter, Float> {

        @Override
        protected Float getFromNumber(Number in) {
            return in.floatValue();
        }

        @Override
        protected Float getFromByteBuffer(ByteBuffer in) {
            return in.getFloat();
        }

        @Override
        protected Float getFromString(String in) {
            return Float.parseFloat(in);
        }
    }

    private static class DoubleConverter extends NumberConverter<DoubleConverter, Double> {

        @Override
        protected Double getFromNumber(Number in) {
            return in.doubleValue();
        }

        @Override
        protected Double getFromByteBuffer(ByteBuffer in) {
            return in.getDouble();
        }

        @Override
        protected Double getFromString(String in) {
            return Double.parseDouble(in);
        }
    }
}
