package org.sbrubbles.genericcons;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnexpectedTypeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Type actual;
  private final List<Type> expected;
  
  private static String buildMessage(Type actual, Type... expected) {
    StringBuilder result = new StringBuilder("Got ").append(actual).append(", expected ");
    
    if(expected == null || expected.length == 0) {
      return result.append("one of []").toString();
    } else if(expected.length == 1) {
      return result.append(expected[0]).toString();
    }
    
    return result.append("one of ").append(Arrays.asList(expected)).toString();
  }

  public UnexpectedTypeException(Type actual, Type... expected) {
    super(buildMessage(actual, expected));
    
    this.actual = actual;
    this.expected = Collections.unmodifiableList(Arrays.asList(expected));
  }

  public Type getActual() {
    return actual;
  }

  public List<Type> getExpected() {
    return expected;
  }
}
