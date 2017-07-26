package org.talend.daikon.kafka.mongo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = "org.talend.daikon.kafka.mongo")
@EnableMongoAuditing
public class MongoRepositoryConfiguration {
}
