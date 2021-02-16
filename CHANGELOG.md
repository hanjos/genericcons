0.5-SNAPSHOT
------------
* Types.fromCons was removed from the public API.
* Types.check now takes Lists instead of Iterables. Although technically this
  is less general, ordered sequences were implicitly expected anyway,
  so using Lists makes that requirement explicit.

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

* Type tokens now work differently: instead of building a `Cons` instance to capture the type parameters, there is a `C` 
class which is used in the type declaration, and the static `extractTypesFromSuperclass` method to, given the base 
class and the index of the parameter type, returns a list with all the types expressed.

* `Cons` and `UnexpectedTypeException` no longer exist.

0.1
---

* First release!