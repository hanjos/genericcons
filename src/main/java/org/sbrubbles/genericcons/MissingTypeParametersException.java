package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

public class MissingTypeParametersException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private Type baseType;

  private static String buildMessage(Type type) {
    return "Type parameters missing in " + type;
  }

  public MissingTypeParametersException(Type baseType) {
    super(buildMessage(baseType));
    
    this.baseType = baseType;
  }

  public Type getBaseType() {
    return baseType;
  }
}
