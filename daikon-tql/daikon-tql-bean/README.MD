# TQL - Bean predicate generation

The goal of this module is to enable code to create a `java.util.Predicate` out of a TQL expression.
As in this example:

```java
Bean bean1 = new Bean();
bean1.setValue(10);

Bean bean2 = new Bean();
bean2.setValue(-5);

final Expression query = Tql.parse("value > 0");
final Predicate<Bean> predicate = (Predicate<Bean>) query.accept(new ObjectPredicateVisitor(Bean.class));

predicate.test(bean1); // true
predicate.test(bean2); // false  
```

Using a `Predicate` is especially useful when dealing with `Stream` of objects:

```java
Stream<Bean> beans = Stream.of(...) // A stream of Bean instances
final Expression query = Tql.parse("value > 0");
final Predicate<Bean> predicate = (Predicate<Bean>) query.accept(new ObjectPredicateVisitor(Bean.class));

List<Bean> positiveValueBeans = beans.filter(predicate).collect(Collectors.toList());
``` 

## Nested property support

The predicate supports nested properties (e.g. `firstLevel.seconLevel.value` will invoke methods `getFirstLevel()` / `getSecondLevel()` / `getValue()`).