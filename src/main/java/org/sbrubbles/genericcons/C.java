package org.sbrubbles.genericcons;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.javaruntype.type.Type;
import org.javaruntype.type.Types;

/**
 * Captures and represents an open-ended list of types. This class isn't 
 * supposed to be instantiated; it is used to construct the list in the type
 * declaration, like below:
 * 
 * <pre>
 * Function&lt;?, ?&gt; f = new Function&lt;String, C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;() {
 *   { // the no-arg constructor
 *     this.types = C.extractTypesFromSuperclass(this.getClass(), 1);
 *   }
 * };
 * </pre>
 * 
 * @author Humberto Anjos
 * @param <First> The first type.
 * @param <Rest> The last type, or a C holding the rest of the types.
 */
public final class C<First, Rest> {
  // prevents instantiation
  private C() { /* empty block */ }
  
  /**
   * Searches the given base class' superclass for the list of types indexed by
   * {@code parameterIndex}.
   * 
   * @param baseClass the class whose generic superclass holds the list of 
   * type arguments. 
   * @param parameterIndex where in the given base class' generic superclass's 
   * type argument list is the desired list of types.
   * @return a list of the types found. 
   * @throws TypeParametersNotFoundException if no type parameters are found. 
   * @throws InvalidTypeException if there is a problem converting the standard
   * Java {@linkplain java.lang.reflect.Type type} to a {@linkplain Type fuller
   * representation}. 
   */
  public static List<? extends Type<?>> extractTypesFromSuperclass(Class<?> baseClass, int parameterIndex)
  throws TypeParametersNotFoundException, InvalidTypeException {
    if(baseClass == null)
      throw new IllegalArgumentException("The base class cannot be null!");
    
    java.lang.reflect.Type superclass = baseClass.getGenericSuperclass();
    
    if(! (superclass instanceof ParameterizedType))
      throw new TypeParametersNotFoundException(baseClass);
    
    ParameterizedType cons = (ParameterizedType) superclass;
    try {
      return extractTypesFromCons(cons.getActualTypeArguments()[parameterIndex]);
    } catch (IndexOutOfBoundsException e) {
      throw new TypeParametersNotFoundException(baseClass, e);
    }
  }

  // TODO match a list of types against a list of objects
  
  private static List<? extends Type<?>> extractTypesFromCons(java.lang.reflect.Type type)
  throws InvalidTypeException {
    assert type != null;
    
    List<Type<?>> result = new ArrayList<Type<?>>();
    
    // end of recursion, add it and return
    if(! (type instanceof ParameterizedType) 
    || ((ParameterizedType) type).getRawType() != C.class) {
      result.add(convertToFullType(type));
      return result;
    }
    
    // recurse
    ParameterizedType cons = (ParameterizedType) type;
    java.lang.reflect.Type[] actualTypes = cons.getActualTypeArguments();
    
    result.add(convertToFullType(actualTypes[0]));
    result.addAll(extractTypesFromCons(actualTypes[1]));
    
    return result;
  }

  private static Type<?> convertToFullType(java.lang.reflect.Type type) 
  throws InvalidTypeException {
    try {
      return Types.forJavaLangReflectType(type);
    } catch (Throwable t) {
      throw new InvalidTypeException(type, t);
    }
  }
}
