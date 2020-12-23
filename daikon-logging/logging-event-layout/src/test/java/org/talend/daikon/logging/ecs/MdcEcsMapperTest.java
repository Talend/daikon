package org.talend.daikon.logging.ecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MdcEcsMapperTest {

    public static final String UNKNOWN_FIELD = "unknown_field";

    public static final Map<String, String> mdcEcsMap = new HashMap();

    static {
        mdcEcsMap.put("mdc_field_1", "ecs.field.first");
        mdcEcsMap.put("mdc_field_2", "ecs.field.second");
    }

    @Test
    public void test() {
        assertThat(MdcEcsMapper.getMapping().keySet().size(), is(2));
        mdcEcsMap.forEach((k, v) -> assertThat(MdcEcsMapper.map(k), is(v)));
        assertThat(MdcEcsMapper.map(UNKNOWN_FIELD), is(UNKNOWN_FIELD));
    }
}
