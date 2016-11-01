package org.talend.daikon.di;

/**
 * Provides means to map design fields to runtime fields
 */
interface IndexMapper {

    /**
     * Computes map of correspondence between design fields (POJO fields) and runtime fields
     * (IndexedRecord fields)
     * 
     * @return map of correspondence
     */
    int[] computeIndexMap();

}
