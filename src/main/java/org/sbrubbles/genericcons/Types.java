package org.sbrubbles.genericcons;

import com.coekie.gentyref.GenericTypeReflector;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A namespace for general type utilities, such as {@linkplain #fromSuperclass(Class, int) cons extraction} and
 * {@linkplain #check(List, List) type checking}. This class is not intended to be instantiated or
 * inherited from.
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
   * Searches {@code baseClass}' immediate superclass for {@linkplain #fromCons(Type) the list of types} in, well,
   * {@code index}.
   * <p>
   * Erasure makes it tricky to capture type data in Java.
   * <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Type tokens</a> are a way around that, but
   * require that type captures be made from a direct subclass. This method uses that same machinery to get to the
   * types, and {@linkplain #fromCons(Type) cons} to decode any lists.
   * <p>
   * Usage:
   * <pre>
   * // note that the instance is an anonymous subclass of A, not A itself!
   * A&lt;String, List&lt;Double&gt;&gt; a = new A&lt;String, List&lt;Double&gt;&gt;() { &#47;* ... *&#47; };
   *
   * System.out.println(Types.fromSuperclass(a.getClass(), 0)); // prints "[class java.lang.String]"
   * System.out.println(Types.fromSuperclass(a.getClass(), 1)); // prints "[java.util.List&lt;java.lang.Double&gt;]"
   * System.out.println(Types.fromSuperclass(a.getClass(), 2)); // throws an exception!
   * </pre>
   * <p>
   * Examples:
   * <table>
   *  <tr><th>Superclass</th><th>Index</th><th>Output</th></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td>0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td>1</td><td>[Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Number, Integer&gt;&gt;</td><td>1</td><td>[Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td>0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td>1</td><td>[Object, Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td>2</td><td>error: IllegalArgumentException!</td></tr>
   *  <tr><td>Object</td><td>0</td><td>error: IllegalArgumentException!</td></tr>
   * </table>
   *
   * @param baseClass the class whose generic superclass holds the type arguments.
   * @param index     where in {@code baseClass}' superclass' type argument list is the desired type.
   * @return a list of the types found in {@code index}.
   * @throws IllegalArgumentException if {@code baseClass} is null or no type parameters were found.
   * @see #fromCons(Type) 
   * @see #genericSuperclassOf(Class)
   */
  public static List<? extends Type> fromSuperclass(Class<?> baseClass, int index)
    throws IllegalArgumentException {
    ParameterizedType superclass = genericSuperclassOf(baseClass);

    if (superclass == null) {
      throw new IllegalArgumentException("No type parameters in " + baseClass + "'s superclass");
    }

    try {
      return fromCons(superclass.getActualTypeArguments()[index]);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException(
        "No type parameters in " + superclass + " at index " + index,
        e);
    }
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
    if (!(type instanceof ParameterizedType)
      || ((ParameterizedType) type).getRawType() != C.class) {
      result.add(type);
      return result;
    }

    Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();

    result.add(actualTypes[0]);
    result.addAll(fromCons(actualTypes[1]));

    return result;
  }

  /**
   * Returns the generic superclass of {@code baseClass}, or {@code null} if there is none.
   * 
   * @param baseClass a class.
   * @return the generic superclass of {@code baseClass}, or {@code null} if there is none.
   * @see #fromSuperclass(Class, int) 
   */
  public static ParameterizedType genericSuperclassOf(Class<?> baseClass) {
    if(baseClass == null) {
      return null;
    }

    Type superclass = baseClass.getGenericSuperclass();

    return (superclass instanceof ParameterizedType)
        ? (ParameterizedType) superclass
        : null;
  }

  private static boolean isPrimitive(Type type) {
    return type == boolean.class
      || type == byte.class
      || type == char.class
      || type == double.class
      || type == float.class
      || type == int.class
      || type == long.class
      || type == short.class;
  }

  private static boolean isReference(Type type) {
    return type != null &&
      !Types.isPrimitive(type) &&
      ((type instanceof Class) || (type instanceof ParameterizedType) || (type instanceof GenericArrayType));
  }
}
