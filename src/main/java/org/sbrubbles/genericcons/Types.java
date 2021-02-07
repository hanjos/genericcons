package org.sbrubbles.genericcons;

import com.coekie.gentyref.GenericTypeReflector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A namespace for general type utilities, such as {@linkplain #fromSuperclass(Class, int) cons extraction} and
 * {@linkplain #check(Iterable, Iterable) type checking}.
 * <p>
 * It's not intended to be instantiated or inherited from.
 *
 * @author Humberto Anjos
 * @see C
 */
public final class Types {
  private Types() { /* preventing instantiation */ }

  /**
   * Checks if the object's runtime type is compatible with the given type. Null types match nothing.
   * <p>
   * Neither will primitive types (such as {@code int.class}), since Java's autoboxing will convert {@code object} to
   * the equivalent reference type (such as {@code Integer}).
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
   * Checks if the given objects are compatible with the types held by this instance.
   *
   * @param types   the given types to check against.
   * @param objects the given objects to check.
   * @return if the given objects are compatible with the given types.
   */
  public static boolean check(Iterable<? extends Type> types, Iterable<?> objects) {
    if (types == null || objects == null) {
      return false; // empty iterator never checks true
    }

    Iterator<?> objectsIterator = objects.iterator();

    for (Type type : types) {
      if (!objectsIterator.hasNext() || // the amount of types and objects doesn't match
        !Types.check(type, objectsIterator.next())) {
        return false;
      }
    }

    return !objectsIterator.hasNext(); // the amount of types and objects must match
  }

  /**
   * Searches the given base class' superclass for the list of types indexed by
   * {@code parameterIndex}.
   * <p>
   * Examples:
   *
   * <table>
   *  <tr><td><b>Generic Superclass</b></td><td><b>Index</b></td><td><b>Output</b></td></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td>0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, Integer&gt;</td><td>1</td><td>[Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Number, Integer&gt;&gt;</td><td>1</td><td>[Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td>0</td><td>[String]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td>1</td><td>[Object, Number, Integer]</td></tr>
   *  <tr><td>Map&lt;String, C&lt;Object, C&lt;Number, Integer&gt;&gt;&gt;</td><td>2</td><td>error: TypeParametersNotFoundException!</td></tr>
   *  <tr><td>Object</td><td>0</td><td>error: IllegalArgumentException!</td></tr>
   * </table>
   *
   * @param baseClass      the class whose generic superclass holds the list of
   *                       type arguments.
   * @param parameterIndex where in the given base class' generic superclass'
   *                       type argument list is the desired list of types.
   * @return a list of the types found.
   * @throws IllegalArgumentException if the given base class is null or no type parameters are found.
   */
  public static List<? extends Type> fromSuperclass(Class<?> baseClass, int parameterIndex)
    throws IllegalArgumentException {
    if (baseClass == null) {
      throw new IllegalArgumentException("The base class cannot be null!");
    }

    Type superclass = baseClass.getGenericSuperclass();

    if (!(superclass instanceof ParameterizedType)) {
      throw new IllegalArgumentException("No type parameters in " + baseClass.getCanonicalName());
    }

    try {
      return fromCons(((ParameterizedType) superclass).getActualTypeArguments()[parameterIndex]);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Error extracting type parameters in " + baseClass.getCanonicalName(), e);
    }
  }

  /**
   * Reads the given type as a cons structure and returns the list of types represented therein.
   * <p>
   * Examples:
   * <table>
   * <tr><td><b>Input</b></td><td><b>Output</b></td></tr>
   * <tr><td>String</td><td>[String]</td></tr>
   * <tr><td>C&lt;String, Number&gt;</td><td>[String, Number]</td></tr>
   * <tr><td>C&lt;String, C&lt;Number, Object&gt;&gt;</td><td>[String, Number, Object]</td></tr>
   * <tr><td>C&lt;String, C&lt;Number, C&lt;Object, List&lt;Double&gt;&gt;&gt;</td><td>[String, Number, Object, List&lt;Double&gt;]</td></tr>
   * <tr><td>null</td><td>error: IllegalArgumentException!</td></tr>
   * </table>
   *
   * @param type a type.
   * @return a list of the types represented by the given type.
   * @throws IllegalArgumentException if the given type is null.
   */
  public static List<? extends Type> fromCons(Type type)
    throws IllegalArgumentException {
    if (type == null) {
      throw new IllegalArgumentException("The type cannot be null!");
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
      ((type instanceof Class) ||
        (type instanceof ParameterizedType));
  }
}
