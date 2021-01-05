package org.talend.daikon.logging.ecs;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Singleton which maps MDC keys with ECS fields based on mdc_ecs_mapping.yml file
 */
public class MdcEcsMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdcEcsMapper.class);

    private static final String MDC_ECS_MAPPING_FILE = "mdc_ecs_mapping.yml";

    private static final MdcEcsMapper INSTANCE = new MdcEcsMapper();

    private Map<String, String> mapping = new HashMap<>();

    private MdcEcsMapper() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File mdcEcsMappingFile = new File(classLoader.getResource(MDC_ECS_MAPPING_FILE).getFile());
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try {
            mapping = (Map<String, String>) om.readValue(mdcEcsMappingFile, Map.class);
        } catch (IOException e) {
            LOGGER.error("MDC ECS mapping file can't be read", e);
        }
    }

    private static MdcEcsMapper getInstance() {
        return INSTANCE;
    }

    /**
     * Return the MDC to ECS map
     *
     * @return the MDC to ECS map
     */
    public static Map<String, String> getMapping() {
        return Collections.unmodifiableMap(getInstance().mapping);
    }

    /**
     * Map a MDC key with its corresponding ECS field
     * In case where not mapping exists, the MDC key is returned
     *
     * @param mdcKey MDC key to map
     * @return the corresponding ECS field or the MDC key if no mapping exists
     */
    public static String map(String mdcKey) {
        if (getMapping().get(mdcKey) != null) {
            return getMapping().get(mdcKey);
        } else {
            return mdcKey;
        }
    }
}
