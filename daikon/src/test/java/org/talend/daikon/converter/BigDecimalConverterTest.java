package org.talend.daikon.converter;

import org.junit.Test;
import org.talend.daikon.exception.TalendRuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static org.junit.Assert.assertEquals;

/**
 * To find more test, please refer to TypeCOnverterTest
 *
 */
public class BigDecimalConverterTest {

    private String inputString = "3"; //$NON-NLS-1$

    private byte inputbyte = 3;

    private Byte inputByte = 3;

    private double inputdouble = 3.0d;

    private Double inputDouble = 3.0D;

    private float inputfloat = 3.0f;

    private Float inputFloat = 3.0F;

    private BigDecimal inputBigDecimal = new BigDecimal(3);

    private BigDecimal outputBigDecimalWithScale = new BigDecimal(3).setScale(5);

    private int inputint = 3;

    private Integer inputInteger = 3;

    private long inputlong = 3l;

    private Long inputLong = 3L;

    private short inputshort = 3;

    private Short inputShort = 3;

    @Test
    public void tesAsBigDecimalWithScale() {
        // the only one impacted by the scale value
        assertEquals(outputBigDecimalWithScale, TypeConverter.asBigDecimal().withScale(5).convert(inputBigDecimal));

        // scale is unused
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputString));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputbyte));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputByte));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputdouble));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputDouble));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputfloat));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputFloat));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputint));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputInteger));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputlong));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputLong));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputshort));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withScale(5).convert(inputShort));
    }

    @Test
    public void testAsBigDecimalWithScaleAndRoundingmode() {
        // the only one impacted by the scale value
        assertEquals(outputBigDecimalWithScale,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputBigDecimal));

        // scale is unused
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputString));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputbyte));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputByte));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputdouble));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputDouble));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputfloat));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputFloat));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputint));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputInteger));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputlong));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputLong));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputshort));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withScale(5).withRoundingMode(RoundingMode.CEILING).convert(inputShort));
    }

    @Test
    public void testAsBigDecimalWithPrecision() {
        // the only one impacted by the precision value
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputBigDecimal));

        // Precision is unused
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputString));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputbyte));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputByte));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputdouble));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputDouble));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputfloat));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputFloat));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputint));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputInteger));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputlong));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputLong));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputshort));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().withPrecision(5).convert(inputShort));
    }

    @Test
    public void testAsBigDecimalWithPrecisionAndRoundingmode() {
        // the only one impacted by the Precision value
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputBigDecimal));

        // Precision is unused
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputString));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputbyte));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputByte));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputdouble));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputDouble));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputfloat));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputFloat));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputint));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputInteger));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputlong));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputLong));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputshort));
        assertEquals(inputBigDecimal,
                TypeConverter.asBigDecimal().withPrecision(5).withRoundingMode(RoundingMode.CEILING).convert(inputShort));
    }

    @Test
    public void testAsBigDecimalWithDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        String pattern = "#,###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);

        assertEquals(new BigDecimal(1234), TypeConverter.asBigDecimal().withDecimalFormat(decimalFormat).convert("1,234"));
    }

    @Test(expected = TalendRuntimeException.class)
    public void testAsBigDecimalParseException() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        String pattern = "#,###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);

        assertEquals(new BigDecimal(1234), TypeConverter.asBigDecimal().withDecimalFormat(decimalFormat).convert("BAD1,234"));
    }

}
