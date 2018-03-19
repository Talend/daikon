package org.talend.daikon.messages.serialization;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.messages.MessageKey;

public class TestDaikonMessageSerialization {

    private DaikonMessageKeyDeserializer daikonMessageKeyDeserializer = new DaikonMessageKeyDeserializer();

    private DaikonMessageKeySerializer daikonMessageKeySerializer = new DaikonMessageKeySerializer();

    @Test
    public void testSerializationDeserialisation() {
        Map<String, String> keys = new HashMap<>();
        keys.put("key1", "value1");
        keys.put("key2", "value2");

        MessageKey data = MessageKey.newBuilder().setRandom("random1234").setTenantId("tenant1").setKeys(keys).build();
        byte[] serializeData = daikonMessageKeySerializer.serialize("topic", data);

        MessageKey deserializeData = daikonMessageKeyDeserializer.deserialize("topic", serializeData);

        Assert.assertEquals(data.getTenantId(), deserializeData.getTenantId());
        Assert.assertEquals(data.getRandom(), deserializeData.getRandom());
        Assert.assertEquals(data.getKeys(), deserializeData.getKeys());

    }

}
