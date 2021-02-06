package org.sbrubbles.genericcons;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.coekie.gentyref.GenericTypeReflector;

/**
 * Provides a namespace for general utility methods. It's not intended to be instantiated or inherited from.
 * 
 * @author Humberto Anjos
 */
public final class Types {
  private Types() { /* preventing instantiation */ }
  
  /**
   * Checks if the object's runtime type is compatible with the given type. Null types match nothing, and null objects
   * are compatible with any given type.
   * 
   * @param type a type. 
   * @param object an object.
   * @return if the object's runtime type is compatible with the given type. 
   */
  public static boolean check(Type type, Object object) {
    if(type == null) // nothing matches a null type
      return false;
    
    if(object == null) // everything else matches a null object
      return true;
    
    return GenericTypeReflector.isSuperType(type, object.getClass());
  }

  /**
   * Searches the given base class' superclass for the list of types indexed by
   * {@code parameterIndex}.
   *
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
   *  <tr><td>Object</td><td>0</td><td>error: TypeParametersNotFoundException!</td></tr>
   * </table>
   *
   * @param baseClass the class whose generic superclass holds the list of
   * type arguments.
   * @param parameterIndex where in the given base class' generic superclass'
   * type argument list is the desired list of types.
   * @return a list of the types found.
   * @throws IllegalArgumentException if the given base class is null.
   * @throws TypeParametersNotFoundException if no type parameters are found.
   */
  public static List<? extends Type> extractFromSuperclass(Class<?> baseClass, int parameterIndex)
  throws IllegalArgumentException, TypeParametersNotFoundException {
    if(baseClass == null) {
      throw new IllegalArgumentException("The base class cannot be null!");
    }

    Type superclass = baseClass.getGenericSuperclass();

    if(! (superclass instanceof ParameterizedType)) {
      throw new TypeParametersNotFoundException(baseClass);
    }

    try {
      return extractFromCons(((ParameterizedType) superclass).getActualTypeArguments()[parameterIndex]);
    } catch (IndexOutOfBoundsException e) {
      throw new TypeParametersNotFoundException(baseClass, e);
    }
  }

  /**
   * Reads the given type as a cons structure and returns the list of types represented therein.
   *
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
  public static List<? extends Type> extractFromCons(Type type)
  throws IllegalArgumentException {
    if(type == null) {
      throw new IllegalArgumentException("The type cannot be null!");
    }

    List<Type> result = new ArrayList<Type>();

    // end of recursion, add it and return
    if(! (type instanceof ParameterizedType)
    || ((ParameterizedType) type).getRawType() != C.class) {
      result.add(type);
      return result;
    }

    Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();

    result.add(actualTypes[0]);
    result.addAll(extractFromCons(actualTypes[1]));

    return result;
  }
}
