package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

/**
 * Thrown when the given {@link Type} instance cannot be converted to a fuller
 * type representation.
 * 
 * @author Humberto Anjos
 */
public class InvalidTypeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Type type;

  private static String buildMessage(Type type, Throwable cause) {
    return "Problem while using type " + type + ": " + cause;
  }

  /**
   * Instantiates a new exception with an unknown cause.
   * 
   * @param type the type which could not be converted.
   */
  public InvalidTypeException(Type type) {
    this(type, null);
  }

  /**
   * Instantiates a new exception with an known cause.
   * 
   * @param type the type which could not be converted.
   * @param cause the throwable which caused the problem.
   */
  public InvalidTypeException(Type type, Throwable cause) {
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
