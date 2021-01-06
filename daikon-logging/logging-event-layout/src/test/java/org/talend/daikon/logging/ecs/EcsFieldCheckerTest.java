package org.talend.daikon.logging.ecs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class EcsFieldCheckerTest {

    public static final String UNKNOWN_FIELD = "unknown_field";

    public static final List<String> ECS_FIELDS = Arrays.asList("ecs.field.first", "ecs.field.second");

    public static final List<String> ECS_LABELS = Arrays.asList("labels.my_awesome_label", "container.labels.blabla");

    public static final List<String> MALFORMED_ECS_LABELS = Arrays.asList("labels.my_awesome_label.first", "container.labels.",
            "labels", "container.labels");

    @Test
    public void test() {
        assertThat(EcsFieldsChecker.getECSFields().size(), is(7));
        // Check ECS fields are well recognized
        ECS_FIELDS.forEach(f -> assertThat(EcsFieldsChecker.isECSField(f), is(true)));
        ECS_FIELDS.forEach(f -> assertThat(EcsFieldsChecker.isECSLabel(f), is(false)));
        // Check that well formed ECS labels are recognized
        ECS_LABELS.forEach(f -> assertThat(EcsFieldsChecker.isECSField(f), is(true)));
        ECS_LABELS.forEach(f -> assertThat(EcsFieldsChecker.isECSLabel(f), is(true)));
        // Check that malformed ECS labels are not recognized
        MALFORMED_ECS_LABELS.forEach(f -> assertThat(EcsFieldsChecker.isECSField(f), is(false)));
        MALFORMED_ECS_LABELS.forEach(f -> assertThat(EcsFieldsChecker.isECSLabel(f), is(false)));
        // Check that unknown ECS fields are not recognized
        assertThat(EcsFieldsChecker.isECSField(UNKNOWN_FIELD), is(false));
        assertThat(EcsFieldsChecker.isECSLabel(UNKNOWN_FIELD), is(false));
    }
}
