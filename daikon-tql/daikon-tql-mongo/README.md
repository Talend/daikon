# Talend Daikon TQL Mongo

## Description

Provided a TQL filter, this module computes a mongodb compliant Criteria.

The principle is to parse the AST generated from the TQL, using the [ASTVisitor](daikon-tql-mongo/src/main/java/org/talend/tqlmongo/ASTVisitor.java)
which implements the [IASTVisitor](../daikon-tql-core/src/main/java/org/talend/tql/visitor/IASTVisitor.java) interface from the [daikon-tql-core](../daikon-tql-core)  module.

It is also possible to extend this implementation for more specifc needs.

## Limitations

### wordComplies

As mongodb doesn't support regex search on integer fields, the TQL filters using `wordComplies` operator on integer 
fields are not supported.

The criteria won't then return any result in this case.

Note that a workaround has been found for `complies` operator, but could not be used for `wordComplies` operator as 
used regular expressions are quite more complicated in this case.

## License

Copyright (c) 2006-2018 Talend