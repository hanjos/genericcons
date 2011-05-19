package org.sbrubbles.genericcons;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.javaruntype.type.Type;
import org.javaruntype.type.Types;

/**
 * Captures and represents an open-ended list of types. This class isn't 
 * supposed to be instantiated or subclassed; it's used to build the 
 * list in the type declaration, like below:
 * 
 * <pre>
 * Function&lt;?, ?&gt; f = new Function&lt;String, C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;() {
 *   { // the no-arg constructor
 *     this.types = C.extractTypesFromSuperclass(this.getClass(), 1);
 *   }
 * };
 * </pre>
 * 
 * It was supposed to be {@code Cons} (hence the {@code C}), but using only
 * one letter kept type arguments (slightly) more readable.
 * 
 * @author Humberto Anjos
 * @param <First> The first type.
 * @param <Rest> The last type, or a C holding the rest of the types.
 */
public final class C<First, Rest> {
  // preventing instantiation
  private C() { /* empty block */ }
  
  /**
   * Searches the given base class' superclass for the list of types indexed by
   * {@code parameterIndex}.
   * 
   * @param baseClass the class whose generic superclass holds the list of 
   * type arguments. 
   * @param parameterIndex where in the given base class' generic superclass' 
   * type argument list is the desired list of types.
   * @return a list of the types found. 
   * @throws IllegalArgumentException if the given base class is null.
   * @throws TypeParametersNotFoundException if no type parameters are found. 
   * @throws TypeConversionException if there is a problem converting the standard
   * Java {@linkplain java.lang.reflect.Type type} to a {@linkplain Type fuller
   * representation}. 
   */
  public static List<? extends Type<?>> extractTypesFromSuperclass(Class<?> baseClass, int parameterIndex)
  throws IllegalArgumentException, TypeParametersNotFoundException, TypeConversionException {
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
 
  /**
   * Checks if the given objects are compatible with the types in {@code types}.
   * 
   * @param types a list of full types.
   * @param objects the given objects to match.
   * @return if the given objects have the same type.
   * @throws IllegalArgumentException if one of the arguments is null.
   * @throws TypeMatchingException if a problem occurs while checking an object's compatibility.
   */
  public static boolean matches(List<? extends Type<?>> types, Object... objects) 
  throws IllegalArgumentException, TypeMatchingException {
    if(types == null)
      throw new IllegalArgumentException("types cannot be null!");
    
    if(objects == null)
      throw new IllegalArgumentException("objects cannot be null");
    
    if(types.size() != objects.length)
      return false;
    
    for(int i = 0; i < objects.length; i++) {
      Object object = objects[i];
      Type<?> type = types.get(i);
      
      if(type == null)
        throw new TypeMatchingException(null, object);
      
      if(object == null) // null matches anything
        continue;
      
      if(! type.getRawClass().isAssignableFrom(object.getClass())) // mismatch found
        return false;
    }
    
    return true;
  }
  
  private static List<? extends Type<?>> extractTypesFromCons(java.lang.reflect.Type type)
  throws TypeConversionException {
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
  throws TypeConversionException {
    try {
      return Types.forJavaLangReflectType(type);
    } catch (Throwable t) {
      throw new TypeConversionException(type, t);
    }
  }
}
