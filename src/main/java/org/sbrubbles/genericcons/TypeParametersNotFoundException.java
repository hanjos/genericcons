package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

/**
 * Thrown when type parameters aren't found in the given base type. 
 * 
 * @author Humberto Anjos
 */
public class TypeParametersNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Type baseType;

  private static String buildMessage(Type baseType) {
    return "Type parameters missing in " + baseType;
  }

  /**
   * Instantiates a new exception.
   * 
   * @param baseType the base type.
   */
  public TypeParametersNotFoundException(Type baseType) {
    this(baseType, null);
  }

  /**
   * Instantiates a new exception.
   * 
   * @param baseType the base type.
   * @param cause the reason why the type parameters were not found.
   */
  public TypeParametersNotFoundException(Type baseType, Throwable cause) {
    super(buildMessage(baseType), cause);
    
    this.baseType = baseType;
  }

  /**
   * Returns the given base type.
   * 
   * @return the given base type.
   */
  public Type getBaseType() {
    return baseType;
  }
}
