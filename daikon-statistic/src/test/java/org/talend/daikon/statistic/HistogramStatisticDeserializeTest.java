package org.talend.daikon.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.talend.daikon.statistic.pojo.HistogramStatistic;
import org.talend.daikon.statistic.pojo.HistogramUnit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistogramStatisticDeserializeTest {

    @ParameterizedTest
    @ValueSource(strings = { "histogram/histogramStats1.json" })
    public void testDeserializeHistogramStat1(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        List<HistogramUnit<Date>> expectedHistogram = new ArrayList<>();
        expectedHistogram.add(new HistogramUnit<>(22, new Date(1414800000000L), new Date(1417392000000L)));
        expectedHistogram.add(new HistogramUnit<>(23, new Date(1417392000000L), new Date(1417392999999L)));

        HistogramStatistic<Date> stats = (HistogramStatistic<Date>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(HistogramStatistic.class, stats.getClass());
        assertEquals("myFirstHistogram", stats.getKey());
        assertEquals("MONTH", stats.getScaleUnit());
        assertEquals(expectedHistogram, stats.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "histogram/histogramStats2.json" })
    public void testDeserializeHistogramStat2(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        List<HistogramUnit<Double>> expectedHistogram = new ArrayList<>();
        expectedHistogram.add(new HistogramUnit<>(24, (double) -83885, 31540.0));
        expectedHistogram.add(new HistogramUnit<>(25, 31540.0, 99999.0));

        HistogramStatistic<Double> stats = (HistogramStatistic<Double>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(HistogramStatistic.class, stats.getClass());
        assertEquals("mySecondHistogram", stats.getKey());
        assertNull(stats.getScaleUnit());
        assertEquals(expectedHistogram, stats.getValue());
    }

}
