# Daikon Spring multi tenancy support

![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains Spring multi tenancy for Talend products.

## ForAll

The goal of the `ForAll` interface is to abstract tasks to be executed for all tenants, whether code is executed in a context where multi-tenancy is active or not.

### Usage

#### Simple example
Let's start with a simple example:

```java
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.tenancy.ForAll;

@Component
public class MyGlobalTenantClass {
    
    @Autowired
    private ForAll forAll;
    
    public void doSomething() {
        forAll.execute(() -> System.out.println("Doing something for a tenant."));
    }
}
```

By default, the implementation is a `DefaultForAll` that do not perform anything related to tenants.

**Important** Check code in daikon-ee for extension with links to tenants.


#### Precondition

You may **optionally** add a precondition to skip execution if context is not valid for the task you wish to run.
For example:

```java
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.tenancy.ForAll;

@Component
public class MyGlobalTenantClassWithPrecondition {
    
    @Autowired
    private ForAll forAll;
    
    public void doSomething() {
        forAll.execute(
                forAll.condition().operational(this), 
                () -> System.out.println("Doing something for a tenant.")
        );
    }
}
```

The use of `forAll.condition().operational(this)` is not mandatory. The precondition is a `Supplier<Boolean>`.