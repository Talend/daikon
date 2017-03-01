// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.di;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.junit.Test;

/**
 * Tests for transformers classes.
 */
public class TransformerTest {

    @Test
    public void logicalDateTransformerTest() {
        int numberOfDays = 1000;
        Transformer transformer = new Transformer.LogicalDateTransformer();
        Object date = transformer.transform(numberOfDays);
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(0);
        c.add(Calendar.DAY_OF_YEAR, numberOfDays);
        assertThat(date, instanceOf(Date.class));
        assertThat((Date) date, is(c.getTime()));
    }

    @Test
    public void timeMillisTransformerTest() {
        long time = 60 * 60 * 1000;// one hour
        Transformer transformer = new Transformer.TimeMillisTransformer();
        Object timeTransformed = transformer.transform(time);
        assertThat(timeTransformed, instanceOf(Long.class));
        assertThat((Long) timeTransformed, is(time));
    }

    @Test
    public void timestampMillisTransformerTest() {
        long timestamp = System.currentTimeMillis();// one hour
        Transformer transformer = new Transformer.TimestampMillisTransformer();
        Date timestampExpected = new Date(timestamp);
        Object timestampTransformed = transformer.transform(timestamp);
        assertThat(timestampTransformed, instanceOf(Date.class));
        assertThat((Date) timestampTransformed, is(timestampExpected));
    }

    @Test
    public void shortTransformerTest() {
        short value = 12;
        Transformer transformer = new Transformer.ShortTransformer();
        Object shortTransformed = transformer.transform(value);
        assertThat(shortTransformed, instanceOf(Short.class));
        assertThat((Short) shortTransformed, is(value));

        String shortValue = "10";
        Short valueParsed = Short.valueOf(shortValue);
        shortTransformed = transformer.transform(shortValue);
        assertThat(shortTransformed, instanceOf(Short.class));
        assertThat((Short) shortTransformed, is(valueParsed));
    }

    @Test
    public void dateTransformerTest() {
        long value = System.currentTimeMillis();
        Date expected = new Date(value);
        Transformer transformer = new Transformer.DateTransformer();
        Object dateTransformed = transformer.transform(value);
        assertThat(dateTransformed, instanceOf(Date.class));
        assertThat((Date) dateTransformed, is(expected));

        dateTransformed = transformer.transform(expected);
        assertThat(dateTransformed, instanceOf(Date.class));
        assertThat((Date) dateTransformed, is(expected));
    }

    @Test
    public void byteTransformerTest() {
        Integer value = 10;
        Transformer transformer = new Transformer.ByteTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Byte.class));
        assertThat((Byte) transformed, is(value.byteValue()));

        transformed = transformer.transform(value.toString());
        assertThat(transformed, instanceOf(Byte.class));
        assertThat((Byte) transformed, is(value.byteValue()));
    }

    @Test
    public void characterTransformerTest() {
        char value = 'A';
        Transformer transformer = new Transformer.CharacterTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Character.class));
        assertThat((Character) transformed, is(value));

        transformed = transformer.transform(String.valueOf(value));
        assertThat(transformed, instanceOf(Character.class));
        assertThat((Character) transformed, is(value));
    }

    @Test
    public void bigDecimalTransformerTest() {
        String stringValue = "101123320";
        BigDecimal value = new BigDecimal(stringValue);
        Transformer transformer = new Transformer.BigDecimalTransformer();
        Object transformed = transformer.transform(stringValue);
        assertThat(transformed, instanceOf(BigDecimal.class));
        assertThat((BigDecimal) transformed, is(value));

        transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(BigDecimal.class));
        assertThat((BigDecimal) transformed, is(value));
    }

    @Test
    public void emptyTransformerTest() {
        String value = "Abcdef";
        Transformer transformer = new Transformer.EmptyTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(String.class));
        assertThat((String) transformed, is(value));
    }

    @Test
    public void smartDateTransformerTest() {
        String datePattern = "yyyy-MM-dd HH:mm:ss.SSS XXX";
        long value = System.currentTimeMillis();
        Date expected = new Date(value);
        Transformer transformer = new Transformer.SmartDateTimeTransformer(datePattern, new HashMap<String, SimpleDateFormat>());
        Object dateTransformed = transformer.transform(value);
        assertThat(dateTransformed, instanceOf(Date.class));
        assertThat((Date) dateTransformed, is(expected));

        dateTransformed = transformer.transform(expected);
        assertThat(dateTransformed, instanceOf(Date.class));
        assertThat((Date) dateTransformed, is(expected));

        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

        dateTransformed = transformer.transform(sdf.format(expected));
        assertThat(dateTransformed, instanceOf(Date.class));
        assertThat((Date) dateTransformed, is(expected));
    }

    @Test
    public void booleanTransformerTest() {
        String value = "true";
        Boolean expected = Boolean.valueOf(value);
        Transformer transformer = new Transformer.BooleanTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Boolean.class));
        assertThat((Boolean) transformed, is(expected));

        transformed = transformer.transform(expected);
        assertThat(transformed, instanceOf(Boolean.class));
        assertThat((Boolean) transformed, is(expected));
    }

    @Test
    public void bytesTransformerTest() {
        String value = "Test Value";
        byte[] expected = value.getBytes();
        Transformer transformer = new Transformer.BytesTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(byte[].class));
        assertThat((byte[]) transformed, is(expected));

        transformed = transformer.transform(expected);
        assertThat(transformed, instanceOf(byte[].class));
        assertThat((byte[]) transformed, is(expected));
    }

    @Test
    public void doubleTransformerTest() {
        Double value = 10.0;
        Transformer transformer = new Transformer.DoubleTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Double.class));
        assertThat((Double) transformed, is(value));

        transformed = transformer.transform(value.toString());
        assertThat(transformed, instanceOf(Double.class));
        assertThat((Double) transformed, is(value));
    }

    @Test
    public void floatTransformerTest() {
        Float value = 10.0f;
        Transformer transformer = new Transformer.FloatTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Float.class));
        assertThat((Float) transformed, is(value));

        transformed = transformer.transform(value.toString());
        assertThat(transformed, instanceOf(Float.class));
        assertThat((Float) transformed, is(value));
    }

    @Test
    public void intTransformerTest() {
        Integer value = 10;
        Transformer transformer = new Transformer.IntTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Integer.class));
        assertThat((Integer) transformed, is(value));

        transformed = transformer.transform(value.toString());
        assertThat(transformed, instanceOf(Integer.class));
        assertThat((Integer) transformed, is(value));
    }

    @Test
    public void longTransformerTest() {
        Integer value = 10;
        Transformer transformer = new Transformer.LongTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(Long.class));
        assertThat((Long) transformed, is(value.longValue()));

        transformed = transformer.transform(value.toString());
        assertThat(transformed, instanceOf(Long.class));
        assertThat((Long) transformed, is(value.longValue()));
    }

    @Test
    public void stringTransformerTest() {
        Object value = 10;
        Transformer transformer = new Transformer.StringTransformer();
        Object transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(String.class));
        assertThat((String) transformed, is(value.toString()));

        value = "Abcde";
        transformed = transformer.transform(value);
        assertThat(transformed, instanceOf(String.class));
        assertThat((String) transformed, is(value));
    }
}
