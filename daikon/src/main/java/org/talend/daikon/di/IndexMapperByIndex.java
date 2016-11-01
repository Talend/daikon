package org.talend.daikon.di;

import org.apache.avro.Schema;

/**
 * {@link IndexMapper} implementation, which match fields according their indexes
 */
class IndexMapperByIndex implements IndexMapper {

    /**
     * Number of fields in design schema. It is also equaled number of fields of POJO class in case there is no dynamic fields
     */
    private final int designSchemaSize;

    /**
     * Constructor sets design schema size
     * 
     * @param designSchema design schema
     */
    IndexMapperByIndex(Schema designSchema) {
        designSchemaSize = designSchema.getFields().size();
    }

    /**
     * {@inheritDoc}
     * 
     * If there is no dynamic fields runtime indexes equal design indexes
     */
    @Override
    public int[] computeIndexMap() {
        int[] indexMap = new int[designSchemaSize];
        for (int i = 0; i < designSchemaSize; i++) {
            indexMap[i] = i;
        }
        return indexMap;
    }

}
