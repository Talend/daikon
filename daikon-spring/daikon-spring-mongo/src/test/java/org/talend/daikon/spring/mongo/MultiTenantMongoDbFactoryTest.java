package org.talend.daikon.spring.mongo;

import static org.junit.Assert.assertEquals;
import static org.talend.daikon.spring.mongo.TestMultiTenantConfiguration.changeHost;
import static org.talend.daikon.spring.mongo.TestMultiTenantConfiguration.changeTenant;

import java.util.List;
import java.util.Map;

import de.bwaldvogel.mongo.MongoServer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "multi-tenancy.mongodb.active=true")
public class MultiTenantMongoDbFactoryTest extends AbstractMultiTenantMongoDbTest {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private MongoDatabaseFactory mongoDbFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void shouldHaveMultiTenantFactory() {
        assertEquals(MultiTenancyMongoDbFactory.class, mongoDbFactory.getClass());
    }

    @Test
    public void shouldHaveMultiTenantFactoryInTemplate() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("value");

        // When
        mongoTemplate.save(tenant1);
        final List<TestData> all = mongoTemplate.findAll(TestData.class);

        // Then
        assertEquals(1, all.size());
        assertEquals("1", all.get(0).getId());
        assertEquals("value", all.get(0).getValue());
    }

    @Test
    public void shouldNotWriteInOtherTenant() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("value");

        // When
        mongoTemplate.save(tenant1);
        changeTenant("other");
        final List<TestData> all = mongoTemplate.findAll(TestData.class);

        // Then
        assertEquals(0, all.size());
    }

    @Test
    public void shouldWriteInEachTenant() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("tenant1");

        final TestData tenant2 = new TestData();
        tenant2.setId("1");
        tenant2.setValue("tenant2");

        // When
        changeTenant("tenant1");
        mongoTemplate.save(tenant1);
        changeTenant("tenant2");
        mongoTemplate.save(tenant2);

        // Then (tenant 1)
        changeTenant("tenant1");
        final List<TestData> allTenant1 = mongoTemplate.findAll(TestData.class);
        assertEquals(1, allTenant1.size());
        assertEquals("1", allTenant1.get(0).getId());
        assertEquals("tenant1", allTenant1.get(0).getValue());

        // Then (tenant 2)
        changeTenant("tenant2");
        final List<TestData> allTenant2 = mongoTemplate.findAll(TestData.class);
        assertEquals(1, allTenant2.size());
        assertEquals("1", allTenant2.get(0).getId());
        assertEquals("tenant2", allTenant2.get(0).getValue());
    }

    @Test
    public void shouldDealWithRepositories() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("tenant1");

        final TestData tenant2 = new TestData();
        tenant2.setId("1");
        tenant2.setValue("tenant2");

        // When
        changeTenant("tenant1");
        testRepository.insert(tenant1);
        changeTenant("tenant2");
        testRepository.insert(tenant2);

        // Then (tenant 1)
        changeTenant("tenant1");
        final List<TestData> allTenant1 = testRepository.findAll();
        assertEquals(1, allTenant1.size());
        assertEquals("1", allTenant1.get(0).getId());
        assertEquals("tenant1", allTenant1.get(0).getValue());

        // Then (tenant 2)
        changeTenant("tenant2");
        final List<TestData> allTenant2 = testRepository.findAll();
        assertEquals(1, allTenant2.size());
        assertEquals("1", allTenant2.get(0).getId());
        assertEquals("tenant2", allTenant2.get(0).getValue());
    }

    @Test(expected = InvalidDataAccessResourceUsageException.class)
    public void shouldFailOnDatabaseNameProviderFailure() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("tenant1");

        // When
        changeTenant("failure");
        mongoTemplate.insert(tenant1);
    }

    @Test
    public void shouldUseDifferentHosts() {
        // Given
        final TestData tenant1 = new TestData();
        tenant1.setId("1");
        tenant1.setValue("tenant1");

        final TestData tenant2 = new TestData();
        tenant2.setId("1");
        tenant2.setValue("tenant2");

        // When
        changeTenant("tenant1");
        changeHost("tenant1Host");
        testRepository.insert(tenant1);
        changeTenant("tenant2");
        changeHost("tenant2Host");
        testRepository.insert(tenant2);

        // Then
        final Map<TenantInformation, MongoServer> instances = TestMultiTenantConfiguration.getMongoInstances();
        assertEquals(2, instances.size());
    }
}
