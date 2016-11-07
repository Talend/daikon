package org.talend.daikon.di;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;

/**
 * Instantiates concrete class of {@link DiOutgoingSchemaEnforcer} according to incoming arguments
 */
public final class EnforcerCreator {

    /**
     * Instantiates concrete class of {@link DiOutgoingSchemaEnforcer} according to incoming arguments
     * 
     * @param designSchema design schema specified by user
     * @param runtimeSchema runtime or actual schema, which comes with {@link IndexedRecord}
     * @param byIndex schema fields mapper mode; true for by index mode; false is for by name mode
     * @return instance of {@link DiOutgoingSchemaEnforcer}
     */
    public static DiOutgoingSchemaEnforcer createOutgoingEnforcer(Schema designSchema, Schema runtimeSchema, boolean byIndex) {

        DiOutgoingSchemaEnforcer enforcer = null;
        if (AvroUtils.isIncludeAllFields(designSchema)) {
            DynamicIndexMapper indexMapper = null;
            if (byIndex) {
                indexMapper = new DynamicIndexMapperByIndex(designSchema, runtimeSchema);
            } else {
                indexMapper = new DynamicIndexMapperByName(designSchema, runtimeSchema);
            }
            enforcer = new DiOutgoingDynamicSchemaEnforcer(designSchema, runtimeSchema, indexMapper);
        } else {
            IndexMapper indexMapper = null;
            if (byIndex) {
                indexMapper = new IndexMapperByIndex(designSchema);
            } else {
                indexMapper = new IndexMapperByName(designSchema, runtimeSchema);
            }
            enforcer = new DiOutgoingSchemaEnforcer(designSchema, indexMapper);
        }

        return enforcer;
    }
}
