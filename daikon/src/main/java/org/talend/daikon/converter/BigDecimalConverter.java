package org.talend.daikon.converter;

import org.talend.daikon.exception.TalendRuntimeException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

public class BigDecimalConverter extends Converter<BigDecimal> {

    public static String PRECISION = "precision";

    public static String SCALE = "scale";

    public static String ROUNDING_MODE = "roundingMode";

    public static String DECIMAL_FORMAT = "decimalFormat";

    @Override
    public BigDecimal convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else {
            if (value instanceof BigDecimal) {
                // When the value is a BigDecimal, we can change the scale
                BigDecimal castedValue = (BigDecimal) value;
                if (properties.containsKey(BigDecimalConverter.SCALE)) {
                    if (properties.containsKey(BigDecimalConverter.ROUNDING_MODE)) {
                        return castedValue.setScale(getScale(), getRoundingMode());
                    } else {
                        return castedValue.setScale(getScale());
                    }
                } else {
                    return castedValue;
                }
            } else {
                if (properties.containsKey(BigDecimalConverter.DECIMAL_FORMAT)) {
                    Number convertedValue = null;
                    try {
                        convertedValue = getDecimalFormat().parse(value.toString());
                    } catch (ParseException e) {
                        throw TalendRuntimeException.createUnexpectedException("Unable to parse " + value);
                    }
                    return new BigDecimal(convertedValue.toString());
                }
                // When the value is a anything else, we can change the precision
                else if (properties.containsKey(BigDecimalConverter.PRECISION)) {
                    if (properties.containsKey(BigDecimalConverter.ROUNDING_MODE)) {
                        return convertValue(value, new MathContext(getPrecision(), getRoundingMode()));
                    } else {
                        return convertValue(value, new MathContext(getPrecision()));
                    }
                } else {
                    return convertValue(value);
                }
            }
        }
    }

    private BigDecimal convertValue(Object value, MathContext mc) {
        if (value instanceof Byte) {
            return new BigDecimal((Byte) value, mc);
        } else if (value instanceof Double) {
            return new BigDecimal((Double) value, mc);
        } else if (value instanceof Float) {
            return new BigDecimal((Float) value, mc);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer) value, mc);
        } else if (value instanceof Long) {
            return new BigDecimal((Long) value, mc);
        } else if (value instanceof Short) {
            return new BigDecimal((Short) value, mc);
        } else {
            return new BigDecimal(value.toString(), mc);
        }
    }

    private BigDecimal convertValue(Object value) {
        if (value instanceof Byte) {
            return new BigDecimal((Byte) value);
        } else if (value instanceof Double) {
            return new BigDecimal((Double) value);
        } else if (value instanceof Float) {
            return new BigDecimal((Float) value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        } else if (value instanceof Long) {
            return new BigDecimal((Long) value);
        } else if (value instanceof Short) {
            return new BigDecimal((Short) value);
        } else {
            return new BigDecimal(value.toString());
        }
    }

    public BigDecimalConverter withPrecision(Integer precision) {
        properties.put(BigDecimalConverter.PRECISION, precision);
        return this;
    }

    public BigDecimalConverter withDecimalFormat(DecimalFormat decimalFormat) {
        properties.put(BigDecimalConverter.DECIMAL_FORMAT, decimalFormat);
        return this;
    }

    public DecimalFormat getDecimalFormat() {
        return (DecimalFormat) properties.get(BigDecimalConverter.DECIMAL_FORMAT);
    }

    public Integer getPrecision() {
        return (Integer) properties.get(BigDecimalConverter.PRECISION);
    }

    public BigDecimalConverter withScale(Integer precision) {
        properties.put(BigDecimalConverter.SCALE, precision);
        return this;
    }

    public Integer getScale() {
        return (Integer) properties.get(BigDecimalConverter.SCALE);
    }

    public BigDecimalConverter withRoundingMode(RoundingMode mode) {
        properties.put(BigDecimalConverter.ROUNDING_MODE, mode.toString());
        return this;
    }

    public RoundingMode getRoundingMode() {
        return RoundingMode.valueOf(properties.get(BigDecimalConverter.ROUNDING_MODE).toString());
    }

}
