package org.sbrubbles.genericcons;

import com.coekie.gentyref.GenericTypeReflector;
import com.coekie.gentyref.TypeFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A namespace for type utilities.
 * <p>
 * Erasure makes it tricky to capture type data, although there are some ways around that, such as
 * <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">type tokens</a> and some reflection chicanery.
 * This class provides methods that find and obtain any desired types, and {@linkplain #fromCons(Type) decode any lists}
 * found.
 * <p>
 * The main methods are {@link #from(ParameterizedType, int) from}, which extracts the desired types from a given
 * generic type, and {@link #check(List, List) check}, which verifies if objects are assignable to types. Other methods
 * provide support and ergonomics. Particularly, the {@code generic*} family of methods is useful for obtaining generic
 * types to feed {@code from}.
 * <p>
 * This class is not intended to be instantiated or inherited.
 *
 * @author Humberto Anjos
 * @see C
 */
public final class Types {
  private Types() { /* preventing instantiation */ }

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

    if (object == null) { // non-primitive types match with null
      return !Types.isPrimitive(type);
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
   * Returns {@linkplain #fromCons(Type) the list of types} {@code type} holds in {@code index}.
   * This class provides some methods to fetch parameterized types from common sources.
   * <p>
   * Usage:
   * <pre>
   * // note that a holds an anonymous subclass of A, not A itself!
   * A&lt;String, List&lt;Double&gt;&gt; a = new A&lt;String, List&lt;Double&gt;&gt;() { &#47;* ... *&#47; };
   *
   * Optional&lt;ParameterizedType&gt; superclass = Types.genericSuperclassOf(a.getClass());
   *
   * // not best practice, but here it'll do
   * System.out.println(Types.from(superclass.get(), 0)); // prints "[class java.lang.String]"
   * System.out.println(Types.from(superclass.get(), 1)); // prints "[java.util.List&lt;java.lang.Double&gt;]"
   * System.out.println(Types.from(superclass.get(), 2)); // throws an exception!
   * </pre>
   * <p>
   * Examples:
   * <table>
   *  <tr><th>Type</th><th align="center">Index</th><th>Output</th></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td align="center">0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td align="center">1</td><td>[Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Number, Integer&gt;&gt;</td><td align="center">1</td><td>[Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td align="center">0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td align="center">1</td><td>[Object, Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td align="center">2</td><td>error: IndexOutOfBoundsException!</td></tr>
   *  <tr><td>null</td><td align="center">doesn't matter</td><td>error: NullPointerException!</td></tr>
   * </table>
   *
   * @param type  a generic type.
   * @param index where in {@code type}'s type argument list are the desired types.
   * @return a list of the types found in {@code index}.
   * @throws NullPointerException      if {@code type} is null.
   * @throws IndexOutOfBoundsException if no type parameters were found at {@code index}.
   * @see #genericSuperclassOf(Class)
   * @see #genericInterfaceOf(Class, int)
   * @see #fromCons(Type)
   * @see #fromSuperclass(Class, int)
   * @see #fromInterface(Class, int)
   */
  public static List<? extends Type> from(ParameterizedType type, int index)
    throws NullPointerException, IndexOutOfBoundsException {
    if (type == null) {
      throw new NullPointerException("No generic type given");
    }

    Type[] typeArguments = type.getActualTypeArguments();
    if (index < 0 || index >= typeArguments.length) {
      throw new IndexOutOfBoundsException("No type parameters in " + type + " at index " + index);
    }

    return fromCons(typeArguments[index]);
  }

  /**
   * Searches {@code baseClass}' superclass for {@linkplain #fromCons(Type) the list of types} in {@code index}.
   *
   * @param baseClass the class whose generic superclass holds the desired types.
   * @param index     where in {@code baseClass}' superclass' type argument list is the desired type.
   * @return a list of the types found in {@code index}.
   * @throws NullPointerException      if {@code baseClass} is null.
   * @throws NoSuchElementException    if {@code baseClass}' superclass isn't generic.
   * @throws IndexOutOfBoundsException if no type parameters were found in {@code baseClass}' superclass at
   *                                   {@code index}.
   * @see #from(ParameterizedType, int)
   */
  public static List<? extends Type> fromSuperclass(Class<?> baseClass, int index)
    throws NullPointerException, NoSuchElementException, IndexOutOfBoundsException {
    if (baseClass == null) {
      throw new NullPointerException("No base class given");
    }

    Optional<ParameterizedType> supertype = genericSuperclassOf(baseClass);
    if (!supertype.isPresent()) {
      throw new NoSuchElementException("No generic superclass found for " + baseClass);
    }

    return from(supertype.get(), index);
  }

  /**
   * Searches {@code baseClass}' first superinterface for {@linkplain #fromCons(Type) the list of types} in
   * {@code index}.
   *
   * @param baseClass the class whose first superinterface holds the desired types.
   * @param index     where in {@code baseClass}' first superinterface's type argument list is the desired type.
   * @return a list of the types found in {@code index}.
   * @throws NullPointerException      if {@code baseClass} is null.
   * @throws NoSuchElementException    if {@code baseClass} doesn't have a superinterface, or it's first superinterface
   *                                   isn't generic.
   * @throws IndexOutOfBoundsException if no type parameters were found in {@code baseClass}' first superinterface at
   *                                   {@code index}.
   * @see #from(ParameterizedType, int)
   */
  public static List<? extends Type> fromInterface(Class<?> baseClass, int index)
    throws NullPointerException, NoSuchElementException, IndexOutOfBoundsException {
    if (baseClass == null) {
      throw new NullPointerException("No base class given");
    }

    Optional<ParameterizedType> supertype = genericInterfaceOf(baseClass, 0);
    if (!supertype.isPresent()) {
      throw new NoSuchElementException("No generic superinterface in " + baseClass + " at index 0");
    }

    return from(supertype.get(), index);
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
   * @see #cons(List)
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
   * Returns the generic superclass of {@code baseClass}, if there is one.
   *
   * @param baseClass a class.
   * @return the generic superclass of {@code baseClass}, if there is one.
   * @see #from(ParameterizedType, int)
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
   * Returns the generic interface of {@code baseClass} at {@code index}, if there is one.
   *
   * @param baseClass a class.
   * @param index     which of {@code baseClass}' generic superinterfaces to return.
   * @return the generic interface of {@code baseClass} at {@code index}, if there is one.
   * @see #from(ParameterizedType, int)
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
   * Encodes the given list of types as a {@linkplain C cons}, as accepted by {@link #fromCons(Type)}. This method is
   * equivalent to calling {@link Types#cons(List)}, with a slightly more convenient syntax.
   *
   * @param types a list of types to encode.
   * @return a type encoding the given list, as extractable by {@link #fromCons(Type)}.
   * @throws NullPointerException if {@code types} is not empty and at least one of the given types is null.
   * @see #cons(List)
   */
  public static Type cons(Type... types) throws NullPointerException {
    if (types == null || types.length == 0) {
      return null;
    }

    return cons(Arrays.asList(types));
  }

  /**
   * Encodes the given list of types as a {@linkplain C cons}, as accepted by {@link #fromCons(Type)}.
   * <p>
   * That means:
   * <ul>
   *   <li>A {@code null} or empty list yields {@code null};</li>
   *   <li>A single type is returned as is;</li>
   *   <li>Two or more types are encoded as a {@link C C} type.</li>
   * </ul>
   * <p>
   * Any {@code C} types within the given list will be broken down and flattened into a larger list. Any {@code null}s
   * within the given type list are considered empty {@code C} types, as per {@code fromCons}. So (excusing the
   * pseudo-Java)
   * <pre>
   *   Types.cons([null, String, Object, null, Number, List&lt;Double&gt;])
   * </pre>
   * and
   * <pre>
   *   Types.cons([C&lt;String, Object&gt;, null, C&lt;Number, List&lt;Double&gt;&gt;])
   * </pre>
   * return the same result: a type representing {@code C<String, C<Object, C<Number, List<Double>>>>}.
   * <p>
   * This method and {@link #fromCons(Type)} are duals: that means that
   *
   * <pre>
   *   List&lt;? extends Type&gt; types = // ...
   *   assert Object.equals(types, Types.fromCons(Types.cons(types)));
   * </pre>
   * <p>
   * holds for any list of {@link Type}s not containing {@code C} types.
   *
   * @param types a list of types to encode.
   * @return a type encoding the given list, as extractable by {@link #fromCons(Type)}, or {@code null} if the given
   * list if {@code null} or empty.
   * @see #fromCons(Type)
   */
  public static Type cons(List<? extends Type> types) {
    if (types == null) {
      return null;
    }

    List<? extends Type> flattenedTypes = types.stream()
      .flatMap(t -> Types.fromCons(t).stream())
      .collect(Collectors.toList());

    if (flattenedTypes.isEmpty()) {
      return null;
    }

    if (flattenedTypes.size() == 1) {
      return flattenedTypes.get(0);
    }

    final int SIZE = flattenedTypes.size();
    Type cons = TypeFactory.parameterizedClass(C.class, flattenedTypes.get(SIZE - 2), flattenedTypes.get(SIZE - 1));
    for (int i = SIZE - 3; i >= 0; i--) {
      cons = TypeFactory.parameterizedClass(C.class, flattenedTypes.get(i), cons);
    }

    return cons;
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
}
