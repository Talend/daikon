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
   <version>latest</version>
</dependency>
```
Doing so will bring all needed dependencies.

2. In your configuration, add the following line:
```properties
content-service.store=s3
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

## Additional S3 configuration

### Specify bucket name using path or host style

You can configure S3 client to use "path style" for bucket name instead of host using the `content-service.store.s3.enable_path_style` property. Its default value differs from the authentication mode you use:

| Authentication mode | Use path style as default? |
|---------------------|----------------------------|
| EC2                 | no                         |
| TOKEN               | no                         |
| MINIO               | yes                        |

### S3 client creation
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

You may also use custom mode:
 
```properties
content-service.store.s3.authentication=CUSTOM
```
In this case, a bean of type `org.talend.daikon.content.s3.provider.AmazonS3Provider` is required in class path.

### Region

When code runs in an EC2 context, AWS region is automatically detected. You may still specify/enforce it using an optional property.

```properties
content-service.store.s3.region=eu-west-1
```

### Multi tenancy

You can also enable multi tenancy for S3 (defaults to `false`):
```properties
multi-tenancy.s3.active=true
```

In this case you are required to provide an implementation of `org.talend.daikon.content.s3.provider.S3BucketProvider` that provide the current bucket name.

`S3BucketProvider` needs to implement two methods: one returns the S3 bucket name, the other the root directory for the tenant (might be empty string if all data is to be stored at root level of the S3 bucket).

### Minio support

This module provides support for [Minio](https://www.minio.io). To activate it, simply add:

```properties
content-service.store.s3.authentication=MINIO
content-service.store.s3.endpoint_url=<minio url>
```

Please note in this configuration, the property `content-service.store.s3.endpoint_url` becomes mandatory. In case this property is missing, application context will fail to load with a message asking to provide a value for `content-service.store.s3.endpoint_url`.

### Custom endpoint support

You may customize S3 endpoint URL. To activate it, simply add:

```properties
content-service.store.s3.authentication=CUSTOM
content-service.store.s3.endpoint_url=<endpoint url>
```