package org.talend.daikon.di;

import java.util.List;

/**
 * Provides means to map design and dynamic fields to runtime fields 
 */
interface DynamicIndexMapper extends IndexMapper {

    /**
     * Computes dynamic fields indexes
     * 
     * @return list of dynamic fields indexes
     */
    List<Integer> computeDynamicFieldsIndexes();
}
