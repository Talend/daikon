package org.talend.daikon.converter;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * To find more test, please refer to TypeConverterTest
 *
 */
public class LocalDateConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAsLocalDateWithDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        assertEquals(LocalDate.of(2007, 12, 03),
                TypeConverter.asLocalDate().withDateTimeFormatter(formatter).convert("03/12/2007"));
    }

    @Test
    public void testError() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        thrown.expect(TalendRuntimeException.class);
        thrown.expect(hasProperty("code", is(TypeConverterErrorCode.CANNOT_PARSE)));
        thrown.expectMessage("Cannot parse '  03/12/2007' using the specified format.");
        TypeConverter.asLocalDate().withDateTimeFormatter(formatter).convert("  03/12/2007");
    }

}
