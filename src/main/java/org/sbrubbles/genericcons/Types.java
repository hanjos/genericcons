package org.sbrubbles.genericcons;

import com.coekie.gentyref.GenericTypeReflector;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A namespace for general type utilities.
 * <p>
 * Erasure makes it tricky to capture type data in Java.
 * <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Type tokens</a> are a way around that, but
 * require that type captures be made from a subtype. This class provides methods that use this machinery to
 * extract the types and {@linkplain #fromCons(Type) decode any lists} found.
 * <p>
 * The main methods are {@link #from(SupertypeSelector, Class, int) from}, which finds and gets the desired types,
 * and {@link #check(List, List) check}, which verifies if some objects are assignable to the types found. Other
 * methods provide support and ergonomics.
 * <p>
 * This class is not intended to be instantiated or inherited from.
 *
 * @author Humberto Anjos
 * @see C
 */
public final class Types {
  /**
   * A {@linkplain SupertypeSelector supertype selector} for the first generic interface implemented by the given class.
   */
  private static final SupertypeSelector FIRST_INTERFACE_SELECTOR = Types.genericInterfaceAt(0);

  private Types() { /* preventing instantiation */ }

  /**
   * A <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">functional interface</a>
   * which defines how to get a specific parameterized supertype of a given class.
   *
   * @see #from(SupertypeSelector, Class, int)
   * @see #genericSuperclassOf(Class)
   * @see #genericInterfaceAt(int)
   */
  @FunctionalInterface
  public interface SupertypeSelector {
    /**
     * Searches the given class' inheritance tree for a specific parameterized type. Returns
     * {@linkplain Optional#empty() empty} if a supertype wasn't found, or if the supertype found isn't
     * generic.
     *
     * @param baseClass whose inheritance tree to search.
     * @return the specific parameterized supertype desired, if found.
     */
    Optional<ParameterizedType> genericSupertypeOf(Class<?> baseClass);
  }

  /**
   * Checks if the object's runtime type is assignable to the given type.
   * <p>
   * Null types match nothing, and neither will primitive types (such as {@code int.class}), since Java's autoboxing
   * will convert {@code object} to the equivalent reference type (such as {@code Integer}).
   *
   * @param type   a type.
   * @param object an object.
   * @return if the object's runtime type is compatible with the given type.
   */
  public static boolean check(Type type, Object object) {
    if (type == null) { // nothing matches a null type
      return false;
    }

    if (object == null) { // reference types match with null
      return Types.isReference(type);
    }

    return GenericTypeReflector.isSuperType(type, object.getClass());
  }

  /**
   * Checks if the given objects are assignable to the given types, in the given order.
   *
   * @param types   the types to check against.
   * @param objects the objects to check.
   * @return if the given objects are compatible with the given types.
   */
  public static boolean check(List<? extends Type> types, List<?> objects) {
    if (types == null || objects == null) {
      return false; // null never checks true
    }

    if (types.size() != objects.size()) { // different sizes never check true
      return false;
    }

    final int SIZE = types.size();
    for (int i = 0; i < SIZE; i++) {
      if (!Types.check(types.get(i), objects.get(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Searches {@code baseClass}' supertype, as per the given {@linkplain Types.SupertypeSelector selector}, for
   * {@linkplain #fromCons(Type) the list of types} in {@code index}.
   * <p>
   * The selector is how the caller informs which supertype of {@code baseClass} to use (e.g. a parent class, one of the
   * interfaces...). This class provides some helper methods to
   * {@linkplain #genericSuperclassOf(Class) select} or {@linkplain #genericInterfaceAt(int) generate a selector}.
   * <p>
   * Usage:
   * <pre>
   * // note that a holds an anonymous subclass of A, not A itself!
   * A&lt;String, List&lt;Double&gt;&gt; a = new A&lt;String, List&lt;Double&gt;&gt;() { &#47;* ... *&#47; };
   *
   * System.out.println(Types.from(Types::genericSuperclassOf, a.getClass(), 0)); // prints "[class java.lang.String]"
   * System.out.println(Types.from(Types::genericSuperclassOf, a.getClass(), 1)); // prints "[java.util.List&lt;java.lang.Double&gt;]"
   * System.out.println(Types.from(Types::genericSuperclassOf, a.getClass(), 2)); // throws an exception!
   * </pre>
   * <p>
   * Examples:
   * <table>
   *  <tr><th>Generic supertype</th><th align="center">Index</th><th>Output</th></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td align="center">0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td align="center">1</td><td>[Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Number, Integer&gt;&gt;</td><td align="center">1</td><td>[Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td align="center">0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td align="center">1</td><td>[Object, Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td align="center">2</td><td>error: IllegalArgumentException!</td></tr>
   *  <tr><td>none</td><td align="center">0</td><td>error: IllegalArgumentException!</td></tr>
   * </table>
   *
   * @param selector  searches {@code baseClass}' inheritance tree for the desired generic supertype.
   * @param baseClass the class whose generic supertype holds the desired types.
   * @param index     where in {@code baseClass}' supertype's type argument list are the desired types.
   * @return a list of the types found in {@code index}.
   * @throws IllegalArgumentException if {@code baseClass} is null, if {@code selector} is null, or no type parameters were found.
   * @see #fromCons(Type)
   * @see Types.SupertypeSelector
   * @see #genericSuperclassOf(Class)
   * @see #genericInterfaceAt(int)
   * @see #fromSuperclass(Class, int)
   */
  public static List<? extends Type> from(SupertypeSelector selector, Class<?> baseClass, int index)
    throws IllegalArgumentException {
    if (selector == null) {
      throw new IllegalArgumentException("No selector given");
    }

    Optional<ParameterizedType> supertype = selector.genericSupertypeOf(baseClass);
    if (!supertype.isPresent()) {
      throw new IllegalArgumentException("No generic supertype found for " + baseClass);
    }

    try {
      return fromCons(supertype.get().getActualTypeArguments()[index]);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException(
        "No type parameters in " + supertype + " at index " + index,
        e);
    }
  }

  /**
   * Searches {@code baseClass}' superclass for {@linkplain #fromCons(Type) the list of types} in {@code index}.
   * <p>
   * The same results can be obtained with {@link #from(SupertypeSelector, Class, int) from} and an
   * {@linkplain #genericSuperclassOf(Class) appropriate selector}, but this method is shorter.
   *
   * @param baseClass the class whose generic superclass holds the desired types.
   * @param index     where in {@code baseClass}' superclass' type argument list is the desired type.
   * @return a list of the types found in {@code index}.
   * @throws IllegalArgumentException if {@code baseClass} is null or no type parameters were found.
   */
  public static List<? extends Type> fromSuperclass(Class<?> baseClass, int index)
    throws IllegalArgumentException {
    return from(Types::genericSuperclassOf, baseClass, index);
  }

  /**
   * Searches {@code baseClass}' first superinterface for {@linkplain #fromCons(Type) the list of types} in
   * {@code index}.
   * <p>
   * The same results can be obtained with {@link #from(SupertypeSelector, Class, int) from} and an
   * {@linkplain #genericInterfaceAt(int) appropriate selector}, but this method is shorter, especially from an
   * anonymous subclass.
   *
   * @param baseClass the class whose first superinterface holds the desired types.
   * @param index     where in {@code baseClass}' first superinterface's type argument list is the desired type.
   * @return a list of the types found in {@code index}.
   * @throws IllegalArgumentException if {@code baseClass} is null or no type parameters were found.
   */
  public static List<? extends Type> fromInterface(Class<?> baseClass, int index)
    throws IllegalArgumentException {
    return Types.from(FIRST_INTERFACE_SELECTOR, baseClass, index);
  }

  /**
   * Reads the given type as a {@linkplain C cons} and returns the list of types represented therein.
   * <p>
   * {@code null} returns the empty list, and a non-{@code C} type will be returned in a one-element list.
   * <p>
   * Examples:
   * <table>
   *   <tr><td><b>Input</b></td><td><b>Output</b></td></tr>
   *   <tr><td>null</td><td>[]</td></tr>
   *   <tr><td>String</td><td>[String]</td></tr>
   *   <tr><td>C&lt;String, Number&gt;</td><td>[String, Number]</td></tr>
   *   <tr><td>C&lt;String, C&lt;Number, Object&gt;&gt;</td><td>[String, Number, Object]</td></tr>
   *   <tr><td>C&lt;String, C&lt;Number, C&lt;Object, List&lt;Double&gt;&gt;&gt;</td><td>[String, Number, Object, List&lt;Double&gt;]</td></tr>
   * </table>
   *
   * @param type a type.
   * @return the list of the types represented by the given type.
   */
  public static List<? extends Type> fromCons(Type type) {
    if (type == null) {
      return Collections.emptyList();
    }

    List<Type> result = new ArrayList<>();

    // end of recursion, add it and return
    if (!(type instanceof ParameterizedType) ||
        ((ParameterizedType) type).getRawType() != C.class) {
      result.add(type);
      return result;
    }

    Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();

    result.add(actualTypes[0]);
    result.addAll(fromCons(actualTypes[1]));

    return result;
  }

  /**
   * Returns the generic superclass of {@code baseClass}, if there is one. This method can be used as a
   * {@linkplain Types.SupertypeSelector supertype selector}.
   *
   * @param baseClass a class.
   * @return the generic superclass of {@code baseClass}, if there is one.
   * @see Types.SupertypeSelector
   * @see #from(SupertypeSelector, Class, int)
   */
  public static Optional<ParameterizedType> genericSuperclassOf(Class<?> baseClass) {
    if (baseClass == null) {
      return Optional.empty();
    }

    Type superclass = baseClass.getGenericSuperclass();

    return Optional.ofNullable(
      (superclass instanceof ParameterizedType)
        ? (ParameterizedType) superclass
        : null);
  }

  /**
   * Returns the generic interface of {@code baseClass} at the given index, if there is one.
   *
   * @param baseClass a class.
   * @param index     which superinterface to return.
   * @return the generic interface of {@code baseClass} at the given index, if there is one.
   */
  public static Optional<ParameterizedType> genericInterfaceOf(Class<?> baseClass, int index) {
    if (baseClass == null) {
      return Optional.empty();
    }

    Type[] supertypes = baseClass.getGenericInterfaces();
    if (index < 0 || index >= supertypes.length) {
      return Optional.empty();
    }

    return Optional.ofNullable(
      (supertypes[index] instanceof ParameterizedType)
        ? (ParameterizedType) supertypes[index]
        : null);
  }

  /**
   * Returns a {@linkplain Types.SupertypeSelector supertype selector} which fetches the {@code index}-th generic
   * superinterface of a given class.
   *
   * @param index which superinterface to return.
   * @return a selector which returns the {@code index}-th generic superinterface of a given class, if there is one.
   * @see Types.SupertypeSelector
   * @see #from(SupertypeSelector, Class, int)
   */
  public static SupertypeSelector genericInterfaceAt(final int index) {
    return baseClass -> genericInterfaceOf(baseClass, index);
  }

  private static boolean isPrimitive(Type type) {
    return type == boolean.class ||
           type == byte.class ||
           type == char.class ||
           type == double.class ||
           type == float.class ||
           type == int.class ||
           type == long.class ||
           type == short.class;
  }

  private static boolean isReference(Type type) {
    return type != null &&
           !Types.isPrimitive(type) &&
           ((type instanceof Class) || (type instanceof ParameterizedType) || (type instanceof GenericArrayType));
  }
}
