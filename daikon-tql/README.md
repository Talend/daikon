# Talend Daikon TQL

## Description

TQL stands for Talend Query Language.

## Contents

This folder contains:

_Modules_                                               |_Description_
--------------------------------------------------------|------------------------------------------
[daikon-tql-bean](daikon-tql-bean)                      |*Talend Query Language Java visitor*
[daikon-tql-client](daikon-tql-client)                  |*Talend Query Language JavaScript client to generate TQL from plain object*
[daikon-tql-core](daikon-tql-core)                      |*Talend Query Language Java core and its generated JavaScript grammar and lexers*
[daikon-tql-mongo](daikon-tql-mongo)                    |*Talend Query Language MongoDB implementation*


## Release TQL Javascript modules

### Build

If TQL java classes are modified, you must generate TQL client in order to get related grammar.

```bash
$> mvn install -Pjavascript
```

### Publish

To publish on npm repository the TQL client and generated grammar, you must be authorized.

```bash
$> yarn  publish
```

## Support

You can ask for help on our [forum](https://community.talend.com/).


## Contributing

We welcome contributions of all kinds from anyone.

Using the [Talend bugtracker](https://jira.talendforge.org/projects/TDKN) is the best channel for bug reports and feature requests. Use [GitHub](https://github.com/Talend/daikon) to submit pull requests.

For code formatting, please use the configuration file and setup for Eclipse or IntelliJ that you find here: https://github.com/Talend/tools/tree/master/tools-java-formatter


## License

Copyright (c) 2006-2018 Talend

Licensed under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)