Service common
==============

This module provides common service utilities such as:
* Access to other service (using either REST if remote of direct call if present in classpath).
* Documentation generation using Enunciate (http://enunciate.webcohesion.com/)

The following sections will guide you through all features using a sample service creation.

Create a new service module
------------------------------------
To create a new service you may simply declare *service-parent* as your parent:

    <?xml version="1.0"?>
    <project>
      <modelVersion>4.0.0</modelVersion>
      <parent>
          <groupId>org.talend.daikon</groupId>
          <artifactId>service-parent</artifactId>
          <version>0.16.0-SNAPSHOT</version>
          <relativePath/> <!-- look in storage -->
      </parent>
      <!-- omitted content -->
    </project>

Doing so already provides you:
* Code to help you access other services.
* Enunciate documentation generation.

Then write your service class:

    package org.talend.services.sayhi;

    /**
     * A service that says 'hi'. You may also include any HTML tag (img, b, li...).
     */
    @Service
    public interface SayHiService {

      /**
       * Say hi operation
       * @returns "hello"
       */
      @RequestMapping(path = "/say/hi", method = GET)
      String sayHi();
    }

**Important** This interface must be declared in a package `org.talend.services` (or a sub package of this).

Javadoc is used by Enunciate to generate the documentation of the service. The next step is to create the implementation of the service:

    package org.talend.sayhi.implementation;

    @ServiceImplementation
    public class SayHiServiceImpl implements SayHiService {

      @Override
      public String sayHi() {
        return "hello";
      }

    }

Unlike the interface previously created, the implementation can be in any package.
At this point, you can already run:

    $ mvn -Dserver.port=8080 spring-boot:run

This will start a Tomcat server with port 8080. Go to http://localhost:8080/docs and you will see the documentation of the service and a link to Swagger where you can try your service.

Service access with client service
----------------------------------

You first need a dependency to the service you wish to use. Add a dependency to the other service in the pom.xml:

    <dependencies>
        <dependency>
            <groupId>welcome-message-services</groupId>
            <artifactId>say-hi-service</artifactId>
            <version>1.0</version>
            <classifier>service</classifier>
        </dependency>
    </dependencies>

When a service is created using `service-parent` an artifact is created with the content of `org.talend.services.**` which contains all needed classes for communication with the service. So adding this dependency enables your module to use all services defined in `welcome-message-services:say-hi-service`. 

The entry point to create a service client is the `org.talend.daikon.client.ClientService` class, you may get using a `@Autowired` field.

    @Component
    public class MyClass {

      @Autowired
      private ClientService clientService;

      public void callTheOtherService() {
        SayHiService sayHiService = clientService.of(SayHiService.class);
        System.out.println("Service says: " + sayHiService.sayHi());
      }
    }

The first thing to notice you have no idea whether the `SayHiService` is a remote service accessed using REST or a local bean.
You may want to customize (and even prioritize) which kind of access you prefer, in this case you may do

    clientService.of(SayHiService.class, Access.LOCAL, Access.REMOTE)

This call to `of(...)` will *first* try to look for local implementation and if it fails return a remote client.

Service access with client injection
------------------------------------

If you don't feel like adding an autowired field with `ClientService`, you have an alternative.

  @Component
  public class MyClass {

    @Client
    private SayHiService sayHiService;

    public void callTheOtherService() {
      System.out.println("Service says: " + sayHiService.sayHi());
    }
  }

In this case the field `sayHiService` will receive the result of `clientService.of(SayHiService.class)`.
Same as for `ClientService.of(...)`, you can specify preferred accesses.

    @Component
    public class MyClass {

      @Client(access = REMOTE, LOCAL)
      private SayHiService sayHiService;

      public void callTheOtherService() {
        System.out.println("Service says: " + sayHiService.sayHi());
      }
    }


Gateway service / Forward operations
------------------------------------

You sometimes need to 'forward' the call you get to other service without making any change to the request.
Consider the following interface:

    /**
     * Remember Javadoc matters!
     */
    @Service
    public interface GatewayService {

        /**
         * This operation gives a welcome message.
         * @return A welcome message.
         */
        @RequestMapping(path = "/forward/hi", method = GET)
        String forwardThis();

    }

And then the implementation:

    @ServiceImplementation
    public class GatewayServiceImpl {

        @Override
        @Call(service = SayHiService.class, operation="sayHi")
        public native String forwardThis();

    }

This implementation is a short version for:

    @ServiceImplementation
    public class GatewayServiceImpl {

        @Autowired
        ClientService clientService;

        @Override
        public String forwardThis() {
          return clientService.of(SayHiService.class).sayHi();
        }

    }

The @Call annotation takes care of the invocation for you (even the parameters).

What happens when a GET request reaches "/forward/sayHi"? The @Call annotation intercepts the call and look for `SayHiService`. Since service is implemented in same project, it will invoke directly `SayHiServiceImpl` (local call). If `SayHiServiceImpl` was not present in classpath, a remote client would then be used.

**Note #1/3** The 'native' keyword allows you to implement the method without specifying the body. The @Call annotation ignores the whole body declared in the method, adding the 'native' keyword allows to implement the method without a `return null` body.

**Note #2/3** If a method is annotated with @Call but does not have the 'native' modifier, a warning is written to log.

**Note #3/3** In case you want to customize the call to another service, you can use `@Call(using = Class<? extends HystrixCommand>)`, when `using=...` is present, @Call will execute the command instead of the service.

Custom Spring Boot Application
------------------------------

The `service-common` module comes with a default Spring Boot Application (`org.talend.Applcation`). However using it is *not* mandatory.
To enable the feature, you need to use a couple of annotations:
* @EnableServices: enables the client invocation mechanism.
* @EnableEnunciate: enables the documentation for the service.

The Spring Application may look like:

    @SpringBootApplication
    @EnableServices // Enable client invocation for current Spring context
    @EnableEnunciate // Enable documentation for current Spring context
    public class Application {
    
        public static void main(String[] args) { //NOSONAR
            SpringApplication.run(Application.class, args); //NOSONAR
        }
    }
