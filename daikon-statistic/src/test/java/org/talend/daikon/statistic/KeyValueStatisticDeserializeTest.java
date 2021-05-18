package org.talend.daikon.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.talend.daikon.statistic.pojo.KeyValueStatistic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KeyValueStatisticDeserializeTest {

    @ParameterizedTest
    @ValueSource(strings = { "keyValue/keyValueStats1.json" })
    public void testDeserializeKeyValueStat1(String filename) throws IOException {
        final InputStream json = StatisticDeserializerUtil.class.getResourceAsStream(filename);
        String jsonString = IOUtils.toString(json, StandardCharsets.UTF_8.name());

        Map<String, Integer> expectedMap = new HashMap<>();
        expectedMap.put("AAAaaAA", 200);
        expectedMap.put("AAA", 56);

        KeyValueStatistic<Integer> stat = (KeyValueStatistic<Integer>) StatisticDeserializerUtil.read(jsonString);
        assertEquals(KeyValueStatistic.class, stat.getClass());
        assertEquals("patternFrequency", stat.getKey());
        assertEquals(expectedMap, stat.getValue());
    }

}
