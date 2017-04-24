# Log4j, Log4j2 and Logback JSON layout

Provides [logback, Log4j 1&2] layouts to log in JSON format.

Supports both regular _LoggingEvents_ (logged through a `Logger`).

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
* [LoggingEvent Structure Proposal](#loggingevent_structure)
	* [LoggingEvent Structure Questions](#loggingevent_structure_example)

<a name="including"/>
## Including it in your project


Gradle style:

```
compile ("org.talend.daikon.logging:logging-event-layout:0.16.0-SNAPSHOT")
compile ("net.minidev:json-smart:2.2.1")
compile ("commons-lang:commons-lang:2.6")
```

Maven style:

```xml
<dependency>
  	<groupId>org.talend.daikon.logging</groupId>
  	<artifactId>logging-event-layout</artifactId>
  	<version>0.16.0-SNAPSHOT</version>
</dependency>
<dependency>
	<groupId>net.minidev</groupId>
  	<artifactId>json-smart</artifactId>
  	<version>2.2.1</version>
</dependency>
<dependency>
  	<groupId>commons-lang</groupId>
  	<artifactId>commons-lang</artifactId>
  	<version>2.6</version>
</dependency>
```

Older versions than the ones specified in the pom file _might_ work, but the versions in the pom file are what testing has been performed against.

<a name="usage"/>
## Usage

To log using JSON format, you must configure logback, log4j 1&2 to use those layouts provided

The layouts provided by the daikon-logging-event-layout library are as follows:


|Name             | Format        | Protocol   | Function | LoggingEvent 
|-----------------|---------------|------------|----------| ------------
|LogbackLayout    | Logstash JSON | any        | Layout   | [`LogstashLayout`](/src/main/java/org/talend/daikon/logging/event/layout/LogbackJSONLayout.java)
|Log4jLayout      | Logstash JSON | any        | Layout   | [`Log4jJSONLayout`](/src/main/java/org/talend/daikon/logging/event/layout/Log4jJSONLayout.java)
|Log4j2Layout     | Logstash JSON | any        | Layout   | [`Log4j2JSONLayout`](/src/main/java/org/talend/daikon/logging/event/layout/Log4j2JSONLayout.java)

These layouts can generally be used by any logback and log4j appender (such as `RollingFileAppender`).

The general _composite_ JSON layouts can be used to
output any JSON format/data by configuring them with various JSON _providers_.
The daikon logging layouts are really just extensions of the general
composite JSON layouts with a pre-defined set of providers.

<a name="layout"/>
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
        <param name="UserFields" value="" />
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
            <param name="UserFields" value="" />
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
            <KeyValuePair key="" value=""/>
            <KeyValuePair key="" value=""/>
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

<a name="loggingevent_fields"/>
## LoggingEvent Fields

The following sections describe the fields included in the JSON output by default for LoggingEvents written by each layout

<a name="loggingevent_standard"/>
### Standard Fields

These fields will appear in every LoggingEvent unless otherwise noted.
The field names listed here are the default field names.

| Field             | Description
|-------------------|------------
| `logTimestamp`    | Time of the log event. (`yyyy-MM-dd'T'HH:mm:ss.SSSZZ`).
| `agentTimestamp`  | Time of the agent that logged the event (can be the same value with logTimestamp). (`yyyy-MM-dd'T'HH:mm:ss.SSSZZ`).
| `@version`        | The format version (e.g. 1) 
| `logMessage`      | Formatted log message of the event 
| `loggerName`      | Name of the logger that logged the event
| `threadName`      | Name of the thread that logged the event
| `severity`        | String name of the level of the event
| `stackTrace`      | (Only if a throwable was logged) The stacktrace of the throwable.  Stackframes are separated by line endings.
| `hostAddress`     | The host address IP
| `hostName`        | The host name
| `exceptionClass`  | (Only if a throwable was logged) Name of the class that logged the exception
| `exceptionMessage`| (OOnly if a throwable was logged) The exception message. 
| `stackTrace`      | (Only if a throwable was logged) The stacktrace of the throwable.  Stackframes are separated by line endings.

If you enable the `locationInfo` and set it to `true` you will have these more following fields

| Field             | Description
|-------------------|------------
| `fileName`        | Name of the file that logged the event
| `className`       | Fully qualified class name of the class that logged the event
| `methodName`      | Name of the method that logged the event
| `lineNumber`      | ine number of the file where the event was logged




<a name="loggingevent_mdc"/>
### MDC fields

By default, each entry in the Mapped Diagnostic Context (MDC) (`org.slf4j.MDC`)
will appear as a field in the LoggingEvent.

Each entry in the Mapped Diagnostic Context (MDC) will appear as a field in the log file in the form of comma-separated "key":"value"

<a name="loggingevent_custom"/>
### Custom Fields

In addition to the fields above, you can add other fields to the LoggingEvent either globally, or on an event-by-event basis.

<a name="loggingevent_custom_global"/>
#### Global Custom Fields

Add custom fields that will appear in every LoggingEvent like this : 

Log4j config

```xml
<layout class="org.talend.daikon.logging.event.layout.Log4jJSONLayout">
  <param name="UserFields" value="Application_NAME:APP_NAME,Application_ID:APP_ID" />
</layout>
```
Logback config
 
```xml
<layout class="org.talend.daikon.logging.event.layout.LogbackJSONLayout">
  <param name="UserFields" value="Application_NAME:APP_NAME,Application_ID:APP_ID" />
</layout>
```
Log4j2  config

```xml
<Log4j2JSONLayout charset="UTF-8" skipJsonEscapeSubLayout="true" subLayoutAsElement="true">
    <KeyValuePair key="Application_NAME" value="APP_NAME"/>
    <KeyValuePair key="Application_ID" value="IAM_ID"/>
</Log4j2JSONLayout>
```
In these  examples we will have in log file these json : 

```
{
  "Application_NAME":"APP_NAME",
  "Application_ID":"APP_ID"
}
```

<a name="spring_cloud_sleuth"/>
#### Log correlation (Spring Cloud Sleuth)

We use Spring Cloud Sleuth (https://cloud.spring.io/spring-cloud-sleuth/spring-cloud-sleuth.html) for logs correaltion, the SLF4J MDC is always set and users will immediately see the trace and span ids in logs. 

By default, each entry in the Mapped Diagnostic Context (MDC) will appear as a field in the log file in the form of comma-separated "key":"value"

If you enable Spring Cloud Sleuth in your application, you will have these additional fields in your logs

| Field             | Description
|-------------------|------------
| `service`         |  The name of the application that logged the span
| `spanId`          |  The id of a specific operation that took place
| `traceId`         |  The id of the latency graph that contains the span
| `exportable`      |  Whether the log should be exported to Zipkin or not. When would you like the span not to be exportable? In the case in which you want to wrap some operation in a Span and have it written to the logs only.

<a name="including_spring_cloud_sleuth"/>
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


<a name="loggingevent_structure"/>
#### LoggingEvent Structure Proposal

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
   "service":"The name of the application that logged the span",
   "spanId":"The id of a specific operation that took place",
   "traceId":"The id of the latency graph that contains the span",
   "exportable":"Whether the log should be exported to Zipkin or not",
   "logTimestamp":"2016-12-13T13:33:39.934Z",
   "agentId":"tenantId",
   "audit":"false",
   "customInfo":{
	   "my.custom.data":"my custom test data",
	   "exceutionId":"9454554dfc-1024585-410c0-d464124
  	 }
}
```

<a name="loggingevent_structure_example"/>
#### LoggingEvent Structure Questions

1. Do we need these following fields currently used in ESB ?

| Field             | Description
|-------------------|------------
| `eventUUID`       | The unique id of every log event. Its a UUID, the appenders should just generate a UUID and assign to this attribute
| `category`        |  ??
| `eventType`       | The attribute which allows to group similar types of log events
| `agentId`         | The tenantId(accountId)
| `audit`           | Is a flag, if this log event is needed to be saved for auditing purpose
| `exceutionId`     | The job execution id
| `processId`       | The id of the jvm process

2. For these following fields can we use MDC to set them ?

| Field             | Description
|-------------------|------------ 
| `agentId`         |  The tenant ID   
| `exceutionId`     |  The job exection ID

3. Do you see some fields missing here that cannot set with MDC ?

4. On MDC fields, is it preferable to use the same keys for all applications? Can we provide a java class that contains all MDC keys allowed?
