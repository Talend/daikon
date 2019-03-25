# Talend Daikon TQL Client

The goal of this library is to provide a NPM package that helps to convert javascript-style filters to TQL queries.

## Installation


```bash
# yarn
yarn add @talend/daikon-tql-client
```

or

```bash
# npm
npm install @talend/daikon-tql-client --save
```

## Usage

The package exposes a [`Query` class](#queryusage) used to create an instance on which you can chain [operators](#operatorusage) and [compositors](#compositorusage) in the wanted order before serialize it.

Basic example :

```javascript
import { Query } from '@talend/daikon-tql-client';

const query = new Query();

query
	.equal('f1', 76)
	.or()
	.greaterThan('f2', 77);

query.serialize(); // -> '(f1 = 76) or (f2 > 77)'
```


### <a id="queryusage"></a>Query

A Query is a serializable set of operators.\
It lets you stack operators and compositors one after an other by constantly returning the query reference.

```javascript
import { Query } from '@talend/daikon-tql-client';

const query = new Query();

query
	.greaterThan('f2', 42)
	.and()
	.lessThan('f2', 76)
	.or()
	.equal('f2', 777);

query.serialize(); // -> '(f2 > 42) and (f2 < 76) or (f2 = 777)'
```
_Hint: All the [operators](#operatorusage) are accessible via the query instance in lower camel case._

----------

Queries can be nested thanks to the `nest()` method without depth limit :

```javascript
import { Query } from '@talend/daikon-tql-client';

const query = new Query();
const subQuery1 = new Query();
const subQuery2 = new Query();

subQuery1
	.equal('q2f1', 76)
	.or()
	.equal('q2f2', 77);

subQuery2
	.equal('q3f1', 78)
	.and()
	.equal('q3f2', 79);

query
	.greaterThan('f2', 42)
	.and()
	.nest(subQuery1) // <- !
	.and()
	.lessThan('f2', 666)
	.or()
	.nest(subQuery2) // <- !
	.or()
	.equal('f2', 777);

query.serialize();
```

Will produce :

```sql
(f2 > 42)  and (
	(q2f1 = 76)  or  (q2f2 = 77)
) and (f2 < 666)  or  (
	(q3f1 = 78) and (q3f2 = 79)
)  or  (f2 = 777)
```
_Hint: Obviously, priority is conserved on nested queries_


----------

Queries can hold the negation of other queries or operators with the help of the `not()` method :

```javascript
// query negation
import { Query } from '@talend/daikon-tql-client';

const query = new Query();
const subQuery = new Query();

subQuery
	.equal('q2f1', 76)
	.or()
	.equal('q2f2', 77);

query
	.greaterThan('f2', 42)
	.and()
	.not(subQuery) // <- !
	.and()
	.lessThan('f2', 666);

query.serialize(); // -> '(f2 > 42) and not((q2f1 = 76) or (q2f2 = 77)) and (f2 < 666)'
```

```javascript
// operator negation
import { Query, Operators } from '@talend/daikon-tql-client';

const query = new Query();

query
	.equal('f1', 666)
	.or()
	.not(new Operators.Equal('f2', 777));

query.serialize(); // -> '(f1 = 666) or not((f2 = 777))'
```


### <a id="operatorusage"></a>Operator

The following operators are supported :

TQL symbol               |Client class
-------------------------|------------------
`contains`               |`Contains`
`containsIgnoreCase`     |`ContainsIgnoreCase`
`complies`               |`Complies`
`wordComplies`           |`WordComplies`
`is empty`               |`Empty`
`is invalid`             |`Invalid`
`is valid`               |`Valid`
`between`                |`Between`
`quality`                |`Quality`
`=`                      |`Equal`
`!=`                     |`Unequal`
`>`                      |`GreaterThan`
`>=`                     |`GreaterThanOrEqual`
`<`                      |`LessThan`
`<=`                     |`LessThanOrEqual`
`in`                     |`In`

They are accessible via the `Operators` named export and can be serialized to TQL expressions :

```javascript
import { Operators } from '@talend/daikon-tql-client';

const operator = new Operators.GreaterThan('col1', 42);

operator.serialize(); // -> 'col1 > 42'
```


### <a id="compositorusage"></a>Compositor

A Compositor is the only way to join operators in a query.

The following compositors are supported :

- `and`
- `or`

They can be used in the same way as an operator in a query :

```javascript
import { Query } from '@talend/daikon-tql-client';

query
	.equal('f1', 666)
	.or()
	.equal('f2', 777);

query.serialize(); // -> '(f1 = 666) or (f2 = 777)'
```


### Parser

The `Parser` class helps to transform a legacy Javascript-style filters tree to a serializable query :

```javascript
import { Parser } from '@talend/daikon-tql-client';

const query = Parser.parse(myTree);
query.serialize();
```

An example of tree can be found in the [tests](./src/converter/__tests__/parser.spec.js).


## How to create an operator ?

An Operator inherits from the `Operator` class (which "implements" the `ISerializable` interface). All operators are simple Javascript classes which have the `Value` and `HasOperand` properties exported.

To add your own operator, you just have to create a new class under `src/converter/operators/`.

For example, to create a new `toto` operator, create `src/converter/operators/toto.js` :

```javascript
import Operator from './operator';

export default class Toto extends Operator {
	static value = 'is toto';
	static hasOperand = false;
}
```

And export it in `src/converter/operators/index.js` :

```javascript
import Toto from './toto';
// ...

export {
	// ...,
	Toto,
};
```

Don't forget the associated tests ;)

Your new `toto` operator will be automatically available under `Query` :

```javascript
import { Query } from '@talend/daikon-tql-client';

const query = new Query();

query
	.greaterThan('f1', 42)
	.and()
	.toto('f2');

query.serialize(); // -> '(f1 > 42) and (f2 is toto)'
```
