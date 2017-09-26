Multi tenancy support (for Spring data MongoDB)
================================================

This module provides support for multi tenancy over Spring Data MongoDB:
* Automatically wraps the previously existing MongoDbFactory to support multi-tenancy.
* Allow to have the following information per tenant: database name, credentials and authentication database (MongoDb apparently authorizes to use another database name for authorization).
* Allow to have a MongoDB host per tenant.

Known limitations
-----------------

* You can't change SSL keys per tenant when connecting to MongoDB.

Using multi tenancy support
---------------------------

1) Include artifact in your application:

```xml
    <dependency>
        <groupId>org.talend.daikon</groupId>
        <artifactId>daikon-spring-mongo</artifactId>
        <version>0.16.0-SNAPSHOT</version>
    </dependency>
```

2) Make sure, `org.talend.daikon` in included in the Spring component scan

```java
@ComponentScan(basePackages = { "org.talend.myapp", "org.talend.daikon" })
```

3) Include property in your application configuration:

```properties
multi-tenancy.mongodb.active=true
```
    
Note: property defaults to `false`, so your application configuration to explicitly enable multi tenancy support.

4) Add a `org.talend.daikon.spring.mongo.TenantInformationProvider` implementation in your application and expose it as a Spring Bean. For example:

```java
    @Bean
    public TenantInformationProvider tenantProvider() {
        return new TenantInformationProvider() {
            @Override
            public String getDatabaseName() {
                // Fetch database name
            }

            @Override
            public MongoClientURI getDatabaseURI() {
                // Returns the connection URL (e.g. "mongodb://fake_host:27017/");
            }
        };
    }
```

5) Add a `org.talend.daikon.spring.mongo.MongoClientProvider` implementation in your application and expose it as a Spring Bean. The implementation `org.talend.daikon.spring.mongo.SimpleMongoClientProvider` is available for quick start.
If you need more advanced cache management, consider providing a custom implementation. 

5) On startup of your application, you should see:

```
    09:41:53.236 [main] INFO  o.t.d.s.m.MultiTenancyMongoDbConfiguration - Enabling MongoDB multi tenancy support on 'class org.springframework.data.mongodb.core.SimpleMongoDbFactory' (wrapped bean: defaultMongoDbFactory)...
```

6) And it's done, all the `MongoTemplate` or `MongoRepository` will work with multi tenancy without changing a line of code in them.

Cache of MongoDB clients
------------------------

Be aware the implementation `org.talend.daikon.spring.mongo.SimpleMongoClientProvider` does **not** perform any cache clean up (and may create memory leaks in your application, if you have many tenants).

You may use `org.talend.daikon.spring.mongo.CachedMongoClientProvider` to have a provider implementation that allows cached entry to have expiration dates (i.e. Mongo clients not used after given duration are automatically removed).

How it works
-----------
In `org.talend.daikon.spring.mongo.MultiTenancyMongoDbConfiguration` a `BeanPostProcessor` will wrap existing `MongoDBFactory` and intercept all calls to retrieve a MongoDB database. Information given my implementation of `TenantInformationProvider` do the rest.

Pitfalls
--------

Be aware the implementation of `org.talend.daikon.spring.mongo.TenantInformationProvider` is very frequently called (depending on your usage of MongoDB). Very fast response times all of the methods in implementation is therefore very important.

