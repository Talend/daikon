package org.talend.daikon.converter;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * To find more test, please refer to TypeCOnverterTest
 *
 */
public class LocalTimeConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAsLocalTimeWithDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ss:mm:HH");
        assertEquals(LocalTime.of(8, 15, 20), TypeConverter.asLocalTime().withDateTimeFormatter(formatter).convert("20:15:08"));
    }

    @Test
    public void testError() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ss:mm:HH");
        thrown.expect(TalendRuntimeException.class);
        thrown.expect(hasProperty("code", is(TypeConverterErrorCode.CANNOT_PARSE)));
        thrown.expectMessage("Cannot parse 'ss:15:08' using the specified format.");
        TypeConverter.asLocalTime().withDateTimeFormatter(formatter).convert("ss:15:08");
    }

}
