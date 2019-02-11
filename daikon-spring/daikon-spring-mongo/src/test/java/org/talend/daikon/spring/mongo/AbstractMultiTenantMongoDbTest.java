package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.talend.daikon.spring.mongo.TestMultiTenantConfiguration.changeTenant;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories
@ComponentScan("org.talend.daikon.spring.mongo")
@ContextConfiguration(classes = MultiTenantMongoDbFactoryTest.class)
public abstract class AbstractMultiTenantMongoDbTest {

    @Autowired
    private MongoServer mongoServer;

    @Autowired
    private MongoClientProvider mongoClientProvider;

    @Autowired
    private TenantInformationProvider tenantInformationProvider;

    @Before
    public void tearDown() throws IOException {
        // Drop all created databases during test
        MongoClient client = new MongoClient(new ServerAddress(mongoServer.getLocalAddress()));
        for (String database : client.listDatabaseNames()) {
            client.dropDatabase(database);
        }
        // Switch back to default tenant
        changeTenant("default");
        // Clean up mongo clients
        mongoClientProvider.close();
    }
}
