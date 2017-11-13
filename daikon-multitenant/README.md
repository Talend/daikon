# Daikon Multitenancy management

The purpose of this module is to provide a common framework to implement multi-tenant applications.

## Core framework

The [multitenant-core](multitenant-core) module contains the base framework. It has no external dependency.

The main entry point of the framework is [TenancyContextHolder](multitenant-core/src/main/java/org/talend/daikon/multitenant/context/TenancyContextHolder.java)
which is responsible to access / set the current tenant.

A tenant is represented as a [Tenant](multitenant-core/src/main/java/org/talend/daikon/multitenant/core/Tenant.java) - a simple POJO with an identity.

The way the current tenant is stored is delegated by the TenancyContextHolder by a [strategy](multitenant-core/src/main/java/org/talend/daikon/multitenant/context/TenancyContextHolderStrategy.java).
If no strategy is defined, the default one will rely on a ThreadLocal to store the current tenant.

To set the current tenant:

```
Tenant tenant = ...;
TenancyContext context = new DefaultTenancyContext(tenant);
TenancyContextHolder.setContext(context); 
```

To get the current tenant:

```
TenancyContextHolder.getContext().getTenant();
```

To clear the current tenant:

```
TenancyContextHolder.clearContext();
```

## Spring implementations

### Spring Web helpers

The [multitenant-spring-web](multitenant-spring-web) module is a Spring boot auto-configuration module. 

It requires 2 beans to be present in the current Spring context:
- a [TenantProvider](multitenant-core/src/main/java/org/talend/daikon/multitenant/provider/TenantProvider.java) implementation
- a [TenantIdentificationStrategy](multitenant-spring-web/src/main/java/org/talend/daikon/multitenant/web/TenantIdentificationStrategy.java) implementation.

At application startup, the module will setup 2 Web filters on the application in case it is a Spring Web application:
 
- [TenancyContextIntegrationFilter](multitenant-spring-web/src/main/java/org/talend/daikon/multitenant/web/TenancyContextIntegrationFilter.java) will manage HTTP synchronous calls
- [TenancyWebAsyncFilter](multitenant-spring-web/src/main/java/org/talend/daikon/multitenant/web/TenancyWebAsyncFilter.java) will manage HTTP asynchronous calls.

Both filters will extract the current tenant from the request through the configured TenantIdentificationStrategy and will initialize the TenantContextHolder accordingly
 so that the current tenant can be accessed later when processing the request.
 
 
### Spring Async helpers

The [multitenant-spring-async](multitenant-spring-async) module is a Spring boot auto-configuration module.

It registers a org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor implementation that will propagate the following information when a task is executed:
- current context (accessible through the TenancyContextHolder)
- spring security context (accessible through the SecurityContextHolder)
- HTTP request attributes (accessible through the RequestContextHolder)
