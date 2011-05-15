package org.sbrubbles.genericcons;

import org.javaruntype.type.Type;
import org.javaruntype.type.Types;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Captures and represents an open-ended list of types. This class is built to 
 * be instantiated with anonymous subclasses, using Neal Gafter's 
 * <a href='http://gafter.blogspot.com/2006/12/super-type-tokens.html'>super 
 * type tokens</a> idea, like below:
 *
 * <pre>
 * Cons&lt;String, Cons&lt;Object[], Serializable&gt;&gt; cons =
 *   new Cons&lt;String, Cons&lt;Object[], Serializable&gt;&gt;() {}; 
 * // has to be an anonymous subclass!
 *
 * org.junit.Assert.assertArrayEquals(
 *   new Object[] { 
 *     org.javaruntype.type.Types.STRING, 
 *     org.javaruntype.type.Types.ARRAY_OF_OBJECT, 
 *     org.javaruntype.type.Types.SERIALIZABLE },
 *   cons.getTypes().toArray());
 * </pre>
 * 
 * Instances of this class are immutable. Named subclasses will throw an 
 * exception during instantiation.
 * 
 * @param <First> The first type.
 * @param <Rest> The last type, or a Cons holding the rest of the types.
 * 
 * @author Humberto Anjos
 */
public abstract class Cons<First, Rest> {
  private List<? extends Type<?>> types;
  
  /**
   * Detects and stores the types given at instantiation, which will be 
   * accessible at {@link #getTypes()}. Only works if it's called from an 
   * anonymous subclass.
   * 
   * @throws MissingTypeParametersException if the constructor can't find the 
   * type parameters.
   * @throws UnexpectedTypeException if this is not called from an anonymous 
   * subclass.
   * @throws InvalidTypeException if one of the types cannot be stored.
   */
  public Cons() 
  throws MissingTypeParametersException, UnexpectedTypeException, 
  InvalidTypeException {
    this.types = Collections.unmodifiableList(detectTypes());
  }

  private List<? extends Type<?>> detectTypes()
  throws MissingTypeParametersException, UnexpectedTypeException, 
  InvalidTypeException {
    java.lang.reflect.Type superclass = this.getClass().getGenericSuperclass();
    
    if(! (superclass instanceof ParameterizedType))
      throw new MissingTypeParametersException(this.getClass());
    
    ParameterizedType cons = (ParameterizedType) superclass;
    if(cons.getRawType() != Cons.class)
      throw new UnexpectedTypeException(cons, Cons.class);
    
    return getTypesFrom(cons);
  }

  private List<? extends Type<?>> getTypesFrom(java.lang.reflect.Type type)
  throws InvalidTypeException {
    assert type != null;
    
    List<Type<?>> result = new ArrayList<Type<?>>();
    
    // end of recursion, add it and return
    if(! (type instanceof ParameterizedType) 
    || ((ParameterizedType) type).getRawType() != Cons.class) {
      result.add(convertToFullType(type));
      return result;
    }
    
    // recurse
    ParameterizedType cons = (ParameterizedType) type;
    
    java.lang.reflect.Type[] actualTypes = cons.getActualTypeArguments();
    
    result.add(convertToFullType(actualTypes[0]));
    result.addAll(getTypesFrom(actualTypes[1]));
    
    return result;
  }

  private Type<?> convertToFullType(java.lang.reflect.Type type) 
  throws InvalidTypeException {
    try {
      return Types.forJavaLangReflectType(type);
    } catch (Throwable t) {
      throw new InvalidTypeException(type, t);
    }
  }

  /**
   * Returns the types detected at instantiation.
   * 
   * @return the types detected at instantiation.
   */
  public final List<? extends Type<?>> getTypes() {
    return types;
  }
  
  @Override
  public final boolean equals(Object obj) {
    if(this == obj)
      return true;
    
    // obj should be an anonymous subclass of Cons
    // so getClass() comparisons fail
    if(obj == null || ! (obj instanceof Cons))
      return false;
    
    final Cons<?, ?> other = (Cons<?, ?>) obj;
    return this.types.equals(other.types);
  }
  
  @Override
  public final int hashCode() {
    return this.types.hashCode();
  }
  
  @Override
  public final String toString() {
    String typesStr = this.types.toString();
    return "<" + typesStr.substring(1, typesStr.length() - 1) + ">";
  }
  
  // TODO check if a given argument list matches the types specified here
}
