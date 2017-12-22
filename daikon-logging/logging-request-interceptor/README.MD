= Talend Request Interceptor

#### Contents:

* [Including it in your project](#including)
* [Usage](#usage)

<a name="including"/></a>
## Including it in your project


Gradle style:

```
compile ("org.talend.daikon:logging-request-interceptor:0.20.0-SNAPSHOT")
```

Maven style:

```xml
<dependency>
  	<groupId>org.talend.daikon</groupId>
  	<artifactId>logging-request-interceptor</artifactId>
  	<version>0.20.0-SNAPSHOT</version>
</dependency>
```


<a name="usage"/></a>
## Usage

To log outboud request when you are using RestTeplate or Feign you have to add this interceptor


==== RestTemplate

[source,java]
----
@Configuration
public class MyConfigService {

    @Autowired
    private RestLogRepository restLogRepository;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rest = new RestTemplate();
		...............................
		
        // add the interceptor that will handle logging
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new TalendRequestInterceptor(restLogRepository));
        rest.setInterceptors(interceptors);

        return rest;
    }
 
}   
.......................................

----

==== Feign

[source,java]
----
MyInterface api = new MyInterfaceBuilder()
        .requestInterceptor(new TalendRequestInterceptor())
        
----

application.yml https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html[Spring Cloud Doc]
----
feign:
  client:
    config:
      feignName:
        requestInterceptors:
          - org.talend.daikon.logging.TalendRequestInterceptor
----
