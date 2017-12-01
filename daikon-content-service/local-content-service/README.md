# Content service - Local file system
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains the source files for a local file system support.

## Usage

1. Include the following dependencies in your module:
```xml
<dependency>
   <groupId>org.talend.daikon</groupId>
   <artifactId>local-content-service</artifactId>
   <version>0.19.0-SNAPSHOT</version>
</dependency>
```
Doing so will bring all needed dependencies.

2. In your configuration, add the following line:
```properties
content-service.store=local
content-service.store.local.path=<path to the local file system>
```

3. Add an `@Autowired` with `org.talend.daikon.content.ResourceResolver` type anywhere you want to use content service:

```java
import org.talend.daikon.content.ResourceResolver;

@Component
public class MyComponent {
    
    @Autowired
    protected ResourceResolver loader;
}
```
