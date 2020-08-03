# Content service
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains the source files for a generic content service with multiple file system storages. The currently supported file systems are:
* Local file system
* Amazon S3

## Folders description
| _Module_                                           | _Description_                                                        |
|:---------------------------------------------------|------------------------------------------|
| [content-service-common](content-service-common)   | *Generic content service description*    |
| [local-content-service](local-content-service)     | *Adds local file system capabilities*    |
| [s3-content-service](s3-content-service)           | *Adds S3 file system capabilities*       |

## Integration with existing monitoring
Implementations of content service uses `@Timed` to track time spent in operations.

Make sure to include `daikon-spring-metrics` in your dependencies to integrate with Spring Boot Actuator and tracing system.

## Usage

To use local file system, see [local-content-service](local-content-service) for configuration.

To use S3, see [s3-content-service](s3-content-service) for configuration.