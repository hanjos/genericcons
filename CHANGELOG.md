0.3-SNAPSHOT
------------

* Added matching methods to `C`.
* Renamed `InvalidTypeException` to `TypeConversionException`.
* Removed `TypeConversionException`'s 1-argument constructor.
* `TypeMatchingException` no longer exists.
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