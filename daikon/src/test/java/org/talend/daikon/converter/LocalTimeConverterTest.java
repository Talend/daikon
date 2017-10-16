package org.talend.daikon.converter;

import org.junit.Test;
import org.talend.daikon.exception.TalendRuntimeException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * To find more test, please refer to TypeCOnverterTest
 *
 */
public class LocalTimeConverterTest {

    @Test
    public void testAsLocalTimeWithDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ss:mm:HH");
        assertEquals(LocalTime.of(8, 15, 20), TypeConverter.asLocalTime().withDateTimeFormatter(formatter).convert("20:15:08"));
    }

    @Test(expected = TalendRuntimeException.class)
    public void testAsLocalTimeParseException() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ss:mm:HH");
        assertEquals(LocalTime.of(8, 15, 20), TypeConverter.asLocalTime().withDateTimeFormatter(formatter).convert("ss:15:08"));
    }

}
