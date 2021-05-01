0.8
---
* `Types.from*` methods now throw specific exceptions instead of catch-all `IllegalArgumentException`s.


0.7
---
* `Types.from` now takes `ParameterizedType`s instead of selectors and classes. This allows for other ways of getting generic types.
* `Types.generic*` methods return `Optional`s instead of `null`.
* `Type.SupertypeSelector` no longer exists.

0.6
---
* New general method `Types.from`, which helps support superinterfaces as well, and some assorted helper methods.
* Fixing the Javadoc publishing workflow.

0.5
---
* `Types.fromSuperclass` throws only `IllegalArgumentExceptions`.
* `Types.fromCons` returns an empty list on null. 
* `Types.check` now takes Lists instead of Iterables. Ordered sequences were implicitly expected anyway, so using Lists makes that requirement explicit.
* CSS adjustments to the generated Javadocs.
* Deploying a package also updates and publishes the new Javadocs.

0.4
---

* Using GitHub as CI and Package Repo.
* Assorted IntelliJ inspection fixes.
* Moved all functionality to `Types`, so `C` is only a marker class.
* Removed all custom exceptions.  
* Updated dependencies to Java 8.

0.3
---

* Using `gentyref` instead of `javaruntype` for type manipulation.
* Added type checking methods to `C`.
* Created `Utils` to host general utility methods.
* `TypeMatchingException` and `InvalidTypeException` no longer exist.
* Assorted doc fixes.

0.2
---

* Type tokens now work differently: instead of building a `Cons` instance to capture the type parameters, there is a `C` class which is used in the type declaration, and the static `extractTypesFromSuperclass` method to, given the base class and the index of the parameter type, return a list with all the types expressed.
* `Cons` and `UnexpectedTypeException` no longer exist.

0.1
---

* First release!