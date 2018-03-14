package org.talend.daikon.converter;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * To find more test, please refer to TypeCOnverterTest
 *
 */
public class LocalDateTimeConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAsLocalDateTimeWithDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        assertEquals(LocalDateTime.of(2007, 12, 03, 10, 15, 30),
                TypeConverter.asLocalDateTime().withDateTimeFormatter(formatter).convert("03/12/2007 10:15:30"));
    }

    @Test
    public void testError() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        thrown.expect(TalendRuntimeException.class);
        thrown.expect(hasProperty("code", is(TypeConverterErrorCode.CANNOT_PARSE)));
        thrown.expectMessage("Cannot parse 'dd/12/2007 10:15:30' using the specified format.");
        TypeConverter.asLocalDateTime().withDateTimeFormatter(formatter).convert("dd/12/2007 10:15:30");
    }

}
