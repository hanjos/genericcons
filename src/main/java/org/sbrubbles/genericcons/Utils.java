package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

import com.googlecode.gentyref.GenericTypeReflector;

/**
 * Provides a namespace for general utility methods. It's not intended to be instantiated or inherited from.
 * 
 * @author Humberto Anjos
 */
public final class Utils {
  private Utils() { /* preventing instantiation */ }
  
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
   * Checks if the given object is null, throwing an exception if so or returning it unchanged if not.
   * 
   * @param object an object.
   * @param <T> the given object's type.
   * @return the given object, unchanged, if it's not null.
   * @throws IllegalArgumentException if the given object is null.
   */
  public static <T> T nonNull(T object) throws IllegalArgumentException {
    if(object == null)
      throw new IllegalArgumentException("null value is invalid!");
    
    return object;
  }

}
