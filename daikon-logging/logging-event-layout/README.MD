# Log4j, Log4j2 and Logback JSON layout

Provides [logback, Log4j 1&2] layouts to log in JSON format.

Supports regular _LoggingEvents_ (logged through a `Logger`).

#### Contents:

* [Including it in your project](#including)
* [Usage](#usage)
  * [Layouts](#layout)
* [LoggingEvent Fields](#loggingevent_fields)
  * [Standard Fields](#loggingevent_standard)
  * [MDC fields](#loggingevent_mdc)
  * [Context fields](#loggingevent_context)
  * [Caller Info Fields](#loggingevent_caller)
  * [Custom Fields](#loggingevent_custom)
    * [Global Custom Fields](#loggingevent_custom_global)
  * [Log correlation (Spring Cloud Sleuth)](#spring_cloud_sleuth)
    * [Including Spring Cloud Sleuth in your project](#including_spring_cloud_sleuth)
* [Tracking user activity ids using Slf4j](#correlation_id)
* [LoggingEvent Structure Proposal](#loggingevent_structure)
	* [LoggingEvent Structure MDC](#loggingevent_structure_example)

<a name="including"/></a>
## Including it in your project


Gradle style:

```
compile ("org.talend.daikon:logging-event-layout:LATEST")
```

Maven style:

```xml
<dependency>
  	<groupId>org.talend.daikon</groupId>
  	<artifactId>logging-event-layout</artifactId>
  	<version>LATEST</version>
</dependency>
```

Older versions than the ones specified in the pom file _might_ work, but the versions in the pom file are what testing has been performed against.

<a name="usage"/></a>
## Usage

To log using JSON format, you must configure logback, log4j 1&2 to use those layouts provided

The layouts provided by the daikon-logging-event-layout library are as follows:


|Name             | Format        | Protocol   | Function | LoggingEvent 
|-----------------|---------------|------------|----------| ------------
|Logback Layout   | Logstash JSON | any        | Layout   | [`LogstashLayout`](/src/main/java/org/talend/daikon/logging/event/layout/LogbackJSONLayout.java)
|Log4j Layout     | Logstash JSON | any        | Layout   | [`Log4jJSONLayout`](/src/main/java/org/talend/daikon/logging/event/layout/Log4jJSONLayout.java)
|Log4j2 Layout    | Logstash JSON | any        | Layout   | [`Log4j2JSONLayout`](/src/main/java/org/talend/daikon/logging/event/layout/Log4j2JSONLayout.java)

These layouts can generally be used by any logback and log4j appender (such as `RollingFileAppender`).

The general _composite_ JSON layouts can be used to
output any JSON format/data by configuring them with various JSON _providers_.
The daikon logging layouts are really just extensions of the general
composite JSON layouts with a pre-defined set of providers.

<a name="layout"/></a>
### Layouts

You can use any of the layouts provided with other logback and log4j appenders.

For example, to output LoggingEvents to a file, use the appropriate Lyout
with the `RollingFileAppender` in your `logback.xml`, `log4j.xml` or `log4j2.xml` like this:


#### Logback configuration

```xml
<configuration>
 <appender name="File" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_PATH}/application.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>${LOG_PATH}/application.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <layout class="org.talend.daikon.logging.event.layout.LogbackJSONLayout">
        <param name="UserFields" value="service:dataset,application:tdp" />
    </layout>
 </appender>
  <root level="INFO">
     <appender-ref ref="File" />
  </root>
</configuration>
```
#### Log4j configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration debug="true">
    <appender name="File" class="org.apache.log4j.RollingFileAppender">
        <param name="File" value="${LOG_PATH}/application.log" />
        <param name="Append" value="true" />
        <param name="Threshold" value="INFO" />
        <param name="MaxFileSize" value="50MB" />
        <param name="MaxBackupIndex" value="20" />
        <param name="encoding" value="UTF-8" />
        <layout class="org.talend.daikon.logging.event.layout.Log4jJSONLayout">
            <param name="UserFields" value="service:dataset,application:tdp" />
        </layout>
    </appender>   
    <root> 
        <level value="INFO" />
        <appender-ref ref="File" />
    </root>
</log4j:configuration>
```

#### Log4j2 configuration :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="DEBUG" packages="org.talend.daikon.logging.event.layout" verbose="false">
  <appenders>
    <RollingFile name="File" fileName="${LOG_PATH}/application.log"
        filePattern="${LOG_PATH}/application-%d{yyyy-MM-dd}.log">
        <Log4j2JSONLayout charset="UTF-8" skipJsonEscapeSubLayout="true" subLayoutAsElement="true">
            <KeyValuePair key="service" value="dataset"/>
            <KeyValuePair key="application" value="tdp"/>
        </Log4j2JSONLayout>
        <Policies>
          <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
        </Policies>
    </RollingFile>
  </appenders>
  <loggers>
    <root level="DEBUG">
      <appender-ref ref="File"/>
    </root>
  </loggers>
</configuration>
```

<a name="loggingevent_fields"/></a>
## LoggingEvent Fields

The following sections describe the fields included in the JSON output by default for LoggingEvents written by each layout

<a name="loggingevent_standard"/></a>
### Standard Fields

These fields will appear in every LoggingEvent unless otherwise noted.
The field names listed here are the default field names.

| Field             | Description
|-------------------|------------
| `eventUUID`       | The unique id of every log event. Its a UUID, the appenders should just generate a UUID and assign to this attribute
| `logTimestamp`    | Time of the log event. (`yyyy-MM-dd'T'HH:mm:ss.SSSZZ`).
| `agentTimestamp`  | Time of the agent that logged the event (can be the same value with logTimestamp). (`yyyy-MM-dd'T'HH:mm:ss.SSSZZ`).
| `serverTimestamp`  | Time of the logstash sever when event arrive.
| `@version`        | The format version (e.g. 1) 
| `logger.name`     | Name of the logger that logged the event
| `logMessage`      | Formatted log message of the event
| `threadName`      | Name of the thread that logged the event
| `severity`        | String name of the level of the event
| `stackTrace`      | (Only if a throwable was logged) The stacktrace of the throwable.  Stackframes are separated by line endings.
| `host.address`    | The host address IP (if host info is enabled)
| `host.name`       | The host name (if host info is enabled)
| `exceptionClass`  | (Only if a throwable was logged) Name of the class that logged the exception
| `exceptionMessage`| (OOnly if a throwable was logged) The exception message. 
| `stackTrace`      | (Only if a throwable was logged) The stacktrace of the throwable.  Stackframes are separated by line endings.
| `correaltionId`   | The correlationd ids if it's enabled [Tracking user activity ids using Slf4j](#correlation_id)

<a name="LocationInfo"/></a>
If you set `LocationInfo` property  to `true` in your layout log4j/logback configuration you will have these more following fields

| Field             | Description
|-------------------|------------
| `file.name`       | Name of the file that logged the event
| `class.name`      | Fully qualified class name of the class that logged the event
| `method.name`     | Name of the method that logged the event
| `line.number`     | Line number of the file where the event was logged
| `process.id`      | The id of the jvm process

<a name="HostInfo"/></a>
If you set `HostInfo` property  to `false` in your layout log4j/logback configuration the host information will **not** be displayed.
If log layout is used to collect customer information, it is **required** to disable host information in log events.

<a name="loggingevent_mdc"/></a>
### MDC fields

By default, each entry in the Mapped Diagnostic Context (MDC) (`org.slf4j.MDC`)
will appear as a field in the LoggingEvent.

Each entry in the Mapped Diagnostic Context (MDC) will appear as a field in the log file in the form of comma-separated "key":"value"

We provide a java class constants that contains common MDC keys: `MdcKeys` (please add there all MDC keys you think will be common).

For Spring Boot applications you may just add the following projects in your classpath to benefit of the automatic MDC setup for:
 * for the [userId](src/main/java/org/talend/daikon/logging/event/field/MdcKeys.java) please add [org.talend.daikon:logging-spring-security](../logging-spring-security)maven artifact
 * for the [accountId](src/main/java/org/talend/daikon/logging/event/field/MdcKeys.java) please add [org.talend.daikon:multitenant-spring-web](../../daikon-multitenant/multitenant-spring-web)maven artifact

<a name="loggingevent_custom"/></a>
### Custom Fields

In addition to the fields above, you can add other fields to the LoggingEvent either globally, or on an event-by-event basis.

<a name="loggingevent_custom_global"/></a>
#### Global Custom Fields

In all the Talend services developers should provide 2 custom fields.
* service : this represent the service name and more precisely the releasable entity name as defined in the [Talend Naming Policy] (https://github.com/Talend/policies/blob/master/official/Naming.md#releasable-entity-api-name).
* application : this represent the application or project abbreviation that this service belongs to (like *tdp* for Talend Dataprep), the project name is defined in the [Talend Naming Policy](https://github.com/Talend/policies/blob/master/official/Naming.md#project) in lower case.

Log4j config

```xml
<layout class="org.talend.daikon.logging.event.layout.Log4jJSONLayout">
  <param name="UserFields" value="service:dataset,application:tdp" />
</layout>
```
Logback config
 
```xml
<layout class="org.talend.daikon.logging.event.layout.LogbackJSONLayout">
  <param name="UserFields" value="service:dataset,application:tdp" />
</layout>
```
Log4j2  config

```xml
<Log4j2JSONLayout charset="UTF-8" skipJsonEscapeSubLayout="true" subLayoutAsElement="true">
    <KeyValuePair key="service" value="dataset"/>
    <KeyValuePair key="application" value="tdp"/>
</Log4j2JSONLayout>
```
In these  examples we will have in log file these json : 

```
{
  "service":"dataset",
  "application":"tdp"
}
```

<a name="spring_cloud_sleuth"/></a>
#### Log correlation (Spring Cloud Sleuth)

We use Spring Cloud Sleuth (https://cloud.spring.io/spring-cloud-sleuth/spring-cloud-sleuth.html) for logs correaltion, the SLF4J MDC is always set and users will immediately see the trace and span ids in logs. 

By default, each entry in the Mapped Diagnostic Context (MDC) will appear as a field in the log file in the form of comma-separated "key":"value"

If you enable Spring Cloud Sleuth in your application, you will have these additional fields in your logs

| Field             | Description
|-------------------|------------
| `service`         |  The name of the application that logged the span
| `X-B3-SpanId`     |  The id of a specific operation that took place
| `X-B3-TraceId`    |  The id of the latency graph that contains the span
| `X-Span-Export`   |  Whether the log should be exported to Zipkin or not. When would you like the span not to be exportable? In the case in which you want to wrap some operation in a Span and have it written to the logs only.

<a name="including_spring_cloud_sleuth"/></a>
#### Including Spring Cloud Sleuth in your project

Gradle style:

```
dependencyManagement { 
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:Camden.SR4"
    }
}
 
dependencies { 
    compile "org.springframework.cloud:spring-cloud-starter-sleuth"
}
```

Maven style:

```xml
<dependencyManagement>
         <dependencies>
             <dependency>
                 <groupId>org.springframework.cloud</groupId>
                 <artifactId>spring-cloud-dependencies</artifactId>
                 <version>Camden.SR4</version>
                 <type>pom</type>
                 <scope>import</scope>
             </dependency>
         </dependencies>
   </dependencyManagement>
 
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
   </dependency>
```


<a name="correlation_id"/></a>
## Tracking user activity ids using Slf4j

The objectives are as follows:

1. Ensure that a correlation id is assigned for each transaction (will be generated if not on service request).
If it's on service request then please use this key name: HEADER_REQUEST_CORRELATION_ID provided in MdcKeys.java

2. Ensure that the correlation id is documented on all log messages so that it can be correlated across services.

### Filter configuration
To enable tracking user activity ids in the logs you have to :

1. Configure the Filter in your application

	a. SPA:
	
	Add FilterRegistrationBean bean in your Application.java or Configuration class as this:
	
	```
	@Bean
    public FilterRegistrationBean correlationHeaderFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new RequestUserActivityFilter());
        filterRegBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegBean.setEnabled(Boolean.TRUE);
        filterRegBean.setName("Log Correlation Filter");
        filterRegBean.setAsyncSupported(Boolean.TRUE);
        return filterRegBean;
    }
    ```
    b.  For RESTful or SOAP service requests, this is accomplished by servlet filter.
	
	In your web.xml, filter configuration is as follows:
	
	```xml
	<filter>
		<filter-name>correlationIdFilter</filter-name>
			<filter-class>org.talend.daikon.logging.user.RequestUserActivityFilter</filter-class>
		</filter>
	
		<filter-mapping>
			<url-pattern>/*</url-pattern>
		</filter-mapping>
    </filter>
	  ```
	  
2. Configure your log4j/logback configuration as described here [Layouts](#layout)	  

<a name="loggingevent_structure"/></a>
## LoggingEvent Structure Example

```
{ 
   "eventUUID":"9454554dfc-4545dv-410c0-d464124",
   "correlationId":"someCorrelationId",
   "category":"TestCategory",
   "eventType":"LOGEvent",
   "severity":"INFO",
   "logMessage":"slf4j log message (high)",
   "logSource":{
   	  "class.name":"org.talend.loggenerator.scenarios.high.LogEventHigh",
	  "file.name":"LogEventHigh.java",
	  "host.name":"win75",
	  "line.number":"68",
	  "logger.name":"org.talend.loggenerator.scenarios.high.LogEventHigh",
	  "method.name":"publishLogs",
	  "process.id":"860"
   },
   "service":"From MDC: The name of the application that logged the span",
   "X-B3-SpanId":"From MDC: The id of a specific operation that took place",
   "X-B3-TraceId":"From MDC: The id of the latency graph that contains the span",
   "X-Span-Export":"From MDC: Whether the log should be exported to Zipkin or not",
   "logTimestamp":"2016-12-13T13:33:39.934Z",
   "serverTimestamp":"2016-12-13T13:33:40.934Z",
   "accountId":"tenantId",
   "audit":"false",
   "customInfo":{
	  "my.custom.data":"my custom test data",
  	  "executionId":"9454554dfc-1024585-410c0-d464124,
	  "service":"dataset",
	  "application":"tdp"
    }
}
```

| Field             | Description (These fields are used ESB/TIC right now)
|-------------------|-----------------------------------------------------------
| `category`        | ??
| `eventType`       | The attribute which allows to group similar types of log events
| `audit`           | Is a flag, if this log event is needed to be saved for auditing purpose
