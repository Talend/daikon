package org.talend.daikon.di;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Unit-tests for {@link DynamicIndexMapperByIndex} class
 */
public class DynamicIndexMapperByIndexTest {
    
    /**
     * Checks {@link DynamicIndexMapperByIndex#computeIndexMap()} returns int array, which size equals n+1 and
     * with values which equal indexes of corresponding fields in runtime schema, where n - number of fields in design schema
     * and dynamic field position is at the start
     */
    @Test
    public void testComputeIndexMapStart() {
        int[] expectedIndexMap = { -1, 2, 3, 4 };
        
        Schema designSchema = SchemaBuilder.builder().record("Record")
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "0").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields()
                .name("col1").type().intType().noDefault()
                .name("col2").type().stringType().noDefault()
                .name("col3").type().intType().noDefault()
                .endRecord();
        
        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0_1").type().intType().noDefault()
                .name("col0_2").type().intType().noDefault()
                .name("col1").type().intType().noDefault()
                .name("col2").type().stringType().noDefault()
                .name("col3").type().intType().noDefault()
                .endRecord();
        
        DynamicIndexMapperByIndex indexMapper = new DynamicIndexMapperByIndex(designSchema, runtimeSchema);
        int[] actualIndexMap = indexMapper.computeIndexMap();
        assertArrayEquals(expectedIndexMap, actualIndexMap);
    }

    /**
     * Checks {@link DynamicIndexMapperByIndex#computeIndexMap()} returns int array, which size equals n+1 and
     * with values which equal indexes of corresponding fields in runtime schema, where n - number of fields in design schema
     * and dynamic field position is in the middle
     */
    @Test
    public void testComputeIndexMapMiddle() {
        int[] expectedIndexMap = { 0, -1, 3, 4 };
        
        Schema designSchema = SchemaBuilder.builder().record("Record")
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "1").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields()
                .name("col0").type().intType().noDefault()
                .name("col2").type().stringType().noDefault()
                .name("col3").type().intType().noDefault()
                .endRecord();
        
        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1_1").type().intType().noDefault()
                .name("col1_2").type().intType().noDefault()
                .name("col2").type().stringType().noDefault()
                .name("col3").type().intType().noDefault()
                .endRecord();
        
        DynamicIndexMapperByIndex indexMapper = new DynamicIndexMapperByIndex(designSchema, runtimeSchema);
        int[] actualIndexMap = indexMapper.computeIndexMap();
        assertArrayEquals(expectedIndexMap, actualIndexMap);
    }
    
    /**
     * Checks {@link DynamicIndexMapperByIndex#computeIndexMap()} returns int array, which size equals n+1 and
     * with values which equal indexes of corresponding fields in runtime schema, where n - number of fields in design schema
     * and dynamic field position is in the end
     */
    @Test
    public void testComputeIndexMapEnd() {
        int[] expectedIndexMap = { 0, 1, 2, -1 };
        
        Schema designSchema = SchemaBuilder.builder().record("Record")
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "3").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();
        
        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().intType().noDefault()
                .name("col2").type().intType().noDefault()
                .name("col3_1").type().stringType().noDefault()
                .name("col3_2").type().intType().noDefault()
                .endRecord();
        
        DynamicIndexMapperByIndex indexMapper = new DynamicIndexMapperByIndex(designSchema, runtimeSchema);
        int[] actualIndexMap = indexMapper.computeIndexMap();
        assertArrayEquals(expectedIndexMap, actualIndexMap);
    }
    
    /**
     * Checks {@link DynamicIndexMapperByIndex#DynamicIndexMapperByIndex(Schema, Schema)} throws {@link IllegalArgumentException}
     * if design schema argument doesn't contain dynamic field properties
     */
    @Test(expected=IllegalArgumentException.class)
    public void testIndexMapperConstructorThrowsException() {
        Schema designSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();
        
        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().intType().noDefault()
                .name("col2").type().intType().noDefault()
                .name("col3_1").type().stringType().noDefault()
                .name("col3_2").type().intType().noDefault()
                .endRecord();
        
        DynamicIndexMapperByIndex indexMapper = new DynamicIndexMapperByIndex(designSchema, runtimeSchema);
    }
    
    /**
     * Checks {@link DynamicIndexMapperByIndex#computeDynamicFieldsIndexes()} returns list, which contains indexes of runtime dynamic fields
     * (i.e. fields, which are present in runtime schema, but are not present in design schema)
     */
    @Test
    public void testComputeDynamicFieldsIndexes() {
        List<Integer> expectedIndexes = Arrays.asList(3, 4);
        
        Schema designSchema = SchemaBuilder.builder().record("Record")
                .prop(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION, "3").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().stringType().noDefault()
                .name("col2").type().intType().noDefault()
                .endRecord();
        
        Schema runtimeSchema = SchemaBuilder.builder().record("Record").fields()
                .name("col0").type().intType().noDefault()
                .name("col1").type().intType().noDefault()
                .name("col2").type().intType().noDefault()
                .name("col3_1").type().stringType().noDefault()
                .name("col3_2").type().intType().noDefault()
                .endRecord();
        
        DynamicIndexMapperByIndex indexMapper = new DynamicIndexMapperByIndex(designSchema, runtimeSchema);
        List<Integer> actualIndexes = indexMapper.computeDynamicFieldsIndexes();
        assertThat(actualIndexes, IsIterableContainingInOrder.contains(expectedIndexes.toArray()));
    }
    
}
