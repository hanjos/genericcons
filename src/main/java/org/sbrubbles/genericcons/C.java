package org.sbrubbles.genericcons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Captures and represents an open-ended list of types. This class isn't 
 * supposed to be instantiated or subclassed; it's used to build the list in 
 * the type declaration, like below:
 * 
 * <pre>
 * Function&lt;?, ?&gt; f = new Function&lt;String, C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;() {
 *   { // the no-arg constructor
 *     this.types = Types.extractFromSuperclass(this.getClass(), 1);
 *   }
 * };
 * </pre>
 * 
 * This list-like structure, similar to Lisp's {@code cons}, is named a <i>cons type</i> (hence the {@code C}), 
 * but using only one letter kept the whole structure (slightly) more readable.
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
  public interface TypeChecker {
    /**
     * Checks if the given objects are compatible with the types held by this instance.
     * 
     * @param objects the given objects to check.
     * @return if the given objects are type-compatible with the types held by this instance.
     */
    boolean check(Iterable<?> objects);
  }
  
  /**
   * Holds an iterable of types for checking.
   */
  private static class TypeCheckerImpl implements TypeChecker {
    private final Iterable<? extends Type> types;

    public TypeCheckerImpl(Type... types) throws IllegalArgumentException {
      if(types == null || types.length == 0) {
        throw new IllegalArgumentException("No types given!");
      }

      this.types = Arrays.asList(types);
    }
    
    public TypeCheckerImpl(Iterable<? extends Type> types) throws IllegalArgumentException {
      if(types == null) {
        throw new IllegalArgumentException("No types given!");
      }

      List<Type> copy = new ArrayList<>();
      for(Type type : types) {
        copy.add(type);
      }

      if(copy.isEmpty()) {
        throw new IllegalArgumentException("No types given!");
      }

      this.types = copy;
    }

    @Override
    public boolean check(Iterable<?> objects) {
      if(objects == null) {
        return false; // empty iterator never checks true
      }

      Iterator<?> iterator = objects.iterator();

      for(Type type : types) {
        if(! iterator.hasNext() // the amount of types and objects doesn't match
          || ! Types.check(type, iterator.next())) {
          return false;
        }
      }

      return ! iterator.hasNext(); // the amount of types and objects must match
    }    
  }
  
  /**
   * Returns an object which uses the given types for type checking.
   * 
   * @param types the types used for checking.
   * @return a type checker.
   * @throws IllegalArgumentException if {@code types} is null or empty.
   */
  public static TypeChecker types(Iterable<? extends Type> types) throws IllegalArgumentException {
    return new TypeCheckerImpl(types);
  }
  
  /**
   * Returns an object which uses the given types for type checking.
   * 
   * @param types the types used for checking.
   * @return a type checker.
   * @throws IllegalArgumentException if {@code types} is null or empty.
   */
  public static TypeChecker types(Type... types) throws IllegalArgumentException {
    return new TypeCheckerImpl(types);
  }
}
