The goal of this library is to provide a NPM package that helps to convert javascript-style filters to TQL queries.



Operator
----------

An Operator inherits from the `Operator` class (which implements the `ISerializable` interface).
All operators are daed-simple Javascript classes which has the `Value` and `HasOperand` properties exported.
These two properties describes the operator and helps the `serialize()` method to do his job.

The following operators are supported :

- `and`
- `or`
- `complies to`
- `contains`
- `is empty`
- `is invalid`
- `is valid`
- `=`
- `>`
- `<`


Query
-------

A Query is a serializable set of operators :

```javascript
const q = new Query();

q
	.greaterThan('f2', 42)
	.and()
	.lessThan('f2', 76)
	.or()
	.equal('f2', 777);

```


Queries can be nested thanks to the `nest()` method :

```javascript
const q = new Query();
const qn = new Query();

qn
	.equal('q2f1', 76)
	.or()
	.equal('q2f2', 77);

q
	.greaterThan('f2', 42)
	.and()
	.nest(qn) // <- !
	.and()
	.lessThan('f2', 666);
```

There is no depth limit.


Serialization
---------------

Operators can be serialized to TQL expressions :

```javascript
const o = new GreaterThan('col1', 42);

o.serialize(); // -> 'col1 > 42'
```

And queries too :

```javascript
const q = new Query();

q
	.greaterThan('f2', 42)
	.and()
	.lessThan('f2', 76)
	.or()
	.equal('f2', 777);

q.serialize(); // -> '(f2 > 42)  and  (f2 < 76)  or  (f2 = 777)'
```

Obviously, priority is conserved on nested queries :

```javascript
const q1 = new Query();
const q2 = new Query();
const q3 = new Query();

q2
	.equal('q2f1', 76)
	.or()
	.equal('q2f2', 77);

q3
	.equal('q3f1', 78)
	.and()
	.equal('q3f2', 79);

q1
	.greaterThan('f2', 42)
	.and()
	.nest(q2)
	.and()
	.lessThan('f2', 666)
	.or()
	.nest(q3)
	.or()
	.equal('f2', 777);

q1.serialize();
```

Will produce :

```sql
(f2 > 42)  and (
	(q2f1 = 76)  or  (q2f2 = 77)
)  and  (f2 < 666)  or  (
	(q3f1 = 78)  and  (q3f2 = 79)
)  or  (f2 = 777)
```


TODO
-----

- `between` operator
- Javascript filters tree to TQL
