package org.talend.daikon.logging.ecs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class CustomFieldsTest {

    @Test
    public void testGetByName() {
        assertEquals(CustomFields.AUTH_CLIENT_ID, CustomFields.getByName(CustomFields.AUTH_CLIENT_ID.fieldName));
        assertNull("If you see this message - do a flip", CustomFields.getByName(UUID.randomUUID().toString()));
    }

    @Test
    public void testAllLowerCase() {
        Arrays.stream(CustomFields.values())
                .forEach(it -> Assert.assertEquals(it.fieldName.toLowerCase(Locale.ROOT), it.fieldName));
    }

    @Test
    public void testNoOverrideEcs() {
        Arrays.stream(CustomFields.values()).forEach(f -> ensureNoEcsField(f.fieldName));
    }

    @Test(expected = AssertionError.class)
    public void testDuplicateCheck() {
        ensureNoEcsField("event.id");
    }

    private void ensureNoEcsField(String customField) {
        assertNull("Custom field " + customField + " duplicates a field in ECS with same name",
                RealEcsFields.getByName(customField)); // in test scope, EcsFields contains test fields
    }
}
