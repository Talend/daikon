# Talend Daikon TQL Mongo

## Description

Provided a TQL filter, this module computes a mongodb compliant Criteria.

It implements the [IASTVisitor](../daikon-tql-core/src/main/java/org/talend/tql/visitor/IASTVisitor.java) interface from the [daikon-tql-core](../daikon-tql-core)  module.

You can also extend its implementation for more specifc needs.

## Limitations

### wordComplies

As mongodb doesn't support regex search on integer fields, the TQL filters using `wordComplies` operator on integer 
fields are not supported.

The criteria won't then return any result in this case.

Note that a workaround has been found for `complies` operator, but could not be used for `wordComplies` operator as 
used regular expressions are quite more complicated in this case.

## License

Copyright (c) 2006-2018 Talend