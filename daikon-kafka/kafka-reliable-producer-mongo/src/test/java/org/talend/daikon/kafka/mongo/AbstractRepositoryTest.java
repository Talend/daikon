package org.talend.daikon.kafka.mongo;

import java.util.Set;

import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.fakemongo.Fongo;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.Mongo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public abstract class AbstractRepositoryTest {

    @Rule
    public MongoDbRule mongoDbRule = MongoDbRule.MongoDbRuleBuilder.newMongoDbRule().defaultSpringMongoDb("daikonkafka");

    /**
     *
     * nosql-unit requirement
     *
     */
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @After
    public void tearDown() {
        cleanDb();
    }

    /**
     * Method which is going to remove all data
     * from the collection in database
     */
    protected void cleanDb() {
        Set<String> collectionsName = mongoTemplate.getCollectionNames();
        for (String collectionName : collectionsName) {
            if (!collectionName.contains("system.indexes"))
                mongoTemplate.remove(new Query(), collectionName);
        }
    }

    @Configuration
    @EnableMongoRepositories(basePackages = "org.talend.daikon.kafka.mongo")
    @ComponentScan(basePackages = "org.talend.daikon.kafka.mongo")
    @EnableMongoAuditing
    static public class MongoTestConfiguration extends AbstractMongoConfiguration {

        private static String MONGO_DB = "daikonkafka";

        @Override
        @Bean
        public Mongo mongo() throws Exception {
            return new Fongo(MONGO_DB).getMongo();
        }

        @Override
        protected String getDatabaseName() {
            return MONGO_DB;
        }
    }

}