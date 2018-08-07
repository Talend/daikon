package org.talend.daikon.converter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TypeConverterTest {

    private String inputString = "3"; //$NON-NLS-1$

    private String inputStringDouble = "3.0"; //$NON-NLS-1$

    private String inputStringDefault = "0"; //$NON-NLS-1$

    private String inputStringDefaultDouble = "0.0"; //$NON-NLS-1$

    private String inputStringDefaultChar = "a"; //$NON-NLS-1$

    private String inputStringDefaultBoolean = "true"; //$NON-NLS-1$

    private String inputStringNull = null;

    private java.nio.ByteBuffer inputByteBufferNull = null;

    private byte inputbyte = 3;

    private byte inputbyteDefault = 0;

    private Byte inputByte = 3;

    private Byte inputByteNull = null;

    private Byte inputByteDefault = 0;

    private boolean inputboolean = true;

    private boolean inputbooleanDefault = false;

    private Boolean inputBoolean = true;

    private Boolean inputBooleanDefault = false;

    private Boolean inputBooleanNull = null;

    private double inputdouble = 3.0d;

    private double inputdoubleDefault = 0d;

    private Double inputDouble = 3.0D;

    private Double inputDoubleNull = null;

    private Double inputDoubleDefault = 0D;

    private float inputfloat = 3.0f;

    private float inputfloatDefault = 0f;

    private Float inputFloat = 3.0F;

    private Float inputFloatNull = null;

    private Float inputFloatDefault = 0F;

    private BigDecimal inputBigDecimal = new BigDecimal(3);

    // weird error case
    private BigDecimal inputBigDecimalDouble = new BigDecimal(((Double) 3.000D).toString());

    private BigDecimal inputBigDecimalNull = null;

    private BigDecimal inputBigDecimalDefault = new BigDecimal(0);

    private int inputint = 3;

    private int inputintDefault = 0;

    private Integer inputInteger = 3;

    private Integer inputIntegerNull = null;

    private Integer inputIntegerDefault = 0;

    private long inputlong = 3l;

    private long inputlongDefault = 0l;

    private Long inputLong = 3L;

    private Long inputLongNull = null;

    private Long inputLongDefault = 0L;

    private short inputshort = 3;

    private short inputshortDefault = 0;

    private Short inputShort = 3;

    private Short inputShortNull = null;

    private Short inputShortDefault = 0;

    private char inputchar = 'a';

    private char inputcharDefault = ' ';

    private Character inputChar = 'a';

    private Character inputCharNull = null;

    private Character inputCharDefault = ' ';

    private DecimalFormat numberFormatIntegerWithHash = new DecimalFormat("'#'#");

    private DecimalFormat numberFormatDoubleFinancial = new DecimalFormat("$#,##0.00;($#,##0.00)");

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static Locale initialLocale;

    @BeforeClass
    public static void setLocale() {
        initialLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @AfterClass
    public static void resetLocale() {
        Locale.setDefault(initialLocale);
    }

    @Test
    public void testAsString() {
        assertEquals(inputString, TypeConverter.asString().convert(inputString));
        assertEquals(inputString, TypeConverter.asString().convert(inputbyte));
        assertEquals(inputString, TypeConverter.asString().convert(inputByte));
        assertEquals(inputString, TypeConverter.asString().convert(java.nio.ByteBuffer.wrap(inputString.getBytes())));
        assertEquals(inputStringDouble, TypeConverter.asString().convert(inputdouble));
        assertEquals(inputStringDouble, TypeConverter.asString().convert(inputDouble));
        assertEquals(inputStringDouble, TypeConverter.asString().convert(inputfloat));
        assertEquals(inputStringDouble, TypeConverter.asString().convert(inputFloat));
        assertEquals(inputString, TypeConverter.asString().convert(inputBigDecimal));
        assertEquals(inputString, TypeConverter.asString().convert(inputint));
        assertEquals(inputString, TypeConverter.asString().convert(inputInteger));
        assertEquals(inputString, TypeConverter.asString().convert(inputlong));
        assertEquals(inputString, TypeConverter.asString().convert(inputLong));
        assertEquals(inputString, TypeConverter.asString().convert(inputshort));
        assertEquals(inputString, TypeConverter.asString().convert(inputShort));
        assertEquals(inputStringDefaultChar, TypeConverter.asString().convert(inputchar));
        assertEquals(inputStringDefaultChar, TypeConverter.asString().convert(inputChar));
    }

    @Test
    public void testAsStringDefault() {
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputStringNull));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputByteBufferNull));
        assertEquals(inputStringDefault, TypeConverter.asString().convert(inputbyteDefault));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputByteNull));
        assertEquals(inputStringDefaultDouble, TypeConverter.asString().convert(inputdoubleDefault));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputDoubleNull));
        assertEquals(inputStringDefaultDouble, TypeConverter.asString().convert(inputfloatDefault));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputFloatNull));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputBigDecimalNull));
        assertEquals(inputStringDefault, TypeConverter.asString().convert(inputintDefault));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputIntegerNull));
        assertEquals(inputStringDefault, TypeConverter.asString().convert(inputlongDefault));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputLongNull));
        assertEquals(inputStringDefault, TypeConverter.asString().convert(inputshortDefault));
        assertEquals(inputStringNull, TypeConverter.asString().convert(inputShortNull));
    }

    @Test
    public void testAsStringDefaultWithValue() {
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputStringNull));
        assertEquals(inputStringDefault,
                TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputByteBufferNull));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputbyteDefault));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputByteNull));
        assertEquals(inputStringDefaultDouble,
                TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputdoubleDefault));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputDoubleNull));
        assertEquals(inputStringDefaultDouble,
                TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputfloatDefault));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputFloatNull));
        assertEquals(inputStringDefault,
                TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputBigDecimalNull));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputintDefault));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputIntegerNull));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputlongDefault));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputLongNull));
        assertEquals(inputStringDefault,
                TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputshortDefault));
        assertEquals(inputStringDefault, TypeConverter.asString().withDefaultValue(inputStringDefault).convert(inputShortNull));
    }

    @Test
    public void testAsInteger() {
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputString));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputbyte));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputByte));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputdouble));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputDouble));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputfloat));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputFloat));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputBigDecimal));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputint));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputInteger));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputlong));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputLong));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputshort));
        assertEquals(inputInteger, TypeConverter.asInteger().convert(inputShort));
    }

    @Test
    public void testAsIntegerDefault() {
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputStringNull));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().convert(inputbyteDefault));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputByteNull));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().convert(inputdoubleDefault));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputDoubleNull));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().convert(inputfloatDefault));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputFloatNull));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputBigDecimalNull));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().convert(inputintDefault));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputIntegerNull));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().convert(inputlongDefault));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputLongNull));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().convert(inputshortDefault));
        assertEquals(inputIntegerNull, TypeConverter.asInteger().convert(inputShortNull));
    }

    @Test
    public void testAsIntegerDefaultWithValue() {
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputStringNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputbyteDefault));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputByteNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputdoubleDefault));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputDoubleNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputfloatDefault));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputFloatNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputBigDecimalNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputintDefault));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputIntegerNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputlongDefault));
        assertEquals(inputIntegerDefault, TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputLongNull));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputshortDefault));
        assertEquals(inputIntegerDefault,
                TypeConverter.asInteger().withDefaultValue(inputIntegerDefault).convert(inputShortNull));
    }

    @Test
    public void testAsByte() {
        assertEquals(inputByte, TypeConverter.asByte().convert(inputString));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputbyte));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputByte));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputdouble));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputDouble));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputfloat));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputFloat));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputBigDecimal));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputint));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputInteger));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputlong));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputLong));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputshort));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputShort));
        assertEquals(inputByte, TypeConverter.asByte().convert(inputShort));
        assertEquals(Byte.valueOf((byte) 0), TypeConverter.asByte().convert(false));
        assertEquals(Byte.valueOf((byte) 1), TypeConverter.asByte().convert(true));
    }

    @Test
    public void testAsByteDefault() {
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputStringNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputbyteDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputByteNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputdoubleDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputDoubleNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputfloatDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputFloatNull));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputBigDecimalNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputintDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputIntegerNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputlongDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputLongNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputshortDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputShortNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().convert(inputbooleanDefault));
        assertEquals(inputByteNull, TypeConverter.asByte().convert(inputBooleanNull));
    }

    @Test
    public void testAsByteDefaultWithValue() {
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputStringNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputbyteDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputByteNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputdoubleDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputDoubleNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputfloatDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputFloatNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputBigDecimalNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputintDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputIntegerNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputlongDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputLongNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputshortDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputShortNull));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputbooleanDefault));
        assertEquals(inputByteDefault, TypeConverter.asByte().withDefaultValue(inputByteDefault).convert(inputBooleanNull));
    }

    @Test
    public void testAsDouble() {
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputString));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputbyte));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputByte));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputdouble));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputDouble));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputfloat));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputFloat));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputBigDecimal));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputint));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputInteger));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputlong));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputLong));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputshort));
        assertEquals(inputDouble, TypeConverter.asDouble().convert(inputShort));
    }

    @Test
    public void testAsDoubleDefault() {
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputStringNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().convert(inputbyteDefault));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputByteNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().convert(inputdoubleDefault));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputDoubleNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().convert(inputfloatDefault));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputFloatNull));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputBigDecimalNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().convert(inputintDefault));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputIntegerNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().convert(inputlongDefault));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputLongNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().convert(inputshortDefault));
        assertEquals(inputDoubleNull, TypeConverter.asDouble().convert(inputShortNull));
    }

    @Test
    public void testAsDoubleDefaultWithValue() {
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputStringNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputbyteDefault));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputByteNull));
        assertEquals(inputDoubleDefault,
                TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputdoubleDefault));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputDoubleNull));
        assertEquals(inputDoubleDefault,
                TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputfloatDefault));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputFloatNull));
        assertEquals(inputDoubleDefault,
                TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputBigDecimalNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputintDefault));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputIntegerNull));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputlongDefault));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputLongNull));
        assertEquals(inputDoubleDefault,
                TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputshortDefault));
        assertEquals(inputDoubleDefault, TypeConverter.asDouble().withDefaultValue(inputDoubleDefault).convert(inputShortNull));
    }

    @Test
    public void testAsFloat() {
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputString));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputbyte));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputByte));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputdouble));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputDouble));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputfloat));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputFloat));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputBigDecimal));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputint));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputInteger));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputlong));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputLong));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputshort));
        assertEquals(inputFloat, TypeConverter.asFloat().convert(inputShort));
    }

    @Test
    public void testAsFloatDefault() {
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputStringNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().convert(inputbyteDefault));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputByteNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().convert(inputdoubleDefault));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputDoubleNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().convert(inputfloatDefault));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputFloatNull));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputBigDecimalNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().convert(inputintDefault));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputIntegerNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().convert(inputlongDefault));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputLongNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().convert(inputshortDefault));
        assertEquals(inputFloatNull, TypeConverter.asFloat().convert(inputShortNull));
    }

    @Test
    public void testAsFloatDefaultWithValue() {
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputStringNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputbyteDefault));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputByteNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputdoubleDefault));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputDoubleNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputfloatDefault));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputFloatNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputBigDecimalNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputintDefault));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputIntegerNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputlongDefault));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputLongNull));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputshortDefault));
        assertEquals(inputFloatDefault, TypeConverter.asFloat().withDefaultValue(inputFloatDefault).convert(inputShortNull));
    }

    @Test
    public void testAsBigDecimal() {
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputString));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputbyte));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputByte));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputdouble));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputDouble));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputfloat));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputFloat));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputBigDecimal));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputint));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputInteger));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputlong));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputLong));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputshort));
        assertEquals(inputBigDecimal, TypeConverter.asBigDecimal().convert(inputShort));
    }

    @Test
    public void testAsBigDecimalDefault() {
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputStringNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.asBigDecimal().convert(inputbyteDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputByteNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.asBigDecimal().convert(inputdoubleDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputDoubleNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.asBigDecimal().convert(inputfloatDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputFloatNull));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputBigDecimalNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.asBigDecimal().convert(inputintDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputIntegerNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.asBigDecimal().convert(inputlongDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputLongNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.asBigDecimal().convert(inputshortDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.asBigDecimal().convert(inputShortNull));
    }

    @Test
    public void testAsBigDecimalDefaultWithValue() {
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputStringNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputbyteDefault));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputByteNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputdoubleDefault));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputDoubleNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputfloatDefault));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputFloatNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputBigDecimalNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputintDefault));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputIntegerNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputlongDefault));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputLongNull));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputshortDefault));
        assertEquals(inputBigDecimalDefault,
                TypeConverter.asBigDecimal().withDefaultValue(inputBigDecimalDefault).convert(inputShortNull));
    }

    @Test
    public void testAsLong() {
        assertEquals(inputLong, TypeConverter.asLong().convert(inputString));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputbyte));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputByte));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputdouble));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputDouble));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputfloat));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputFloat));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputBigDecimal));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputint));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputInteger));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputlong));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputLong));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputshort));
        assertEquals(inputLong, TypeConverter.asLong().convert(inputShort));
    }

    @Test
    public void testAsLongDefault() {
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputStringNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().convert(inputbyteDefault));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputByteNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().convert(inputdoubleDefault));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputDoubleNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().convert(inputfloatDefault));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputFloatNull));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputBigDecimalNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().convert(inputintDefault));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputIntegerNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().convert(inputlongDefault));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputLongNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().convert(inputshortDefault));
        assertEquals(inputLongNull, TypeConverter.asLong().convert(inputShortNull));
    }

    @Test
    public void testAsLongDefaultWithValue() {
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputStringNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputbyteDefault));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputByteNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputdoubleDefault));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputDoubleNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputfloatDefault));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputFloatNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputBigDecimalNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputintDefault));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputIntegerNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputlongDefault));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputLongNull));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputshortDefault));
        assertEquals(inputLongDefault, TypeConverter.asLong().withDefaultValue(inputLongDefault).convert(inputShortNull));
    }

    @Test
    public void testAsShort() {
        assertEquals(inputShort, TypeConverter.asShort().convert(inputString));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputbyte));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputByte));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputdouble));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputDouble));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputfloat));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputFloat));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputBigDecimal));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputint));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputInteger));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputlong));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputLong));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputshort));
        assertEquals(inputShort, TypeConverter.asShort().convert(inputShort));
    }

    @Test
    public void testAsShortDefault() {
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputStringNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().convert(inputbyteDefault));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputByteNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().convert(inputdoubleDefault));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputDoubleNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().convert(inputfloatDefault));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputFloatNull));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputBigDecimalNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().convert(inputintDefault));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputIntegerNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().convert(inputlongDefault));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputLongNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().convert(inputshortDefault));
        assertEquals(inputShortNull, TypeConverter.asShort().convert(inputShortNull));
    }

    @Test
    public void testAsShortDefaultWithValue() {
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputStringNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputbyteDefault));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputByteNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputdoubleDefault));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputDoubleNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputfloatDefault));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputFloatNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputBigDecimalNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputintDefault));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputIntegerNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputlongDefault));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputLongNull));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputshortDefault));
        assertEquals(inputShortDefault, TypeConverter.asShort().withDefaultValue(inputShortDefault).convert(inputShortNull));
    }

    @Test
    public void testAsCharacter() {
        assertEquals((Character) inputString.charAt(0), TypeConverter.asCharacter().convert(inputString));
        assertEquals(inputChar, TypeConverter.asCharacter().convert(inputchar));
        assertEquals(inputChar, TypeConverter.asCharacter().convert(inputChar));
    }

    @Test
    public void testAsCharacterDefault() {
        assertEquals(inputCharNull, TypeConverter.asCharacter().convert(inputStringNull));
        assertEquals(inputCharDefault, TypeConverter.asCharacter().convert(inputcharDefault));
        assertEquals(inputCharNull, TypeConverter.asCharacter().convert(inputCharNull));
    }

    @Test
    public void testAsCharacterDefaultWithValue() {
        assertEquals(inputCharDefault, TypeConverter.asCharacter().withDefaultValue(inputCharDefault).convert(inputStringNull));
        assertEquals(inputCharDefault, TypeConverter.asCharacter().withDefaultValue(inputCharDefault).convert(inputcharDefault));
        assertEquals(inputCharDefault, TypeConverter.asCharacter().withDefaultValue(inputCharDefault).convert(inputCharNull));
    }

    @Test
    public void testAsBoolean() {
        assertEquals(inputboolean, TypeConverter.asBoolean().convert(inputboolean));
        assertEquals(inputBoolean, TypeConverter.asBoolean().convert(inputBoolean));
        assertEquals(inputBooleanDefault, TypeConverter.asBoolean().convert(inputBooleanDefault));
        assertEquals(inputBooleanNull, TypeConverter.asBoolean().convert(inputBooleanNull));

        assertEquals(inputBooleanDefault, TypeConverter.asBoolean().convert(inputString));
        assertEquals(inputBoolean, TypeConverter.asBoolean().convert(inputStringDefaultBoolean));
        assertEquals(inputBooleanNull, TypeConverter.asBoolean().convert(inputStringNull));
        assertEquals(inputBooleanNull, TypeConverter.asBoolean().convert(""));

        assertEquals(inputBooleanDefault, TypeConverter.asBoolean().convert(ByteBuffer.wrap(new byte[0])));
        assertEquals(inputBooleanDefault, TypeConverter.asBoolean().convert(ByteBuffer.wrap(new byte[] { 0 })));
        assertEquals(inputBoolean, TypeConverter.asBoolean().convert(ByteBuffer.wrap(new byte[] { 1 })));
    }

    @Test
    public void testAsBooleanDefaultWithValue() {
        assertEquals(false, TypeConverter.asBoolean().withDefaultValue(false).convert(inputBooleanNull));

        assertEquals(false, TypeConverter.asBoolean().withDefaultValue(false).convert(inputStringNull));
        assertEquals(false, TypeConverter.asBoolean().withDefaultValue(false).convert(""));
    }

    @Test
    public void testAsWithString() {
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputString));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputbyte));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputByte));
        assertEquals(inputString, TypeConverter.as(String.class).convert(java.nio.ByteBuffer.wrap(inputString.getBytes())));
        assertEquals(inputStringDouble, TypeConverter.as(String.class).convert(inputdouble));
        assertEquals(inputStringDouble, TypeConverter.as(String.class).convert(inputDouble));
        assertEquals(inputStringDouble, TypeConverter.as(String.class).convert(inputfloat));
        assertEquals(inputStringDouble, TypeConverter.as(String.class).convert(inputFloat));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputBigDecimal));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputint));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputInteger));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputlong));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputLong));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputshort));
        assertEquals(inputString, TypeConverter.as(String.class).convert(inputShort));
        assertEquals(inputStringDefaultChar, TypeConverter.as(String.class).convert(inputchar));
        assertEquals(inputStringDefaultChar, TypeConverter.as(String.class).convert(inputChar));
    }

    @Test
    public void testAsWithStringDefault() {
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputStringNull));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputByteBufferNull));
        assertEquals(inputStringDefault, TypeConverter.as(String.class).convert(inputbyteDefault));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputByteNull));
        assertEquals(inputStringDefaultDouble, TypeConverter.as(String.class).convert(inputdoubleDefault));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputDoubleNull));
        assertEquals(inputStringDefaultDouble, TypeConverter.as(String.class).convert(inputfloatDefault));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputFloatNull));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputBigDecimalNull));
        assertEquals(inputStringDefault, TypeConverter.as(String.class).convert(inputintDefault));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputIntegerNull));
        assertEquals(inputStringDefault, TypeConverter.as(String.class).convert(inputlongDefault));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputLongNull));
        assertEquals(inputStringDefault, TypeConverter.as(String.class).convert(inputshortDefault));
        assertEquals(inputStringNull, TypeConverter.as(String.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithInteger() {
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputString));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputbyte));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputByte));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputdouble));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputDouble));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputfloat));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputFloat));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputBigDecimal));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputint));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputInteger));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputlong));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputLong));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputshort));
        assertEquals(inputInteger, TypeConverter.as(Integer.class).convert(inputShort));
    }

    @Test
    public void testAsWithIntegerDefault() {
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputStringNull));
        assertEquals(inputIntegerDefault, TypeConverter.as(Integer.class).convert(inputbyteDefault));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputByteNull));
        assertEquals(inputIntegerDefault, TypeConverter.as(Integer.class).convert(inputdoubleDefault));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputDoubleNull));
        assertEquals(inputIntegerDefault, TypeConverter.as(Integer.class).convert(inputfloatDefault));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputFloatNull));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputBigDecimalNull));
        assertEquals(inputIntegerDefault, TypeConverter.as(Integer.class).convert(inputintDefault));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputIntegerNull));
        assertEquals(inputIntegerDefault, TypeConverter.as(Integer.class).convert(inputlongDefault));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputLongNull));
        assertEquals(inputIntegerDefault, TypeConverter.as(Integer.class).convert(inputshortDefault));
        assertEquals(inputIntegerNull, TypeConverter.as(Integer.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithByte() {
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputString));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputbyte));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputByte));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputdouble));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputDouble));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputfloat));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputFloat));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputBigDecimal));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputint));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputInteger));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputlong));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputLong));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputshort));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputShort));
        assertEquals(inputByte, TypeConverter.as(Byte.class).convert(inputShort));
        assertEquals(Byte.valueOf((byte) 0), TypeConverter.as(Byte.class).convert(false));
        assertEquals(Byte.valueOf((byte) 1), TypeConverter.as(Byte.class).convert(true));
    }

    @Test
    public void testAsWithByteDefault() {
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputStringNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputbyteDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputByteNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputdoubleDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputDoubleNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputfloatDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputFloatNull));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputBigDecimalNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputintDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputIntegerNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputlongDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputLongNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputshortDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputShortNull));
        assertEquals(inputByteDefault, TypeConverter.as(Byte.class).convert(inputbooleanDefault));
        assertEquals(inputByteNull, TypeConverter.as(Byte.class).convert(inputBooleanNull));
    }

    @Test
    public void testAsWithDouble() {
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputString));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputbyte));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputByte));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputdouble));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputDouble));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputfloat));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputFloat));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputBigDecimal));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputint));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputInteger));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputlong));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputLong));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputshort));
        assertEquals(inputDouble, TypeConverter.as(Double.class).convert(inputShort));
    }

    @Test
    public void testAsWithDoubleDefault() {
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputStringNull));
        assertEquals(inputDoubleDefault, TypeConverter.as(Double.class).convert(inputbyteDefault));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputByteNull));
        assertEquals(inputDoubleDefault, TypeConverter.as(Double.class).convert(inputdoubleDefault));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputDoubleNull));
        assertEquals(inputDoubleDefault, TypeConverter.as(Double.class).convert(inputfloatDefault));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputFloatNull));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputBigDecimalNull));
        assertEquals(inputDoubleDefault, TypeConverter.as(Double.class).convert(inputintDefault));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputIntegerNull));
        assertEquals(inputDoubleDefault, TypeConverter.as(Double.class).convert(inputlongDefault));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputLongNull));
        assertEquals(inputDoubleDefault, TypeConverter.as(Double.class).convert(inputshortDefault));
        assertEquals(inputDoubleNull, TypeConverter.as(Double.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithFloat() {
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputString));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputbyte));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputByte));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputdouble));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputDouble));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputfloat));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputFloat));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputBigDecimal));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputint));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputInteger));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputlong));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputLong));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputshort));
        assertEquals(inputFloat, TypeConverter.as(Float.class).convert(inputShort));
    }

    @Test
    public void testAsWithFloatDefault() {
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputStringNull));
        assertEquals(inputFloatDefault, TypeConverter.as(Float.class).convert(inputbyteDefault));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputByteNull));
        assertEquals(inputFloatDefault, TypeConverter.as(Float.class).convert(inputdoubleDefault));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputDoubleNull));
        assertEquals(inputFloatDefault, TypeConverter.as(Float.class).convert(inputfloatDefault));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputFloatNull));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputBigDecimalNull));
        assertEquals(inputFloatDefault, TypeConverter.as(Float.class).convert(inputintDefault));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputIntegerNull));
        assertEquals(inputFloatDefault, TypeConverter.as(Float.class).convert(inputlongDefault));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputLongNull));
        assertEquals(inputFloatDefault, TypeConverter.as(Float.class).convert(inputshortDefault));
        assertEquals(inputFloatNull, TypeConverter.as(Float.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithBigDecimal() {
        assertEquals(inputBigDecimalDouble, BigDecimal.valueOf(3.0d));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputString));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputbyte));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputByte));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputdouble));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputDouble));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputfloat));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputFloat));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputBigDecimal));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputint));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputInteger));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputlong));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputLong));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputshort));
        assertEquals(inputBigDecimal, TypeConverter.as(BigDecimal.class).convert(inputShort));
    }

    @Test
    public void testAsWithBigDecimalDefault() {
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputStringNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.as(BigDecimal.class).convert(inputbyteDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputByteNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.as(BigDecimal.class).convert(inputdoubleDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputDoubleNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.as(BigDecimal.class).convert(inputfloatDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputFloatNull));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputBigDecimalNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.as(BigDecimal.class).convert(inputintDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputIntegerNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.as(BigDecimal.class).convert(inputlongDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputLongNull));
        assertEquals(inputBigDecimalDefault, TypeConverter.as(BigDecimal.class).convert(inputshortDefault));
        assertEquals(inputBigDecimalNull, TypeConverter.as(BigDecimal.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithLong() {
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputString));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputbyte));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputByte));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputdouble));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputDouble));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputfloat));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputFloat));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputBigDecimal));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputint));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputInteger));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputlong));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputLong));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputshort));
        assertEquals(inputLong, TypeConverter.as(Long.class).convert(inputShort));
    }

    @Test
    public void testAsWithLongDefault() {
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputStringNull));
        assertEquals(inputLongDefault, TypeConverter.as(Long.class).convert(inputbyteDefault));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputByteNull));
        assertEquals(inputLongDefault, TypeConverter.as(Long.class).convert(inputdoubleDefault));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputDoubleNull));
        assertEquals(inputLongDefault, TypeConverter.as(Long.class).convert(inputfloatDefault));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputFloatNull));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputBigDecimalNull));
        assertEquals(inputLongDefault, TypeConverter.as(Long.class).convert(inputintDefault));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputIntegerNull));
        assertEquals(inputLongDefault, TypeConverter.as(Long.class).convert(inputlongDefault));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputLongNull));
        assertEquals(inputLongDefault, TypeConverter.as(Long.class).convert(inputshortDefault));
        assertEquals(inputLongNull, TypeConverter.as(Long.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithShort() {
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputString));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputbyte));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputByte));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputdouble));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputDouble));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputfloat));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputFloat));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputBigDecimal));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputint));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputInteger));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputlong));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputLong));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputshort));
        assertEquals(inputShort, TypeConverter.as(Short.class).convert(inputShort));
    }

    @Test
    public void testAsWithShortDefault() {
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputStringNull));
        assertEquals(inputShortDefault, TypeConverter.as(Short.class).convert(inputbyteDefault));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputByteNull));
        assertEquals(inputShortDefault, TypeConverter.as(Short.class).convert(inputdoubleDefault));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputDoubleNull));
        assertEquals(inputShortDefault, TypeConverter.as(Short.class).convert(inputfloatDefault));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputFloatNull));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputBigDecimalNull));
        assertEquals(inputShortDefault, TypeConverter.as(Short.class).convert(inputintDefault));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputIntegerNull));
        assertEquals(inputShortDefault, TypeConverter.as(Short.class).convert(inputlongDefault));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputLongNull));
        assertEquals(inputShortDefault, TypeConverter.as(Short.class).convert(inputshortDefault));
        assertEquals(inputShortNull, TypeConverter.as(Short.class).convert(inputShortNull));
    }

    @Test
    public void testAsWithCharacter() {
        assertEquals((Character) inputString.charAt(0), TypeConverter.as(Character.class).convert(inputString));
        assertEquals(inputChar, TypeConverter.as(Character.class).convert(inputchar));
        assertEquals(inputChar, TypeConverter.as(Character.class).convert(inputChar));
    }

    @Test
    public void testAsWithCharacterDefault() {
        assertEquals(inputCharNull, TypeConverter.as(Character.class).convert(inputStringNull));
        assertEquals(inputCharDefault, TypeConverter.as(Character.class).convert(inputcharDefault));
        assertEquals(inputCharNull, TypeConverter.as(Character.class).convert(inputCharNull));
    }

    @Test
    public void testAsWithBoolean() {
        assertEquals(inputboolean, TypeConverter.as(Boolean.class).convert(inputboolean));
        assertEquals(inputBoolean, TypeConverter.as(Boolean.class).convert(inputBoolean));
        assertEquals(inputBooleanDefault, TypeConverter.as(Boolean.class).convert(inputBooleanDefault));
        assertEquals(inputBooleanNull, TypeConverter.as(Boolean.class).convert(inputBooleanNull));

        assertEquals(inputBooleanDefault, TypeConverter.as(Boolean.class).convert(inputString));
        assertEquals(inputBoolean, TypeConverter.as(Boolean.class).convert(inputStringDefaultBoolean));
        assertEquals(inputBooleanNull, TypeConverter.as(Boolean.class).convert(inputStringNull));
    }

    @Test
    public void testAsLocalDate() {
        assertEquals(LocalDate.of(2007, 12, 03), TypeConverter.as(LocalDate.class).convert("2007-12-03"));
    }

    @Test
    public void testAsLocalTime() {
        assertEquals(LocalTime.of(8, 15, 20), TypeConverter.as(LocalTime.class).convert("08:15:20"));
    }

    @Test
    public void testAsLocalDateTime() {
        assertEquals(LocalDateTime.of(2007, 12, 03, 10, 15, 30),
                TypeConverter.as(LocalDateTime.class).convert("2007-12-03T10:15:30"));
    }

    @Test
    public void testToFromStringWithNumberFormats() {
        Converter<String> conv = TypeConverter.asString().withNumberFormatter(numberFormatDoubleFinancial);

        // Back and forth conversions from integers.
        {
            Object[][] examples = { //
                    { 0, "$0.00" }, //
                    { 123, "$123.00" }, //
                    { -123, "($123.00)" }, //
                    { 1234, "$1,234.00" }, //
                    { -1234, "($1,234.00)" } //
            };
            Converter<Integer> convertBack = TypeConverter.asInteger().withNumberFormatter(numberFormatDoubleFinancial);
            for (Object[] ex : examples) {
                int src = (int) ex[0];
                String dst = (String) ex[1];
                assertThat("Converting " + src, conv.convert(src), is(dst));
                assertThat("Converting back " + dst, convertBack.convert(dst), is(src));
            }
        }

        // Back and forth conversions from longs
        {
            Object[][] examples = { //
                    { 0L, "$0.00" }, //
                    { 123L, "$123.00" }, //
                    { -123L, "($123.00)" }, //
                    { 1234L, "$1,234.00" }, //
                    { -1234L, "($1,234.00)" }, //
                    { 123456789L, "$123,456,789.00" }, //
                    { -123456789L, "($123,456,789.00)" } //
            };
            Converter<Long> convertBack = TypeConverter.asLong().withNumberFormatter(numberFormatDoubleFinancial);
            for (Object[] ex : examples) {
                long src = (long) ex[0];
                String dst = (String) ex[1];
                assertThat("Converting " + src, conv.convert(src), is(dst));
                assertThat("Converting back " + dst, convertBack.convert(dst), is(src));
            }
        }

        // Back and forth conversions from floats
        {
            Object[][] examples = { //
                    { 0f, "$0.00" }, //
                    { 123f, "$123.00" }, //
                    { -123f, "($123.00)" }, //
                    { 1234f, "$1,234.00" }, //
                    { -1234f, "($1,234.00)" }, //
                    { 123456f, "$123,456.00" }, //
                    { -123456f, "($123,456.00)" }, //
                    { 1234.5f, "$1,234.50" }, //
                    { -1234.5f, "($1,234.50)" }, //
                    { 1234.56f, "$1,234.56" }, //
                    { -1234.56f, "($1,234.56)" }, //
                    { 1234.567f, "$1,234.57", 1234.57f }, //
                    { -1234.567f, "($1,234.57)", -1234.57f } //
            };
            Converter<Float> convertBack = TypeConverter.asFloat().withNumberFormatter(numberFormatDoubleFinancial);
            for (Object[] ex : examples) {
                float src = (float) ex[0];
                String dst = (String) ex[1];
                float srcBack = ex.length <= 2 ? src : (float) ex[2];
                assertThat("Converting " + src, conv.convert(src), is(dst));
                assertThat("Converting back " + dst, convertBack.convert(dst), is(srcBack));
            }
        }

        // Back and forth conversions from doubles
        {
            Object[][] examples = { //
                    { 0d, "$0.00" }, //
                    { 123d, "$123.00" }, //
                    { -123d, "($123.00)" }, //
                    { 1234d, "$1,234.00" }, //
                    { -1234d, "($1,234.00)" }, //
                    { 123456789d, "$123,456,789.00" }, //
                    { -123456789d, "($123,456,789.00)" }, //
                    { 1234.5d, "$1,234.50" }, //
                    { -1234.5d, "($1,234.50)" }, //
                    { 1234.56d, "$1,234.56" }, //
                    { -1234.56d, "($1,234.56)" }, //
                    { 1234.567d, "$1,234.57", 1234.57d }, //
                    { -1234.567d, "($1,234.57)", -1234.57d } //
            };
            Converter<Double> convertBack = TypeConverter.asDouble().withNumberFormatter(numberFormatDoubleFinancial);
            for (Object[] ex : examples) {
                double src = (double) ex[0];
                String dst = (String) ex[1];
                double srcBack = ex.length <= 2 ? src : (double) ex[2];
                assertThat("Converting " + src, conv.convert(src), is(dst));
                assertThat("Converting back " + dst, convertBack.convert(dst), is(srcBack));
            }
        }

        // Another format
        Converter<String> conv2 = TypeConverter.asString().withNumberFormatter(numberFormatIntegerWithHash);
        assertThat(conv2.convert(0), is("#0"));
        assertThat(conv2.convert(-123), is("-#123"));
        assertThat(conv2.convert(123.4f), is("#123"));
        assertThat(conv2.convert(-1234.567d), is("-#1235"));
    }

    @Test
    public void testToFromStringWithDateLikeFormats() {
        Converter<String> convDate = TypeConverter.asString().withDateFormatter(dateFormatter);
        Converter<String> convTime = TypeConverter.asString().withTimeMillisFormatter(timeFormatter);
        Converter<String> convDateTime = TypeConverter.asString().withTimestampMillisFormatter(dateTimeFormatter);

        Converter<Integer> convBackDate = TypeConverter.asInteger().withDateFormatter(dateFormatter);
        Converter<Integer> convBackTime = TypeConverter.asInteger().withTimeMillisFormatter(timeFormatter);
        Converter<Long> convBackDateTime = TypeConverter.asLong().withTimestampMillisFormatter(dateTimeFormatter);

        // Back and forth conversions for date.
        {
            Object[][] examples = { //
                    { 0, "1970-01-01" }, //
                    { 1, "1970-01-02" }, //
                    { 1000, "1972-09-27" }, //
                    { 17605, "2018-03-15" }, //
                    { -1, "1969-12-31" }, //
                    { -1000, "1967-04-07" } //
            };
            for (Object[] ex : examples) {
                int src = (int) ex[0];
                String dst = (String) ex[1];
                int srcBack = ex.length <= 2 ? src : (int) ex[2];
                assertThat("Converting date " + src, convDate.convert(src), is(dst));
                assertThat("Converting back date " + src, convBackDate.convert(dst), is(srcBack));
            }
        }

        // Back and forth conversions for time_millis.
        {
            Object[][] examples = { //
                    { 0, "00:00:00" }, //
                    { 1, "00:00:00", 0 }, //
                    { 1000, "00:00:01" }, //
                    { 123456789, "10:17:36", 37056000 }, //
                    { -1, "23:59:59", 86399000 } //
            };
            for (Object[] ex : examples) {
                int src = (int) ex[0];
                String dst = (String) ex[1];
                int srcBack = ex.length <= 2 ? src : (int) ex[2];
                assertThat("Converting time " + src, convTime.convert(src), is(dst));
                assertThat("Converting back time " + src, convBackTime.convert(dst), is(srcBack));
            }
        }

        // Back and forth conversions for timestamp_millis.
        {
            Object[][] examples = { //
                    { 0L, "1970-01-01T00:00:00Z" }, //
                    { 1L, "1970-01-01T00:00:00Z", 0L }, //
                    { 1000L, "1970-01-01T00:00:01Z" }, //
                    { 123456789L, "1970-01-02T10:17:36Z", 123456000L }, //
                    { -1L, "1969-12-31T23:59:59Z", -1000L } //
            };
            for (Object[] ex : examples) {
                long src = (long) ex[0];
                String dst = (String) ex[1];
                long srcBack = ex.length <= 2 ? src : (long) ex[2];
                assertThat("Converting datetime " + src, convDateTime.convert(src), is(dst));
                assertThat("Converting back datetime " + src, convBackDateTime.convert(dst), is(srcBack));
            }
        }
    }
}