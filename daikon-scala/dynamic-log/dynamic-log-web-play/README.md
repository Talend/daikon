# Dynamic log web play

This component is designed to be used by a Play project with injection of a dedicated module.

## Installation

### Dependency

Add the dependency in your sbt project file

`      "org.talend.daikon" %% "dynamic-log-web-play" % "1.0.0-SNAPSHOT"`

### Update configuration

Please include the dynamic.conf file in your configuration file :

`include "dynamiclog.conf"`

### Add routes

Update your routes configuration file to add new routes :

`->      /logger/                        dynamic.Routes`

### Test

You can update the root level with a curl command, for example :

`curl -i -X PUT -H "Accept-Language:en" -H "Content-Type:application/json" -d \ '{}'  'http://localhost:9020/logger/logger-level?level=DEBUG`

Or for a specific class : 

`curl -i -X PUT -H "Accept-Language:en" -H "Content-Type:application/json" -d \ '{}' 'http://localhost:9020/logger/logger-level/org.talend.mypackage.MyClass?level=DEBUG`

Or for a package : 

`curl -i -X PUT -H "Accept-Language:en" -H "Content-Type:application/json" -d \ '{}' 'http://localhost:9020/logger/logger-level/org.talend.mypackage?level=DEBUG`

## License

Copyright (c) 2006-2017 Talend

Licensed under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)
