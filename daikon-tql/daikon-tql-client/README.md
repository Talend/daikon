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

The package exposes a [`Query` class](#queryusage) used to create an instance on which you can chain [operators](#operatorusage) and [compositors](#compositorusage) in the wanted order before [serialize](#serializationusage) it.

Basic example :

```javascript
import { Query } from '@talend/daikon-tql-client';

const query = new Query();

query
	.equal('f1', 76)
	.or()
	.greaterThan('f2', 77);

query.serialize(); // Produce => '(f1 = 76) or (f2 > 77)'
```


### <a id="queryusage"></a>Query

A Query is a serializable set of operators.\
It lets you stack operators and compositors one after an other by constantly returning the query reference.

```javascript
const query = new Query();

query
	.greaterThan('f2', 42)
	.and()
	.lessThan('f2', 76)
	.or()
	.equal('f2', 777);

```
_Hint: All the operators are accessible via the instance in lower camel case._

----------

Queries can be nested thanks to the `nest()` method without depth limit :

```javascript
const query = new Query();
const subQuery = new Query();

subQuery
	.equal('q2f1', 76)
	.or()
	.equal('q2f2', 77);

query
	.greaterThan('f2', 42)
	.and()
	.nest(subQuery) // <- !
	.and()
	.lessThan('f2', 666);
```

----------

Queries can hold the negation of other queries or operators with the help of the `not()` method :

```javascript
// query negation
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

query.serialize(); // Produce => '(f2 > 42) and not((q2f1 = 76) or (q2f2 = 77)) and (f2 < 666)'
```

```javascript
// operator negation
const query = new Query();

query
	.equal('f1', 666)
	.or()
	.not(new Equal('f2', 777));

query.serialize(); // Produce => '(f1 = 666) or not((f2 = 777))'
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
`>`                      |`GreaterThan`
`<`                      |`LessThan`

They are accessible via the `Operators` named export or directly in a [query instance](#queryusage).

```javascript
import { Operators } from '@talend/daikon-tql-client';

const operator = new Operators.Equal('f2', 777);
```

### <a id="compositorusage"></a>Compositor

A Compositor is the only way to join operators in a query.

The following compositors are supported :

- `and`
- `or`

They can be used in the same way as an operator in a query :

```javascript
query.equal('f1', 666).or().equal('f2', 777);
```


### <a id="serializationusage"></a>Serialization

Operators can be serialized to TQL expressions :

```javascript
const operator = new GreaterThan('col1', 42);

operator.serialize(); // -> 'col1 > 42'
```

And queries too :

```javascript
const query = new Query();

query
	.greaterThan('f2', 42)
	.and()
	.lessThan('f2', 76)
	.or()
	.equal('f2', 777);

query.serialize(); // -> '(f2 > 42) and (f2 < 76)  or  (f2 = 777)'
```

Obviously, priority is conserved on nested queries :

```javascript
const query1 = new Query();
const query2 = new Query();
const query3 = new Query();

query2
	.equal('q2f1', 76)
	.or()
	.equal('q2f2', 77);

query3
	.equal('q3f1', 78)
	.and()
	.equal('q3f2', 79);

query1
	.greaterThan('f2', 42)
	.and()
	.nest(query2)
	.and()
	.lessThan('f2', 666)
	.or()
	.nest(query3)
	.or()
	.equal('f2', 777);

query1.serialize();
```

Will produce :

```sql
(f2 > 42)  and (
	(q2f1 = 76)  or  (q2f2 = 77)
) and (f2 < 666)  or  (
	(q3f1 = 78) and (q3f2 = 79)
)  or  (f2 = 777)
```

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
const query = new Query();

query
	.greaterThan('f1', 42)
	.and()
	.toto('f2');

query.serialize(); // -> '(f1 > 42) and (f2 is toto)'
```


## Parser

The `Parser` class helps to transform a Javascript-style filters tree to a serializable query :

```javascript
const query = Parser.parse(myTree);
query.serialize();
```
