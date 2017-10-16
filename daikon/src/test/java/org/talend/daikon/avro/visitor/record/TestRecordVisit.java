// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.avro.visitor.record;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.avro.visitor.record.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TestRecordVisit {

    @Test
    public void testSimpleTypes() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor();

        IndexedRecord record = loadRecord("simpleTypes");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/longField", 123456789L);
        visitor.verifyField("/stringField", "stringValue");
        visitor.verifyField("/booleanField", true);
        visitor.verifyField("/floatField", 1.234f);
        visitor.verifyField("/doubleField", 4.5678);
        visitor.verifyField("/nullField", null);
        visitor.verifyField("/enumField", "B");
        visitor.verifyField("/fixedField", new GenericData.Fixed(null, "c24d3c52ec03b24f".getBytes()));
        visitor.verifyBytesField("/bytesField", "ABCD".getBytes());
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testNestedRecordType() throws IOException {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("nestedRecord");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/longField", 123456789L);
        // visit the full inner record
        visitor.verifyField("/inner", "{\"innerIntField\": 321, \"innerLongField\": 987654321}");

        // visit the inner record structure
        visitor.verifyField("/inner/innerIntField", 321);
        visitor.verifyField("/inner/innerLongField", 987654321L);
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testArrayOfSimpleType() throws IOException {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("arrayOfSimpleType");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/arrayOfSimpleTypes", Arrays.asList(456L, 789L, 123456789L));
        visitor.verifyField("/arrayOfSimpleTypes[0]", 456L);
        visitor.verifyField("/arrayOfSimpleTypes[1]", 789L);
        visitor.verifyField("/arrayOfSimpleTypes[2]", 123456789L);
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testArrayOfSimpleTypeNotIndexed() throws IOException {
        RecordingVisitor visitor = new RecordingVisitor(VisitableArray.ArrayItemsPathType.NOT_INDEXED);
        IndexedRecord record = loadRecord("arrayOfSimpleType");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/arrayOfSimpleTypes", Arrays.asList(456L, 789L, 123456789L));
        visitor.verifyField("/arrayOfSimpleTypes", 456L, Schema.create(Schema.Type.LONG));
        visitor.verifyField("/arrayOfSimpleTypes", 789L, Schema.create(Schema.Type.LONG));
        visitor.verifyField("/arrayOfSimpleTypes", 123456789L, Schema.create(Schema.Type.LONG));
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testArrayOfArrayOfSimpleType() throws IOException {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("arrayOfArrayOfSimpleType");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        List<List<Integer>> array = createArrayOfArray();

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/arrayOfArrayOfSimpleType", array);
        visitor.verifyField("/arrayOfArrayOfSimpleType[0]", array.get(0));
        visitor.verifyField("/arrayOfArrayOfSimpleType[0][0]", array.get(0).get(0));
        visitor.verifyField("/arrayOfArrayOfSimpleType[0][1]", array.get(0).get(1));
        visitor.verifyField("/arrayOfArrayOfSimpleType[0][2]", array.get(0).get(2));
        visitor.verifyField("/arrayOfArrayOfSimpleType[1]", array.get(1));
        visitor.verifyField("/arrayOfArrayOfSimpleType[1][0]", array.get(1).get(0));
        visitor.verifyField("/arrayOfArrayOfSimpleType[1][1]", array.get(1).get(1));
        visitor.verifyField("/arrayOfArrayOfSimpleType[1][2]", array.get(1).get(2));
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testArrayOfArrayOfSimpleTypeNotIndexed() throws IOException {
        RecordingVisitor visitor = new RecordingVisitor(VisitableArray.ArrayItemsPathType.NOT_INDEXED);
        IndexedRecord record = loadRecord("arrayOfArrayOfSimpleType");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        List<List<Integer>> array = createArrayOfArray();

        Schema firstLevelArray = record.getSchema().getField("arrayOfArrayOfSimpleType").schema();
        Schema secondLevelArray = firstLevelArray.getElementType();

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/arrayOfArrayOfSimpleType", array);
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(0), secondLevelArray);
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(0).get(0), secondLevelArray.getElementType());
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(0).get(1), secondLevelArray.getElementType());
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(0).get(2), secondLevelArray.getElementType());
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(1), secondLevelArray);
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(1).get(0), secondLevelArray.getElementType());
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(1).get(1), secondLevelArray.getElementType());
        visitor.verifyField("/arrayOfArrayOfSimpleType", array.get(1).get(2), secondLevelArray.getElementType());
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testArrayOfRecords() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("arrayOfRecords");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/arrayOfRecords",
                "[{\"innerIntField\": 456, \"innerLongField\": 456789}, {\"innerIntField\": 789, \"innerLongField\": 789123}]");
        visitor.verifyField("/arrayOfRecords[0]", "{\"innerIntField\": 456, \"innerLongField\": 456789}");
        visitor.verifyField("/arrayOfRecords[0]/innerIntField", 456);
        visitor.verifyField("/arrayOfRecords[0]/innerLongField", 456789L);
        visitor.verifyField("/arrayOfRecords[1]", "{\"innerIntField\": 789, \"innerLongField\": 789123}");
        visitor.verifyField("/arrayOfRecords[1]/innerIntField", 789);
        visitor.verifyField("/arrayOfRecords[1]/innerLongField", 789123L);
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testArrayOfRecordsNotIndexed() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor(VisitableArray.ArrayItemsPathType.NOT_INDEXED);
        IndexedRecord record = loadRecord("arrayOfRecords");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        Schema innerSchema = record.getSchema().getField("arrayOfRecords").schema().getElementType();

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/arrayOfRecords", record.get(1));
        visitor.verifyField("/arrayOfRecords", "{\"innerIntField\": 456, \"innerLongField\": 456789}", innerSchema);
        visitor.verifyField("/arrayOfRecords/innerIntField", 456, Schema.create(Schema.Type.INT));
        visitor.verifyField("/arrayOfRecords/innerLongField", 456789L, Schema.create(Schema.Type.LONG));
        visitor.verifyField("/arrayOfRecords", "{\"innerIntField\": 789, \"innerLongField\": 789123}", innerSchema);
        visitor.verifyField("/arrayOfRecords/innerIntField", 789, Schema.create(Schema.Type.INT));
        visitor.verifyField("/arrayOfRecords/innerLongField", 789123L, Schema.create(Schema.Type.LONG));
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testOptionalSimpleType() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("optionalSimpleTypePresent", "optionalSimpleType");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/optionalLongField", 123456789L);
        Assert.assertTrue("Visitor not verified", visitor.isVerified());

        visitor = new RecordingVisitor();
        record = loadRecord("optionalSimpleTypeNull", "optionalSimpleType");

        wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/optionalLongField", null);
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testMapOfSimpleType() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("mapOfSimpleType");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/mapOfSimpleTypes", "{key1=345, key2=546, key3=125}");
        visitor.verifyField("/mapOfSimpleTypes/key1", 345L);
        visitor.verifyField("/mapOfSimpleTypes/key2", 546L);
        visitor.verifyField("/mapOfSimpleTypes/key3", 125L);
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testMapORecords() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("mapOfRecords");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/mapOfRecords", record.get(1));
        visitor.verifyField("/mapOfRecords/key1", "{\"innerInt\": 1, \"innerString\": \"value1\"}");
        visitor.verifyField("/mapOfRecords/key1/innerInt", 1);
        visitor.verifyField("/mapOfRecords/key1/innerString", "value1");
        visitor.verifyField("/mapOfRecords/key2", "{\"innerInt\": 2, \"innerString\": \"value2\"}");
        visitor.verifyField("/mapOfRecords/key2/innerInt", 2);
        visitor.verifyField("/mapOfRecords/key2/innerString", "value2");
        visitor.verifyField("/mapOfRecords/key3", "{\"innerInt\": 3, \"innerString\": \"value3\"}");
        visitor.verifyField("/mapOfRecords/key3/innerInt", 3);
        visitor.verifyField("/mapOfRecords/key3/innerString", "value3");
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    @Test
    public void testMapOfArrays() throws Exception {
        RecordingVisitor visitor = new RecordingVisitor();
        IndexedRecord record = loadRecord("mapOfArrays");

        VisitableRecord wrapper = new VisitableRecord(record);
        wrapper.accept(visitor);

        visitor.verifyRoot();
        visitor.verifyField("/intField", 123);
        visitor.verifyField("/mapOfArrays", record.get(1));
        visitor.verifyField("/mapOfArrays/key1", "[A, B, C]");
        visitor.verifyField("/mapOfArrays/key1[0]", "A");
        visitor.verifyField("/mapOfArrays/key1[1]", "B");
        visitor.verifyField("/mapOfArrays/key1[2]", "C");
        visitor.verifyField("/mapOfArrays/key2", "[D, E]");
        visitor.verifyField("/mapOfArrays/key2[0]", "D");
        visitor.verifyField("/mapOfArrays/key2[1]", "E");
        visitor.verifyField("/mapOfArrays/key3", "[F]");
        visitor.verifyField("/mapOfArrays/key3[0]", "F");
        Assert.assertTrue("Visitor not verified", visitor.isVerified());
    }

    private Schema loadSchema(String name) throws IOException {
        String filename = name + "_schema.json";
        try (InputStream schemaInputStream = this.getClass().getResourceAsStream(filename)) {
            return new Schema.Parser().parse(schemaInputStream);
        }
    }

    private IndexedRecord loadRecord(String name) throws IOException {
        return loadRecord(name, name);
    }

    private IndexedRecord loadRecord(String name, String schemaName) throws IOException {
        Schema schema = loadSchema(schemaName);
        String filename = name + "_record.json";
        try (InputStream recordInputStream = this.getClass().getResourceAsStream(filename)) {
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, recordInputStream);
            DatumReader<IndexedRecord> reader = new GenericDatumReader<>(schema);
            return reader.read(null, decoder);
        }
    }

    private List<List<Integer>> createArrayOfArray() {
        return Arrays.asList(Arrays.asList(123, 456, 789), Arrays.asList(987, 654, 321));
    }

    private static class RecordingVisitor implements RecordVisitor {

        private final LinkedList<VisitableStructure> visit = new LinkedList<>();

        private final VisitableArray.ArrayItemsPathType arraysPathType;

        private RecordingVisitor() {
            this(VisitableArray.ArrayItemsPathType.INDEXED);
        }

        private RecordingVisitor(VisitableArray.ArrayItemsPathType arraysPathType) {
            this.arraysPathType = arraysPathType;
        }

        @Override
        public void visit(VisitableInt field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableLong field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableString field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableBoolean field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableFloat field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableDouble field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableNull field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableFixed field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableBytes field) {
            visit.add(field);
        }

        @Override
        public void visit(VisitableRecord record) {
            visit.add(record);
            Iterator<VisitableStructure> fields = record.getFields();
            while (fields.hasNext()) {
                fields.next().accept(this);
            }
        }

        @Override
        public void visit(VisitableArray array) {
            visit.add(array);
            Iterator<VisitableStructure> items = array.getItems(this.arraysPathType);
            while (items.hasNext()) {
                items.next().accept(this);
            }
        }

        @Override
        public void visit(VisitableMap field) {
            visit.add(field);
            Iterator<VisitableStructure> items = field.getValues();
            while (items.hasNext()) {
                items.next().accept(this);
            }
        }

        public <T> void verifyField(String path, T value) {
            this.verifyField(path, value, null);
        }

        public <T> void verifyField(String path, T value, Schema schema) {
            VisitableStructure next = visit.removeFirst();
            Object actualValue = next.getValue();
            if (value instanceof String && (!(actualValue instanceof String))) {
                actualValue = actualValue.toString();
            }
            Assert.assertEquals("Value does not match", value, actualValue);
            Assert.assertEquals("Full path does not match", path, next.getPath().toString());
            if (schema != null) {
                Assert.assertEquals("Schema does not match", schema, next.getPath().last().getSchema());
            }
        }

        public void verifyBytesField(String path, byte[] value) {
            VisitableBytes field = (VisitableBytes) visit.removeFirst();
            ByteBuffer bytes = field.getValue();
            Assert.assertTrue(Arrays.equals(bytes.array(), value));
            Assert.assertEquals("Full path does not match", path, field.getPath().toString());
        }

        public void verifyRoot() {
            VisitableStructure next = visit.removeFirst();
            Assert.assertEquals("Full path does not match", "/", next.getPath().toString());
        }

        public boolean isVerified() {
            return visit.isEmpty();
        }
    }

}
