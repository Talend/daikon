package org.talend.daikon.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.talend.daikon.statistic.pojo.SimpleStatistic;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SimpleStatisticDeserializeTest {

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats1.json" })
    public void testDeserializeSimpleStat1(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<Double> stats = (SimpleStatistic<Double>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("median", stats.getKey());
        assertEquals(100.53, stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats2.json" })
    public void testDeserializeSimpleStat2(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        Date expectedDate = new Date(489110400000L);

        SimpleStatistic<Date> stats = (SimpleStatistic<Date>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals(expectedDate, stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats3.json" })
    public void testDeserializeSimpleStat3(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<Integer> stats = (SimpleStatistic<Integer>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals(1664, stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats4.json" })
    public void testDeserializeSimpleStat4(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<String> stats = (SimpleStatistic<String>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals("myString", stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats5.json" })
    public void testDeserializeSimpleStat5(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<Float> stats = (SimpleStatistic<Float>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals(16.64f, stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats6.json" })
    public void testDeserializeSimpleStat6(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<BigDecimal> stats = (SimpleStatistic<BigDecimal>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals(BigDecimal.valueOf(16.64123456789), stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats7.json" })
    public void testDeserializeSimpleStat7(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<LocalDate> stats = (SimpleStatistic<LocalDate>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals(LocalDate.ofEpochDay(100l), stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "simple/simpleStats8.json" })
    public void testDeserializeSimpleStat8(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        SimpleStatistic<LocalTime> stats = (SimpleStatistic<LocalTime>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(SimpleStatistic.class, stats.getClass());
        assertEquals("upperQuantile", stats.getKey());
        assertEquals(LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.convert(101664, TimeUnit.NANOSECONDS)), stats.getValue());
    }
}
