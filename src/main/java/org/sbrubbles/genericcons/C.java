package org.sbrubbles.genericcons;

import static org.sbrubbles.genericcons.Utils.checkType;
import static org.sbrubbles.genericcons.Utils.nonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


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
  private C() { /* preventing instantiation */ }
  
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
   * Holds an iterable of types for checking.
   */
  private static class TypeCheckerImpl implements TypeChecker {
    private final Iterable<? extends Type> types;

    public TypeCheckerImpl(Type... types) throws IllegalArgumentException {
      this(Arrays.asList(nonNull(types)));
    }
    
    public TypeCheckerImpl(Iterable<? extends Type> types) throws IllegalArgumentException {
      this.types = nonNull(types);
    }
    
    @Override
    public boolean onVarargs(Object... objects) throws IllegalArgumentException {
      return checkIterable(types, Arrays.asList(nonNull(objects)));
    }

    @Override
    public boolean onIterable(Iterable<?> objects) throws IllegalArgumentException {
      return checkIterable(types, nonNull(objects));
    }    
  }
  
  /**
   * Returns an object which uses the given types for type checking.
   * 
   * @param types the types used for checking.
   * @return a type checker.
   * @throws IllegalArgumentException if {@code types} is null. 
   */
  public static TypeChecker check(Iterable<? extends Type> types) throws IllegalArgumentException {
    return new TypeCheckerImpl(types);
  }
  
  /**
   * Returns an object which uses the given types for type checking.
   * 
   * @param types the types used for checking.
   * @return a type checker.
   * @throws IllegalArgumentException if {@code types} is null. 
   */
  public static TypeChecker check(Type... types) throws IllegalArgumentException {
    return new TypeCheckerImpl(types);
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
    
    try {
      return extractTypesFromCons(((ParameterizedType) superclass).getActualTypeArguments()[parameterIndex]);
    } catch (IndexOutOfBoundsException e) {
      throw new TypeParametersNotFoundException(baseClass, e);
    }
  }
 
  private static boolean checkIterable(Iterable<? extends Type> types, Iterable<?> objects) {
    assert types != null;
    assert objects != null;
    
    Iterator<?> iterator = objects.iterator();
    
    for(Type type : types) {
      if(! iterator.hasNext() // the amount of types and objects doesn't match
      || ! checkType(type, iterator.next()))
        return false;
    }
    
    return ! iterator.hasNext(); // the amount of types and objects must match
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
