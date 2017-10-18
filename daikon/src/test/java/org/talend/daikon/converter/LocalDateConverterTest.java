package org.talend.daikon.converter;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * To find more test, please refer to TypeConverterTest
 *
 */
public class LocalDateConverterTest {

    @Test
    public void testAsLocalDateWithDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        assertEquals(LocalDate.of(2007, 12, 03),
                TypeConverter.asLocalDate().withDateTimeFormatter(formatter).convert("03/12/2007"));
    }
}
