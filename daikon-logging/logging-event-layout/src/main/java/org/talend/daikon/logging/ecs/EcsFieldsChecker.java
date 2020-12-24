package org.talend.daikon.logging.ecs;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Lazy loaded singleton which checks :
 * - whether a given field is an ECS label or not, based on pattern "labels.*"
 * - whether a given field is an ECS field or not, based on ecs_flat.yml file
 */
public class EcsFieldsChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(EcsFieldsChecker.class);

    private static final String ECS_FLAT_FILE = "ecs_flat.yml";

    private static final String LABELS_FIELD = "labels";

    private static EcsFieldsChecker INSTANCE = null;

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
        if (INSTANCE == null) {
            INSTANCE = new EcsFieldsChecker();
        }
        return INSTANCE;
    }

    /**
     * Return the set of all ECS fields
     * 
     * @return the set of all ECS fields
     */
    public static Set<String> getECSFields() {
        return getInstance().fields;
    }

    /**
     * Check if a given field is an ECS label
     * 
     * @param field Field to check
     * @return true if the field is an ECS label or false otherwise
     */
    public static boolean isECSLabel(String field) {
        return field != null && field.split("\\.").length == 2 && LABELS_FIELD.equals(field.split("\\.")[0]);
    }

    /**
     * Check if a given field is an ECS field (ECS label included)
     * 
     * @param field Field to check
     * @return true if the field is an ECS field or false otherwise
     */
    public static boolean isECSField(String field) {
        return getECSFields().contains(field) || isECSLabel(field);
    }
}
