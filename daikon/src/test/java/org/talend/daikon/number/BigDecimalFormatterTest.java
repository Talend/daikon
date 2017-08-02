package org.talend.daikon.number;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalFormatterTest {

    @Test
    public void testToBigDecimalUS() throws Exception {
        assertEquals("12.5", BigDecimalFormatter.format(new BigDecimal("12.50"), BigDecimalParser.US_DECIMAL_PATTERN));
        assertEquals("12", BigDecimalFormatter.format(new BigDecimal("12"), BigDecimalParser.US_DECIMAL_PATTERN));
        assertEquals("12.58", BigDecimalFormatter.format(new BigDecimal("12.57708"), BigDecimalParser.US_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalGroupingUS() throws Exception {
        assertEquals("4,512.5", BigDecimalFormatter.format(new BigDecimal("4512.50"), BigDecimalParser.US_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalNegativeUS() throws Exception {
        assertEquals("-12.5", BigDecimalFormatter.format(new BigDecimal("-12.5"), BigDecimalParser.US_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalEU() throws Exception {
        assertEquals("12,5", BigDecimalFormatter.format(new BigDecimal("12.50"), BigDecimalParser.EU_DECIMAL_PATTERN));
        assertEquals("12", BigDecimalFormatter.format(new BigDecimal("12"), BigDecimalParser.EU_DECIMAL_PATTERN));
        assertEquals("12,58", BigDecimalFormatter.format(new BigDecimal("12.57708"), BigDecimalParser.EU_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalGroupingEU() throws Exception {
        assertEquals("4" + ((char) 160) + "512,5",
                BigDecimalFormatter.format(new BigDecimal("4512.50"), BigDecimalParser.EU_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalNegativeEU() throws Exception {
        assertEquals("-12,5", BigDecimalFormatter.format(new BigDecimal("-12.5"), BigDecimalParser.EU_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalScientificUS() throws Exception {
        assertEquals("1.216E3",
                BigDecimalFormatter.format(new BigDecimal("1215.50"), BigDecimalParser.US_SCIENTIFIC_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalScientificEU() throws Exception {
        assertEquals("1,216E3",
                BigDecimalFormatter.format(new BigDecimal("1215.50"), BigDecimalParser.EU_SCIENTIFIC_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalPercentageUS() throws Exception {
        assertEquals("68.4%",
                BigDecimalFormatter.format(new BigDecimal("0.684"), BigDecimalParser.US_PERCENTAGE_DECIMAL_PATTERN));
        assertEquals("3.69%",
                BigDecimalFormatter.format(new BigDecimal("0.03686"), BigDecimalParser.US_PERCENTAGE_DECIMAL_PATTERN));
    }

    @Test
    public void testToBigDecimalPercentageEU() throws Exception {
        assertEquals("68,4%",
                BigDecimalFormatter.format(new BigDecimal("0.684"), BigDecimalParser.EU_PERCENTAGE_DECIMAL_PATTERN));
        assertEquals("3,69%",
                BigDecimalFormatter.format(new BigDecimal("0.03686"), BigDecimalParser.EU_PERCENTAGE_DECIMAL_PATTERN));
    }

}
