# Daikon scala libraries

This module contains helpful common Scala code and libraries used at Talend

## scala-parent: Scala maven integration

The scala-parent module is a pom artifact containing common Maven configuration for Scala.

It contains appropriate Scala compiler setup and base dependencies.

In order to create a new Scala maven module, simply use the following Maven pom template:

```
<?xml version="1.0"
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.talend.daikon.scala</groupId>
        <artifactId>scala-parent</artifactId>
        <version>0.24.0-SNAPSHOT</version>
        <relativePath>../scala-parent</relativePath>
    </parent>
    [...]
</project>
```

It will compile Scala code located in src/main/scala and Scala tests located in src/test/scala

It configures the Maven surefire plugin to run *Spec classes (specs2 style). This configuration requires Specification classes are annotated with

```
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MyClassSpec extends Specification
```

It uses Scala 2.11 and defines the `scala-binary-version` Maven property to refer to the `2.11` constant later in artifactId

## scala-play2-dependencies: Play2 and akka dependency management

The scala-play2-dependencies module is a pom artifact defining the required dependencies to run a web app using the [Play2 Framework](https://www.playframework.com/) with akka and [Slick](http://slick.lightbend.com/).

It enherits from scala-parent (see above).

To use this module simply import it as follows in a Maven project:

```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.talend.daikon.scala</groupId>
            <artifactId>scala-play2-dependencies</artifactId>
            <version>0.24.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## multi-tenancy-jdbc

Useful classes to implement per-tenant data isolation with akka persistence JDBC and slick.

Tenant specific configuration is resolved at runtime when a command is executed. It assumes the current tenant identifier can be extracted from the command being processed.

Configuration retrieval is delegated to an implementation of `org.talend.common.configuration.ConfigurationService`.

### akka-persistence configuration

To see how to use it, refer to the example code: [ExampleEntitySupervisor.scala](src/test/scala/org/talend/common/actor/ExampleEntitySupervisor.scala) and the associated specification [MultiTenantEntitySupervisorSpec.scala](src/test/scala/org/talend/common/actor/MultiTenantEntitySupervisorSpec.scala)

Explanation:

1) ExampleEntitySupervisor.scala  declare a supervisor for the persistent entity ExampleEntity. This supervisor implements the `org.talend.common.actor.MultiTenantEntitySupervisor` trait. It defines how the current tenant can be extracted from a message this actor receives by its `extractTenantIdFrom` method. It defines how the delegate ExampleEntity actor properties are built by its `childProps` method. 

2) The supervisor requires an implementation of `ConfigurationService` to retrieve tenant-specific confirguration. This configuration must be 

```
database {
    key1 : value1,
    key2 : value2
    ...
}
```

and it will be used for both journal and snapshot stores configurations

3) it merges tenant-specific configuration with application default configuration such as

```
jdbc-journal {
    common-key1 : common-value1,
    common-key2 : common-value2,
    ...
}

jdbc-snapshot-store {
    common-key3 : common-value3,
    common-key4 : common-value4,
    ...
}
```

### direct slick configuration

When slick is used directly (outside of akka persistence) and the same tenant isolation logic must be required, the `MultiTenantDatabaseConfigProvider` can be used to retrieve a tenant database configuration at runtime.

This provider delegates to 

