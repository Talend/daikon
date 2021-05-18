package org.talend.daikon.statistic.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.talend.daikon.statistic.pojo.HistogramStatistic;
import org.talend.daikon.statistic.pojo.KeyValueStatistic;
import org.talend.daikon.statistic.pojo.SimpleStatistic;
import org.talend.daikon.statistic.pojo.Statistic;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StatisticDeserializer extends StdDeserializer<Statistic> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticDeserializer.class);

    public StatisticDeserializer() {
        super(Statistic.class);
    }

    private Class defineClassBasedOnType(String type) {
        try {
            return StatisticClassBinding.valueOf(type.toUpperCase()).getClazz();
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("Cannot determine statistic class from {}. Fallback was done to SimpleStatistic.class", type);
            return StatisticClassBinding.SIMPLE.getClazz();
        }
    }

    private Class defineClassBasedOnValueType(String valueType) {
        try {
            return StatisticTypeClassBinding.valueOf(valueType.toUpperCase()).getClazz();
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("Cannot determine statistic value class from {}. Fallback was done to String.class", valueType);
            return StatisticTypeClassBinding.STRING.getClazz();
        }
    }

    @Override
    public Statistic deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = ctxt.readTree(p);

        // determine which stats type and stat value type we try to deserialize
        Class valueTypeClass = defineClassBasedOnValueType(jsonNode.get("valueType").asText());
        Class typeClass = defineClassBasedOnType(jsonNode.get("type").asText());

        JavaType type = ctxt.getTypeFactory().constructParametricType(typeClass, valueTypeClass);
        JsonParser reuse = jsonNode.traverse();
        reuse.nextToken(); // need to init the pointer

        return ctxt.readValue(reuse, type);
    }

    private enum StatisticClassBinding {
        SIMPLE(SimpleStatistic.class),
        KEYVALUE(KeyValueStatistic.class),
        HISTOGRAM(HistogramStatistic.class);

        Class<? extends Statistic> clazz;

        StatisticClassBinding(Class<? extends Statistic> keyValueStatisticClass) {
            this.clazz = keyValueStatisticClass;
        }

        public Class<? extends Statistic> getClazz() {
            return clazz;
        }
    }

    private enum StatisticTypeClassBinding {
        STRING(String.class),
        INTEGER(Integer.class),
        DOUBLE(Double.class),
        FLOAT(Float.class),
        BIGDECIMAL(BigDecimal.class),
        DATE(LocalDate.class),
        DATETIME(Date.class),
        TIME(LocalTime.class);

        Class clazz;

        StatisticTypeClassBinding(Class keyValueStatisticClass) {
            this.clazz = keyValueStatisticClass;
        }

        public Class getClazz() {
            return clazz;
        }
    }
}
