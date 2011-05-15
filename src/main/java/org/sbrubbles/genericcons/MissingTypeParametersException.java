package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

/**
 * Thrown when type parameters aren't found in the given base type. 
 * 
 * @author Humberto Anjos
 */
public class MissingTypeParametersException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Type baseType;

  private static String buildMessage(Type type) {
    return "Type parameters missing in " + type;
  }

  /**
   * Instantiates a new exception.
   * 
   * @param baseType the base type.
   */
  public MissingTypeParametersException(Type baseType) {
    super(buildMessage(baseType));
    
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
