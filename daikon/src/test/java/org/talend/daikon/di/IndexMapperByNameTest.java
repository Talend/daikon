package org.talend.daikon.di;

import static org.junit.Assert.assertArrayEquals;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;

/**
 * Unit-tests for {@link IndexMapperByName} class
 */
public class IndexMapperByNameTest {

    /**
     * Checks {@link IndexMapperByName#computeIndexMap()} returns int array, which size equals n and
     * with values: consecutive numbers from 0 to n-1, where n - number of fields in design schema
     * in case when design schema and runtime schema have same fields in same order
     */
    @Test
    public void testComputeIndexMap() {
        int[] expectedIndexMap = { 0, 1, 2 };

        Schema designSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();

        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();

        IndexMapperByName indexMapper = new IndexMapperByName(designSchema, runtimeSchema);
        int[] actualIndexMap = indexMapper.computeIndexMap();
        assertArrayEquals(expectedIndexMap, actualIndexMap);
    }

    /**
     * Checks {@link IndexMapperByName#computeIndexMap()} returns int array, which size equals n and
     * with values are indexes of corresponding fields in runtime schema, where n - number of fields in design schema
     * in case when design schema and runtime schema have same fields, but in different order
     */
    @Test
    public void testComputeIndexMapDiffOrder() {
        int[] expectedIndexMap = { 1, 0, 2 };

        Schema designSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();

        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col1").type().intType().noDefault()
                .name("col0").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();

        IndexMapperByName indexMapper = new IndexMapperByName(designSchema, runtimeSchema);
        int[] actualIndexMap = indexMapper.computeIndexMap();
        assertArrayEquals(expectedIndexMap, actualIndexMap);
    }
}
