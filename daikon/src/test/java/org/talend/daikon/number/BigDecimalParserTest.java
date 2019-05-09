package org.talend.daikon.number;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BigDecimalParserTest {

    private Locale previousLocale;

    @Before
    public void setUp() throws Exception {
        previousLocale = Locale.getDefault();
    }

    @After
    public void tearDown() throws Exception {
        Locale.setDefault(previousLocale);
    }

    @Test
    public void testOtherSeparator() throws Exception {
        assertEquals(new BigDecimal(0), BigDecimalParser.toBigDecimal("0", ((char) -1), ((char) -1)));
    }

    @Test(expected = NumberFormatException.class)
    public void testEmptyString() throws Exception {
        BigDecimalParser.toBigDecimal("");
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidNumber1() throws Exception {
        BigDecimalParser.toBigDecimal("5.5k");
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidNumber2() throws Exception {
        BigDecimalParser.toBigDecimal("tagada");
    }

    @Test(expected = NumberFormatException.class)
    public void testNullString() throws Exception {
        BigDecimalParser.toBigDecimal(null);
    }

    @Test
    public void testToBigDecimalUS() throws Exception {
        assertFewLocales(new BigDecimal("12.5"), BigDecimalParser.toBigDecimal("0012.5"));
    }

    @Test
    public void testToBigDecimalCH() throws Exception {
        assertFewLocales(new BigDecimal("1012.5"), BigDecimalParser.toBigDecimal("1'012.5"));
    }

    @Test(expected = NumberFormatException.class)
    public void testToBigDecimalNotANumber() throws Exception {
        BigDecimalParser.toBigDecimal("ouf");
    }

    @Test
    public void testToBigDecimalPrecisionUS() throws Exception {
        assertFewLocales(0.35, BigDecimalParser.toBigDecimal("0.35").doubleValue());
    }

    @Test
    public void testToBigDecimalThousandGroupUS() throws Exception {
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10,012.5"));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10,012.5"));
        assertFewLocales(new BigDecimal("10012"), BigDecimalParser.toBigDecimal("10,012"));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012.5"));
    }

    @Test
    public void testToBigDecimalNegativeUS1() throws Exception {
        assertFewLocales(new BigDecimal("-12.5"), BigDecimalParser.toBigDecimal("-12.5"));
    }

    @Test
    public void testToBigDecimalNegativeUS2() throws Exception {
        assertFewLocales(new BigDecimal("-12.5"), BigDecimalParser.toBigDecimal("(12.5)"));
    }

    @Test
    public void testToBigDecimalEU() throws Exception {
        assertFewLocales(new BigDecimal("12.5"), BigDecimalParser.toBigDecimal("0012,5", ',', ' '));
    }

    @Test
    public void testToBigDecimalThousandGroupCH() throws Exception {
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10'012.5", '.', '\''));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012.5", '.', '\''));
        assertFewLocales(new BigDecimal("1010012.5"), BigDecimalParser.toBigDecimal("1'010'012.5", '.', '\''));
        assertFewLocales(new BigDecimal("10012"), BigDecimalParser.toBigDecimal("10 012", '.', '\''));
    }

    @Test
    public void testToBigDecimalThousandGroupEU() throws Exception {
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10 012,5", ',', ' '));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012,5", ',', ' '));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10.012,5", ',', '.'));
        assertFewLocales(new BigDecimal("10012"), BigDecimalParser.toBigDecimal("10 012", ',', ' '));
    }

    @Test
    public void testToBigDecimalThousandGroupBetterGuessEU() throws Exception {
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10 012,5"));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10012,5"));
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10.012,5"));
        assertFewLocales(new BigDecimal("12.5678"), BigDecimalParser.toBigDecimal("12,5678"));
    }

    @Test
    public void testNonBreakingSpace() throws Exception {
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10" + ((char) 160) + "012,5")); // char(160)
                                                                                                                   // is
                                                                                                                   // non-breaking
                                                                                                                   // space
        assertFewLocales(new BigDecimal("268435000000000000"), BigDecimalParser.toBigDecimal("268 435 000 000 000 000"));
        assertFewLocales(new BigDecimal("268435000000000000"), BigDecimalParser.toBigDecimal("268" + ((char) 160) + "435"
                + ((char) 160) + "000" + ((char) 160) + "000" + ((char) 160) + "000" + ((char) 160) + "000")); // char(160) is
                                                                                                               // non-breaking
                                                                                                               // space

        // we want the non-breaking space to be managed even if the classical space is defined as grouping separator:
        assertFewLocales(new BigDecimal("10012.5"), BigDecimalParser.toBigDecimal("10" + ((char) 160) + "012,5", ',', ' '));
    }

    @Test
    public void testToBigDecimalNegativeEU() throws Exception {
        assertFewLocales(new BigDecimal("-12.5"), BigDecimalParser.toBigDecimal("-12,5", ',', ' '));
    }

    @Test
    public void testToBigDecimalScientific() throws Exception {
        assertFewLocales(new BigDecimal("1230").toString(), BigDecimalParser.toBigDecimal("1.23E+3").toPlainString());
        assertFewLocales(new BigDecimal("1235.2").toString(), BigDecimalParser.toBigDecimal("1.2352E+3").toPlainString());

        // TDP-1356:
        assertFewLocales(new BigDecimal("10000").toString(), BigDecimalParser.toBigDecimal("1 E+4").toPlainString());
        assertFewLocales(new BigDecimal("10000").toString(),
                BigDecimalParser.toBigDecimal("1" + ((char) 160) + "E+4").toPlainString()); // char(160) is non-breaking space
        assertFewLocales(new BigDecimal("10000").toString(), BigDecimalParser.toBigDecimal("1 e+4").toPlainString());
    }

    @Test
    public void testToBigDecimalPercentage() throws Exception {
        // TDQ-13103
        assertFewLocales(new BigDecimal("0.36").toString(), BigDecimalParser.toBigDecimal("36%").toPlainString());
        assertFewLocales(new BigDecimal("0.2998").toString(), BigDecimalParser.toBigDecimal("29.98%").toPlainString());
        assertFewLocales(new BigDecimal("0.1598").toString(), BigDecimalParser.toBigDecimal("15.98 %").toPlainString());
        assertFewLocales(new BigDecimal("0.00025").toString(), BigDecimalParser.toBigDecimal("2.5E-2%").toPlainString());
        assertFewLocales(new BigDecimal("0.01").toString(),
                BigDecimalParser.toBigDecimal("1" + ((char) 160) + "%").toPlainString()); // char(160) is non-breaking space
    }

    @Test
    public void testGuessSeparatorsTwoDifferentSeparatorsPresent() {
        assertGuessSeparators("1,045.5", '.', ',');
        assertGuessSeparators("1 045,5", ',', ' ');
        assertGuessSeparators("1" + ((char) 160) + "045,5", ',', ((char) 160)); // char(160) is non-breaking space
        assertGuessSeparators("1.045,5", ',', '.');
        assertGuessSeparators("1'045,5", ',', '\'');

        assertGuessSeparators("2.051.045,5", ',', '.');
        assertGuessSeparators("2 051 045,5", ',', ' ');
        assertGuessSeparators("2" + ((char) 160) + "051" + ((char) 160) + "045,5", ',', ((char) 160)); // char(160) is
                                                                                                       // non-breaking space
        assertGuessSeparators("2,051,045.5", '.', ',');
        assertGuessSeparators("2'051'045.5", '.', '\'');
    }

    @Test
    public void testGuessSeparatorsManyGroupSep() {
        assertGuessSeparators("2.051.045", ',', '.');
        assertGuessSeparators("2 051 045", ',', ' ');
        assertGuessSeparators("2" + ((char) 160) + "051" + ((char) 160) + "045", '.', ((char) 160)); // char(160) is
                                                                                                     // non-breaking space
        assertGuessSeparators("2,051,045", '.', ',');
        assertGuessSeparators("2'051'045", '.', '\'');
    }

    @Test
    public void testGuessSeparatorsStartsWithDecimalSep() {
        assertGuessSeparators(".045", '.', ',');
        assertGuessSeparators(",045", ',', '.');
    }

    @Test
    public void testGuessSeparatorsNoGroup() {
        assertGuessSeparators("1045,5", ',', '.');
        assertGuessSeparators("2051045,5", ',', '.');
        assertGuessSeparators("1234,888", ',', '.');
    }

    @Test
    public void testGuessSeparatorsNotEndBy3Digits() {
        assertGuessSeparators("45,5", ',', '.');
        assertGuessSeparators("45,55", ',', '.');
        assertGuessSeparators("45,5555", ',', '.');
        assertGuessSeparators("45.5555", '.', ',');
        assertGuessSeparators("45" + ((char) 160) + "555,5", ',', ((char) 160)); // char(160) is non-breaking space
    }

    @Test
    public void testGuessSeparatorsTDP5153() {
        assertGuessSeparators("5 555,555", ',', ' ');
        assertGuessSeparators("5.555,555", ',', '.');
    }

    @Test
    public void testGuessSeparatorsTDKN233() {
        assertGuessSeparators("3 000.0", '.', ' ');
        assertGuessSeparators("3 000", ',', ' ');
        assertGuessSeparators("111 111 111", ',', ' ');
    }

    @Test
    public void testGetSupportedFormats() {
        List<DecimalFormat> supportedFormats = BigDecimalParser.getSupportedFormats();
        assertEquals(6, supportedFormats.size());
    }

    private void assertGuessSeparators(String value, char expectedDecimalSeparator, char expectedGroupingSeparator) {
        DecimalFormatSymbols decimalFormatSymbols = BigDecimalParser.guessSeparators(value);
        assertFewLocales(expectedGroupingSeparator, decimalFormatSymbols.getGroupingSeparator());
        assertFewLocales(expectedDecimalSeparator, decimalFormatSymbols.getDecimalSeparator());

        // Assert that negative symbol doesn't affect guess:
        decimalFormatSymbols = BigDecimalParser.guessSeparators("-" + value);
        assertFewLocales(expectedGroupingSeparator, decimalFormatSymbols.getGroupingSeparator());
        assertFewLocales(expectedDecimalSeparator, decimalFormatSymbols.getDecimalSeparator());

        // Assert that negative alt symbol doesn't affect guess:
        decimalFormatSymbols = BigDecimalParser.guessSeparators("(" + value + ")");
        assertFewLocales(expectedGroupingSeparator, decimalFormatSymbols.getGroupingSeparator());
        assertFewLocales(expectedDecimalSeparator, decimalFormatSymbols.getDecimalSeparator());
    }

    private void assertFewLocales(Object expected, Object actual) {
        for (Locale locale : new Locale[] { Locale.US, Locale.FRENCH, Locale.GERMAN }) {
            Locale.setDefault(locale);
            assertEquals("Not equals with locale=" + locale, expected, actual);
        }
    }

}
