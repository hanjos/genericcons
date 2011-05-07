package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

public class InvalidTypeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private Type type;

  private static String buildMessage(Type type, Throwable cause) {
    return "Problem while using type " + type + ": " + cause;
  }

  public InvalidTypeException(Type type) {
    this(type, null);
  }

  public InvalidTypeException(Type type, Throwable cause) {
    super(buildMessage(type, cause), cause);
    this.type = type;
  }
  
  public Type getType() {
    return type;
  }
}
