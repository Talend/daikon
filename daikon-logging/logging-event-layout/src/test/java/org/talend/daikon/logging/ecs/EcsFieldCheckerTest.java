package org.talend.daikon.logging.ecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EcsFieldCheckerTest {

    public static final String UNKNOWN_FIELD = "unknown_field";

    public static final String ECS_LABEL = "labels.my_awesome_label";

    public static final List<String> ECS_FIELDS = Arrays.asList("ecs.field.first", "ecs.field.second");

    @Test
    public void test() {
        assertThat(EcsFieldsChecker.getECSFields().size(), is(2));
        ECS_FIELDS.forEach(f -> assertThat(EcsFieldsChecker.isECSField(f), is(true)));
        ECS_FIELDS.forEach(f -> assertThat(EcsFieldsChecker.isECSLabel(f), is(false)));
        assertThat(EcsFieldsChecker.isECSField(ECS_LABEL), is(true));
        assertThat(EcsFieldsChecker.isECSLabel(ECS_LABEL), is(true));
        assertThat(EcsFieldsChecker.isECSField(UNKNOWN_FIELD), is(false));
        assertThat(EcsFieldsChecker.isECSLabel(UNKNOWN_FIELD), is(false));
    }
}
