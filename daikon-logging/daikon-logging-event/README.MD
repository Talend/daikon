# Logging (log4j, log4j2 and logback) in JSON to use with logstash

This is a Log4J, Log4J2 and LogBack Layout as a Logstash "json_event". This is a  layout that produces json that is compliant to logstash. JSON produced is a serialization of a given  LogEvent and is intentionally very similar to that produced by the default log4j2 [JSONLayout](http://logging.apache.org/log4j/2.x/manual/layouts.html).

## Dependencies:
Add these dependencies in your project

GRADLE:

```
compile ("org.talend.daikon.logging:daikon-logging-event:0.16.0-SNAPSHOT")
compile ("net.minidev:json-smart:2.2.1")
compile ("commons-lang:commons-lang:2.6")
```

MAVEN:

```
<dependency>
  	<groupId>org.talend.daikon.logging</groupId>
  	<artifactId>daikon-logging-event</artifactId>
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

## Log4j configuration

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
        <layout class="org.talend.daikon.logging.event.Log4jJSONLayout">
            <param name="UserFields" value="" />
        </layout>
    </appender>   
    <root> 
        <level value="INFO" />
        <appender-ref ref="File" />
    </root>
</log4j:configuration>
```

## Log4j2 configuration :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="DEBUG" packages="org.talend.daikon.logging.event" verbose="false">
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

## Logback configuration

```xml
<configuration>
 <appender name="File" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_PATH}/application.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>${LOG_PATH}/application.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <layout class="org.talend.daikon.logging.event.LogbackJSONLayout">
        <param name="UserFields" value="" />
    </layout>
 </appender>
  <root level="INFO">
     <appender-ref ref="File" />
  </root>
</configuration>
```

## MDC fields

By default, each entry in the Mapped Diagnostic Context (MDC) will appear as a field in the log file in the form of comma-separated "key":"value"
 
## Custom User Fields

You can now add your own metadata to the event in the form of comma-separated key:value pairs. This can be set in either the log4j config

### Log4j config

```xml
<layout class="org.talend.daikon.logging.event.Log4jJSONLayout">
  <param name="UserFields" value="Application_NAME:APP_NAME,Application_ID:APP_ID" />
</layout>
```
### Logback config
 
```xml
<layout class="org.talend.daikon.logging.event.LogbackJSONLayout">
  <param name="UserFields" value="Application_NAME:APP_NAME,Application_ID:APP_ID" />
</layout>
```
###  Log4j2  config

```xml
<Log4j2JSONLayout charset="UTF-8" skipJsonEscapeSubLayout="true" subLayoutAsElement="true">
    <KeyValuePair key="Application_NAME" value="APP_NAME"/>
    <KeyValuePair key="Application_ID" value="IAM_ID"/>
</Log4j2JSONLayout>
```
In these  examples we will have in log file these json : {"Application_NAME":"APP_NAME", "Application_ID":"APP_ID"}


## Possible attributes for each log line: 

-logTimestamp

-hostName

-hostAddress

-level

-class

-method

-logMessage

-exceptionClass

-exceptionMessage

-stackTrace

-thread

-loggerName



Log output example:

```
{ 
   "logTimestamp":"2016-12-13T13:33:39.934Z",
   "hostName":"tlnd-sdiallo",
   "hostAddress":"192.168.56.1",
   "level":"INFO",
   "methods="[GET],produces=[application\/json]}\" onto public org.talend.provisioning.core.provisioner.tic.BookkeeperAccount..."
   "logMessage":"Exception in monitor thread while connecting to server localhost:27017", 
   "exceptionClass":"ch.qos.logback.classic.spi.ThrowableProxy",
   "stackTrace":"at com.mongodb.connection.SocketStream.open(SocketStream.java:63)\nat com.mongodb.connection.InternalStreamConnection.open(InternalStreamConnection.java:114)\nat com.mongodb.connection.DefaultServerMonitor$Se"
   "thread":"cluster-ClusterId{value='584ff8b24f57bc2b3426be9b', description='null'}-localhost:27017",
   "loggerName":"org.mongodb.driver.cluster",
   "exceptionMessage":"Exception opening socket"
}
```
