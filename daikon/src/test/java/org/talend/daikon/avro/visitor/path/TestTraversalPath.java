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
package org.talend.daikon.avro.visitor.path;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;

public class TestTraversalPath {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRoot() {
        Schema schema = createSimpleSchema();
        TraversalPath path = TraversalPath.create(schema);
        TraversalPath.TraversalPathElement root = path.last();
        Assert.assertTrue(root instanceof org.talend.daikon.avro.visitor.path.TraversalPath.RootPathElement);
        Assert.assertEquals("/", path.toString());
        Assert.assertEquals(schema, root.getSchema());
    }

    @Test
    public void testAppend() {
        Schema schema = createSimpleSchema();
        TraversalPath path = TraversalPath.create(schema).append("step1").append("step2");
        Iterator<TraversalPath.TraversalPathElement> elements = path.iterator();
        TraversalPath.TraversalPathElement root = elements.next();
        Assert.assertTrue(root instanceof TraversalPath.RootPathElement);
        Assert.assertEquals(schema, root.getSchema());

        TraversalPath.TraversalPathElement step1 = elements.next();
        Assert.assertEquals("step1", step1.getName());
        Assert.assertEquals(0, step1.getPosition());
        Assert.assertEquals(schema.getField("step1").schema(), step1.getSchema());

        TraversalPath.TraversalPathElement step2 = elements.next();
        Assert.assertEquals("step2", step2.getName());
        Assert.assertEquals(0, step2.getPosition());
        Assert.assertEquals(schema.getField("step1").schema().getField("step2").schema(), step2.getSchema());

        Assert.assertEquals("/step1/step2", path.toString());

        Assert.assertEquals(root, path.root());
        Assert.assertEquals(step2, path.last());
    }

    @Test
    public void testAppendByIndex() {
        Schema schema = createSimpleSchema();
        TraversalPath path = TraversalPath.create(schema).append(0).append(0);
        Assert.assertEquals("/step1/step2", path.toString());
    }

    @Test
    public void testAppendByBadIndex() {
        Schema schema = createSimpleSchema();
        expectedException.expect(IndexOutOfBoundsException.class);
        TraversalPath.create(schema).append(35);
    }

    @Test
    public void testAppendNameNotFound() {
        Schema schema = createSimpleSchema();
        expectedException.expect(NullPointerException.class);
        TraversalPath.create(schema).append("step1").append("unknown");
    }

    @Test
    public void testAppendNotARecord() {
        Schema schema = createSimpleSchema();
        expectedException.expect(AvroRuntimeException.class);
        TraversalPath.create(schema).append("step1").append("step2").append("step3");
    }

    @Test
    public void testAppendArrayIndex() {
        Schema schema = createSimpleSchema();

        TraversalPath path = TraversalPath.create(schema).append("array").appendArrayIndex(5);

        Iterator<TraversalPath.TraversalPathElement> elements = path.iterator();

        TraversalPath.TraversalPathElement root = elements.next();
        Assert.assertTrue(root instanceof TraversalPath.RootPathElement);
        Assert.assertEquals(schema, root.getSchema());

        TraversalPath.TraversalPathElement array = elements.next();
        Assert.assertEquals("array", array.getName());
        Assert.assertEquals(1, array.getPosition());
        Assert.assertEquals(schema.getField("array").schema(), array.getSchema());

        TraversalPath.ArrayItemPathElement arrayElement = (TraversalPath.ArrayItemPathElement) elements.next();
        Assert.assertEquals("array", arrayElement.getName());
        Assert.assertEquals(1, arrayElement.getPosition());
        Assert.assertEquals(5, arrayElement.getIndex());
        Assert.assertEquals(schema.getField("array").schema().getElementType(), arrayElement.getSchema());

        Assert.assertEquals("/array[5]", path.toString());
    }

    @Test
    public void testAppendMapEntry() {
        Schema schema = createSimpleSchema();

        TraversalPath path = TraversalPath.create(schema).append("map").appendMapEntry("key1");

        Iterator<TraversalPath.TraversalPathElement> elements = path.iterator();

        TraversalPath.TraversalPathElement root = elements.next();
        Assert.assertTrue(root instanceof TraversalPath.RootPathElement);
        Assert.assertEquals(schema, root.getSchema());

        TraversalPath.TraversalPathElement map = elements.next();
        Assert.assertEquals("map", map.getName());
        Assert.assertEquals(2, map.getPosition());
        Assert.assertEquals(schema.getField("map").schema(), map.getSchema());

        TraversalPath.MapEntryPathElement entry = (TraversalPath.MapEntryPathElement) elements.next();
        Assert.assertEquals("map", entry.getName());
        Assert.assertEquals(2, entry.getPosition());
        Assert.assertEquals("key1", entry.getKey());
        Assert.assertEquals(schema.getField("map").schema().getValueType(), entry.getSchema());
    }

    @Test
    public void testJSONPathPrinter() throws Exception {
        Schema schema = createSimpleSchema();

        Assert.assertEquals("$.step1.step2",
                TraversalPath.create(schema).append("step1").append("step2").toString(new JsonPathPrinter()));

        Assert.assertEquals("$.array[5]",
                TraversalPath.create(schema).append("array").appendArrayIndex(5).toString(new JsonPathPrinter()));

        Assert.assertEquals("$.map.key1",
                TraversalPath.create(schema).append("map").appendMapEntry("key1").toString(new JsonPathPrinter()));
    }

    @Test
    public void testJSONPathPrinterBrackets() throws Exception {
        Schema schema = createSimpleSchema();
        Assert.assertEquals("$['step1']['step2']", TraversalPath.create(schema).append("step1").append("step2")
                .toString(new JsonPathPrinter(JsonPathPrinter.JsonPathStyle.BRACKETS)));

        Assert.assertEquals("$['array'][5]", TraversalPath.create(schema).append("array").appendArrayIndex(5)
                .toString(new JsonPathPrinter(JsonPathPrinter.JsonPathStyle.BRACKETS)));

        Assert.assertEquals("$['map']['key1']", TraversalPath.create(schema).append("map").appendMapEntry("key1")
                .toString(new JsonPathPrinter(JsonPathPrinter.JsonPathStyle.BRACKETS)));
    }

    @Test
    public void testIdentity() throws Exception {
        Schema schema1 = createSimpleSchema();
        TraversalPath path1 = TraversalPath.create(schema1).append("step1").append("step2");

        Schema schema2 = createSimpleSchema();
        TraversalPath path2 = TraversalPath.create(schema2).append("step1").append("step2");

        Assert.assertEquals(path1, path2);
    }

    private Schema createSimpleSchema() {
        return SchemaBuilder.record("record").fields().name("step1").type().record("step1Type").fields().name("step2").type()
                .stringType().noDefault().endRecord().noDefault().name("array").type().array().items().intType().noDefault()
                .name("map").type().map().values().intType().noDefault().endRecord();
    }
}
