package org.talend.daikon.statistic.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.talend.daikon.statistic.pojo.Statistic;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class StatisticsModule extends SimpleModule {

    public StatisticsModule() {
        // add deserializer for LocalTime with data representing number of millis since day start
        this.addDeserializer(LocalTime.class, new JsonDeserializer<LocalTime>() {

            @Override
            public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                JsonNode jsonNode = ctxt.readTree(jp);
                return LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.convert(jsonNode.asLong(), TimeUnit.NANOSECONDS));
            }
        });

        // deserializer modifier to handle generic type on statistic
        this.setDeserializerModifier(new BeanDeserializerModifier() {

            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
                    JsonDeserializer<?> deserializer) {
                if (Statistic.class.equals(beanDesc.getBeanClass())) {
                    return new StatisticDeserializer();
                }
                return deserializer;
            }
        });
    }
}
