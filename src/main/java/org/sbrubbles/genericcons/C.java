package org.sbrubbles.genericcons;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.googlecode.gentyref.GenericTypeReflector;

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
 * It was supposed to be called {@code Cons} (hence the {@code C}), but using only
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
   * A helper interface for the type checking API. Instances will hold the types to be used for checking. 
   * 
   * @author Humberto Anjos
   */
  public static interface TypeChecker {
    /**
     * Checks if the given objects are compatible with the types held by this instance.
     * 
     * @param objects the given objects to check.
     * @return if the given objects are type-compatible with the types held by this instance.
     * @throws IllegalArgumentException if {@code objects} is null.
     */
    boolean onVarargs(Object... objects) throws IllegalArgumentException;
    
    /**
     * Checks if the given objects are compatible with the types held by this instance.
     * 
     * @param objects the given objects to check.
     * @return if the given objects are type-compatible with the types held by this instance.
     * @throws IllegalArgumentException if {@code objects} is null.
     */
    boolean onIterable(Iterable<?> objects) throws IllegalArgumentException;
  }
  
  /**
   * Holds a list of types for checking.
   */
  private static class TypeListChecker implements TypeChecker {
    private final List<? extends Type> types;

    public TypeListChecker(Type... types) throws IllegalArgumentException {
      if(types == null)
        throw new IllegalArgumentException("types cannot be null!");
      
      this.types = Arrays.asList(types);
    }
    
    public TypeListChecker(List<? extends Type> types) throws IllegalArgumentException {
      if(types == null)
        throw new IllegalArgumentException("types cannot be null!");
      
      this.types = types;
    }
    
    @Override
    public boolean onVarargs(Object... objects) throws IllegalArgumentException {
      if(objects == null)
        throw new IllegalArgumentException("objects cannot be null");
      
      return checkVarargs(types, objects);
    }

    @Override
    public boolean onIterable(Iterable<?> objects) throws IllegalArgumentException {
      if(objects == null)
        throw new IllegalArgumentException("objects cannot be null");
      
      return checkIterable(types, objects);
    }    
  }
  
  /**
   * Returns an object which uses the given types for type checking.
   * 
   * @param types the types used for checking.
   * @return a type checker.
   * @throws IllegalArgumentException if {@code types} is null. 
   */
  public static TypeChecker check(List<? extends Type> types) throws IllegalArgumentException {
    return new TypeListChecker(types);
  }
  
  /**
   * Returns an object which uses the given types for type checking.
   * 
   * @param types the types used for checking.
   * @return a type checker.
   * @throws IllegalArgumentException if {@code types} is null. 
   */
  public static TypeChecker check(Type... types) throws IllegalArgumentException {
    return new TypeListChecker(types);
  }
  
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
   */
  public static List<? extends Type> extractTypesFromSuperclass(Class<?> baseClass, int parameterIndex)
  throws IllegalArgumentException, TypeParametersNotFoundException {
    if(baseClass == null)
      throw new IllegalArgumentException("The base class cannot be null!");
    
    Type superclass = baseClass.getGenericSuperclass();
    
    if(! (superclass instanceof ParameterizedType))
      throw new TypeParametersNotFoundException(baseClass);
    
    ParameterizedType cons = (ParameterizedType) superclass;
    try {
      return extractTypesFromCons(cons.getActualTypeArguments()[parameterIndex]);
    } catch (IndexOutOfBoundsException e) {
      throw new TypeParametersNotFoundException(baseClass, e);
    }
  }
 
  private static boolean checkVarargs(List<? extends Type> types, Object... objects) {
    assert types != null;
    assert objects != null;
    
    if(types.size() != objects.length) // the number of objects and types doesn't match
      return false;
    
    for(int i = 0; i < objects.length; i++) {
      if(! checkType(types.get(i), objects[i]))
        return false;
    }
    
    return true;
  }
  
  private static boolean checkIterable(List<? extends Type> types, Iterable<?> objects) {
    assert types != null;
    assert objects != null;
    
    Iterator<?> iterator = objects.iterator();
    
    for(Type type : types) {
      if(! iterator.hasNext() // the number of objects and types doesn't match
      || ! checkType(type, iterator.next())) // mismatch
        return false;
    }
    
    if(iterator.hasNext())
      return false; // the number of objects and types doesn't match 
    
    return true;
  }

  /**
   * Checks if the object's runtime type is compatible with the given type.
   * 
   * @param type a type. 
   * @param object an object.
   * @return if the object's runtime type is compatible with the given type. 
   */
  public static boolean checkType(Type type, Object object) {
    if(type == null) // nothing matches a null type
      return false;
    
    if(object == null) // everything else matches a null object
      return true;
    
    return GenericTypeReflector.isSuperType(type, object.getClass());
  }
  
  private static List<? extends Type> extractTypesFromCons(Type type) {
    assert type != null;
    
    List<Type> result = new ArrayList<Type>();
    
    // end of recursion, add it and return
    if(! (type instanceof ParameterizedType) 
    || ((ParameterizedType) type).getRawType() != C.class) {
      result.add(type);
      return result;
    }
    
    // recurse
    ParameterizedType cons = (ParameterizedType) type;
    java.lang.reflect.Type[] actualTypes = cons.getActualTypeArguments();
    
    result.add(actualTypes[0]);
    result.addAll(extractTypesFromCons(actualTypes[1]));
    
    return result;
  }
}
