
Version storage
===

CQRS 
---

CQRS in 2 seconds: separate read queries and write operations. 

*Read operations*

Read operations are domain specific. For example, you don't get a generic "MyBean get()" operation but a set or read operations that corresponds to your application (e.g. "getMyBeanWithAdditionalInformation()").

*Write operations*

Write operations are called "commands" and are by design domain specific. As opposed to a Spring-data way, where code look like (1):


    Repository storage = ...
    MyBean b = storage.get("MyBeanId");
    b.setName("My new name");
    storage.update(b);

A CQRS-like approach is (2):

    Repository storage = ...
    storage.command("MyBeanId", new UpdateMyBeanName("My new name"));

For an application developped using (1) requires bit of work to get to (2).

ES
---

Event sourcing is a pattern where changes to data are stored in 

* A journal: this only stores events (like "MyBean's name was updated from value 'aaaa' to 'bbbb'")
* A 'read' data base: this database is used for querying.

This allows:

* Async updates: writes are only performed on the journal. An asynchronous process reads for events and apply them to the read database.
* Versioning: event log allows to perform rollback and restore.
* High throughput for writes: write only append events to a journal.

How does apply to current Spring projects?
---

Writing a generic CQRS service is quite difficult since CQRS are domain specific. 

  