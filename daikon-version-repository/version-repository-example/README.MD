
Version storage - Example
===

A simple example repository for `Customer` instances.

Prerequisites
---
**MongoDB** You need a MongoDB installed and running.

**Java** You need Java > 8.

Build and run
---
To build the example, simply use Maven:
```bash
mvn clean install
```

Then run the sample server
```bash
java -jar target/version-repository-example-0.16.0-SNAPSHOT.jar
```

By default it will try to connect to MongoDB on `mongo://localhost:27017` and start server on `http://localhost:8080`.
You can use `mongodb.host` and `mongodb.port` to customize MongoDB server and `server.port` to customize HTTP server port as follow:
```bash
java -Dserver.port=9999 \
-Dmongodb.host=localhost \
-Dmongodb.port=27017 \
-jar target/version-repository-example-0.16.0-SNAPSHOT.jar
```

Usage
---
Open your favorite browser and go to [http://localhost:8080/docs]().

How is it done?
---

**Define service API**

Same as for all Daikon services you need to define a service API. You may define your own but to save time and expose all persistence related operations, you may simply use:

```java
import org.talend.daikon.annotation.Service;
import org.talend.daikon.version.service.RepositoryService;

@Service(name = "CustomerRepository")
public interface CustomerRepository extends RepositoryService<Customer> {
    // Nothing to implement here
}
```

By doing so, you already expose a service with all persistence *and* versions.

**Define service implementation**

```java
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.version.service.RepositoryServiceImplementation;
import org.talend.services.Customer;
import org.talend.services.CustomerRepository;

@ServiceImplementation
public class CustomerRepositoryImpl extends RepositoryServiceImplementation<Customer> implements CustomerRepository {
    // Nothing to implement here
}
```

**Configuration**

Once service and implementation are done, you need to configure how version will be handled. A short configuration is:

```java
import static org.talend.daikon.version.VersionedRepositoryConfiguration.versionedItem;

class Configuration {
    
    @Bean
    public VersionedRepository<Customer> versionedRepository(Journal journal, ModificationsProvider modificationsProvider, CrudRepository<Customer, String> storage) {
        return versionedItem(Customer.class) // (1)
                .journal(journal) // (2)
                .modificationProvider(modificationsProvider) // (3)
                .storage(storage) // (4)
                .build();
    }
}
```

Details:
* **1** Declare the versioned item class (here `Customer`).
* **2** Declare the implementation of `Journal` to be used. This is where all events that happens to your data will be stored.
* **3** (optional) `ModificationsProvider`. You may omit this, it will default to `JaversModificationsProvider`.
* **4** Declare where the item will be stored (the actual data, not the event journal).