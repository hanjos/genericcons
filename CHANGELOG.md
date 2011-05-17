0.2-SNAPSHOT
------------

* Type tokens now work differently: instead of building a Cons instance to capture the type parameters, there is a C 
class which is used in the type declaration, and the static `extractTypesFromSuperclass` method to, given the base 
class and the index of the parameter type, returns a list with all the types expressed.

* Cons and UnexpectedTypeException no longer exist.