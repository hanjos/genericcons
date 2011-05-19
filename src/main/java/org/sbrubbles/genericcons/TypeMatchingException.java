package org.sbrubbles.genericcons;

import org.javaruntype.type.Type;

/**
 * Thrown when a problem occurs while matching a {@link Type full type} to an object.
 * 
 * @author Humberto Anjos
 */
public class TypeMatchingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Type<?> fullType;
  private final Object object;
  
  private static String buildMessage(Type<?> fullType, Object object, Throwable cause) {
    return "Problem while matching type " + fullType + " with object " + object + ": " + cause;
  }

  /**
   * Instantiates a new exception with an unknown cause.
   * 
   * @param fullType the full type.
   * @param object the object.
   */
  public TypeMatchingException(Type<?> fullType, Object object) {
    this(fullType, object, null);
  }
  
  /**
   * Instantiates a new exception with the given cause.
   * 
   * @param fullType the full type.
   * @param object the object.
   * @param cause the throwable which caused the problem.
   */
  public TypeMatchingException(Type<?> fullType, Object object, Throwable cause) {
    super(buildMessage(fullType, object, cause), cause);
    
    this.fullType = fullType;
    this.object = object;
  }

  /**
   * Returns the full type used to match.
   * 
   * @return the full type used to match.
   */
  public Type<?> getFullType() {
    return fullType;
  }

  /**
   * Returns the object used to match.
   * 
   * @return the object used to match.
   */
  public Object getObject() {
    return object;
  }
}
