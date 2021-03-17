package org.talend.daikon.logging.ecs;

import java.util.*;

/**
 * Singleton which checks :
 * - whether a given field is an ECS label or not
 * - whether a given field is an ECS field or not, based on ecs_flat.yml file
 */
public class EcsFieldsChecker {

    // ECS Labels fields have a particular behavior as they contain custom keyword fields
    private static final List<String> LABELS_FIELDS = Arrays.asList( //
            EcsFields.LABELS.fieldName, //
            EcsFields.CONTAINER_LABELS.fieldName //
    );

    private EcsFieldsChecker() {
    }

    private static Collection<String> getIgnoredFields() {
        return LABELS_FIELDS;
    }

    /**
     * Check if a given field is an ECS label
     *
     * @param field Field to check
     * @return true if the field is an ECS label or false otherwise
     */
    public static boolean isECSLabel(String field) {
        // For each labels field, check that :
        // * Given field start with labels field name
        // * Given field without labels prefix is not empty
        // * Given field without labels prefix doesn't contain another object
        return LABELS_FIELDS.stream().anyMatch( //
                f -> field.startsWith(f + ".") && //
                        !field.substring(f.length() + 1).isEmpty() && //
                        !field.substring(f.length() + 1).contains(".") //
        );
    }

    /**
     * Check if a given field is an ECS field (ECS label included)
     *
     * @param field Field to check
     * @return true if the field is an ECS field or false otherwise
     */
    public static boolean isECSField(String field) {
        return !getIgnoredFields().contains(field)
                && (EcsFields.containsName(field) || CustomFields.containsName(field) || isECSLabel(field));
    }
}
