# Content service - Amazon S3
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains the source files for a local file system support.

## Usage

1. Include the following dependencies in your module:
```xml
<dependency>
   <groupId>org.talend.daikon</groupId>
   <artifactId>s3-content-service</artifactId>
   <version>0.16.0-SNAPSHOT</version>
</dependency>
```
Doing so will bring all needed dependencies.

2. In your configuration, add the following line:
```properties
content-service.store=s3
```

3. Add an `@Autowired` with `DeletableResourceLoader` type anywhere you want to use content service:

```java
@Component
public class MyComponent {
    
    @Autowired
    protected DeletableResourceLoader loader;
}
```

## Additional S3 configuration

### Authentication modes
By default, AWS S3 client will authenticate using running EC2 instance credentials. Default behavior is same as:

```properties
content-service.store.s3.authentication=EC2
```

You may choose to use token based authentication. You can do so with:

```properties
content-service.store.s3.authentication=TOKEN
content-service.store.s3.accessKey=<access_key>
content-service.store.s3.secretKey=<secret_key>
```

### Region

When code runs in an EC2 context, AWS region is automatically detected. You may still specify/enforce it using an optional property.

```properties
content-service.store.s3.region=eu-west-1
```