package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

/**
 * Thrown when the given basic {@link Type} instance cannot be converted to a 
 * {@linkplain org.javaruntype.type.Type fuller representation}.
 * 
 * @author Humberto Anjos
 */
public class TypeConversionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Type type;

  private static String buildMessage(Type type, Throwable cause) {
    return "Problem while using type " + type + ": " + cause;
  }

  /**
   * Instantiates a new exception.
   * 
   * @param type the type which could not be converted.
   * @param cause the throwable which caused the problem.
   */
  public TypeConversionException(Type type, Throwable cause) {
    super(buildMessage(type, cause), cause);
    this.type = type;
  }
  
  /**
   * Returns the type which could not be converted.
   * 
   * @return the type which could not be converted.
   */
  public Type getType() {
    return type;
  }
}
