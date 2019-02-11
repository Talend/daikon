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
package org.talend.daikon.messages.header;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.messages.MessageHeader;
import org.talend.daikon.messages.MessageIssuer;
import org.talend.daikon.messages.MessageTypes;
import org.talend.daikon.messages.header.consumer.MessageHeaderExtractor;

public class TestMessageHeaderExtractor {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testHiddenSecuredField() {
        MessageHeader messageHeader = MessageHeader.newBuilder().setId("My id") //
                .setCorrelationId("Correlation id") //
                .setTimestamp(123L) //
                .setIssuer(MessageIssuer.newBuilder().setApplication("Application1").setService("Service1").setVersion("ABC")
                        .build()) //
                .setType(MessageTypes.COMMAND).setName("name").setTenantId("tenantId").setUserId("userId") //
                .setSecurityToken("securityToken").build();

        Assert.assertNotNull(messageHeader.toString());
        Assert.assertTrue(messageHeader.toString().contains("\"securityToken\": <hidden>"));
    }

    @Test
    public void testExtractMessageHeaderWithSpecificRecord() throws Exception {
        Schema headerSchema = loadMessageHeaderSchema();
        Schema messageSchema = SchemaBuilder.record("message").fields().name("header").type(headerSchema).noDefault()
                .name("customField").type().stringType().noDefault().endRecord();

        MessageHeader messageHeader = MessageHeader.newBuilder().setId("My id").setCorrelationId("Correlation id")
                .setTimestamp(123L)
                .setIssuer(MessageIssuer.newBuilder().setApplication("Application1").setService("Service1").setVersion("ABC")
                        .build())
                .setType(MessageTypes.COMMAND).setName("name").setTenantId("tenantId").setUserId("userId")
                .setSecurityToken("securityToken").build();

        IndexedRecord message = new GenericRecordBuilder(messageSchema).set("header", messageHeader).set("customField", "ABC")
                .build();

        MessageHeaderExtractor extractor = new MessageHeaderExtractor();
        MessageHeader output = extractor.extractHeader(message);
        Assert.assertEquals(messageHeader, output);
    }

    @Test
    public void testGetMessageHeaderWithGenericRecord() throws Exception {
        Schema headerSchema = loadMessageHeaderSchema();
        Schema messageSchema = SchemaBuilder.record("message").fields().name("header").type(headerSchema).noDefault()
                .name("customField").type().stringType().noDefault().endRecord();

        IndexedRecord issuer = new GenericRecordBuilder(headerSchema.getField("issuer").schema())
                .set("application", "Application1").set("service", "Service1").set("version", "ABC").build();

        IndexedRecord messageHeader = new GenericRecordBuilder(headerSchema).set("id", "My id")
                .set("correlationId", "Correlation id").set("timestamp", 123L).set("issuer", issuer).set("type", "COMMAND")
                .set("name", "name").set("tenantId", "tenantId").set("userId", "userId").set("securityToken", "securityToken")
                .build();

        IndexedRecord message = new GenericRecordBuilder(messageSchema).set("header", messageHeader).set("customField", "ABC")
                .build();

        MessageHeaderExtractor extractor = new MessageHeaderExtractor();
        MessageHeader output = extractor.extractHeader(message);
        Assert.assertNotNull(output);
        Assert.assertEquals(0, GenericData.get().compare(messageHeader, output, headerSchema));
    }

    @Test
    public void testStringAsFirstField() throws Exception {
        Schema messageSchema = SchemaBuilder.record("message").fields().name("fakeHeader").type().stringType().noDefault()
                .endRecord();

        IndexedRecord message = new GenericRecordBuilder(messageSchema).set("fakeHeader", "hello").build();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Provided message's first field is not a record but STRING");

        MessageHeaderExtractor extractor = new MessageHeaderExtractor();
        extractor.extractHeader(message);
    }

    @Test
    public void testUnknownRecordAsFirstField() throws Exception {
        Schema firstFieldSchema = SchemaBuilder.record("firstField").fields().name("field1").type().stringType().noDefault()
                .endRecord();

        Schema messageSchema = SchemaBuilder.record("message").fields().name("fakeHeader").type(firstFieldSchema).noDefault()
                .endRecord();

        IndexedRecord firstField = new GenericRecordBuilder(firstFieldSchema).set("field1", "value1").build();

        IndexedRecord message = new GenericRecordBuilder(messageSchema).set("fakeHeader", firstField).build();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Provided message's first field is not a header but firstField");

        MessageHeaderExtractor extractor = new MessageHeaderExtractor();
        extractor.extractHeader(message);
    }

    private Schema loadMessageHeaderSchema() throws IOException {
        return new Schema.Parser().parse(this.getClass().getResourceAsStream("/MessageHeader.avsc"));
    }

}
