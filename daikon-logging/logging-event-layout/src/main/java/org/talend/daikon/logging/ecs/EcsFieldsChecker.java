package org.talend.daikon.logging.ecs;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Singleton which checks :
 * - whether a given field is an ECS label or not
 * - whether a given field is an ECS field or not, based on ecs_flat.yml file
 */
public class EcsFieldsChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(EcsFieldsChecker.class);

    private static final String ECS_FLAT_FILE = "ecs_flat.yml";

    private static final EcsFieldsChecker INSTANCE = new EcsFieldsChecker();

    // ECS Labels fields have a particular behavior as they contain custom keyword fields
    private static final List<String> LABELS_FIELDS = Arrays.asList( //
            EcsFields.LABELS.fieldName, //
            EcsFields.CONTAINER_LABELS.fieldName //
    );

    private Set<String> fields = new HashSet<>();

    private EcsFieldsChecker() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File ecsFlatFile = new File(classLoader.getResource(ECS_FLAT_FILE).getFile());
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try {
            fields = om.readValue(ecsFlatFile, Map.class).keySet();
        } catch (IOException e) {
            LOGGER.error("ECS fields file can't be read", e);
        }
    }

    private static EcsFieldsChecker getInstance() {
        return INSTANCE;
    }

    /**
     * Return the set of all ECS fields
     *
     * @return the set of all ECS fields
     */
    public static Set<String> getECSFields() {
        return Collections.unmodifiableSet(getInstance().fields);
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
        return (!LABELS_FIELDS.contains(field) && getECSFields().contains(field)) || isECSLabel(field);
    }
}
