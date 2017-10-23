package org.talend.daikon.converter;

import org.junit.Test;
import org.talend.daikon.exception.TalendRuntimeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * To find more test, please refer to TypeCOnverterTest
 *
 */
public class LocalDateTimeConverterTest {

    @Test
    public void testAsLocalDateTimeWithDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        assertEquals(LocalDateTime.of(2007, 12, 03, 10, 15, 30),
                TypeConverter.asLocalDateTime().withDateTimeFormatter(formatter).convert("03/12/2007 10:15:30"));
    }

    @Test(expected = TalendRuntimeException.class)
    public void testAsLocalDateTimeParseException() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        assertEquals(LocalDateTime.of(2007, 12, 03, 10, 15, 30),
                TypeConverter.asLocalDateTime().withDateTimeFormatter(formatter).convert("dd/12/2007 10:15:30"));
    }

}
