package org.talend.daikon.spring.mongo;

import static org.junit.Assert.assertEquals;
import static org.talend.daikon.spring.mongo.TestMultiTenantConfiguration.changeTenant;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "multi-tenancy.mongodb.active=false")
public class DisabledMultiTenantMongoDbTest extends AbstractMultiTenantMongoDbTest {

    @Autowired
    private MongoDatabaseFactory mongoDbFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void shouldHaveMultiTenantFactory() {
        assertEquals(SimpleMongoClientDatabaseFactory.class, mongoDbFactory.getClass());
    }

    @Test
    public void shouldWriteInSameDatabase() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("value");

        // When
        mongoTemplate.save(tenant1);
        changeTenant("other");
        final List<TestData> all = mongoTemplate.findAll(TestData.class);

        // Then
        assertEquals(1, all.size()); // All in same database (tenant name doesn't have impacts).
    }

}
