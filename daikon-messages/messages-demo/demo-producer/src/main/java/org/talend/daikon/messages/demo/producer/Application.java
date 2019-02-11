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
package org.talend.daikon.messages.demo.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;

@SpringBootApplication
@EnableBinding(ProductEventsSink.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args); // NOSONAR
    }

    @Bean
    public MessageConverter avroMessageConverter() {
        return new AvroSchemaMessageConverter(MimeType.valueOf("application/avro"));
    }

}
