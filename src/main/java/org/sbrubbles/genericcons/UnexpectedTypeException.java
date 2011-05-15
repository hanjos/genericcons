package org.sbrubbles.genericcons;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Thrown when a new type which was not expected is given.
 * 
 * @author Humberto Anjos
 */
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

  /**
   * Instantiates a new exception.
   * 
   * @param actual the type given.
   * @param expected the types which were expected.
   */
  public UnexpectedTypeException(Type actual, Type... expected) {
    super(buildMessage(actual, expected));
    
    this.actual = actual;
    this.expected = Collections.unmodifiableList(Arrays.asList(expected));
  }

  /**
   * Returns the given type.
   * 
   * @return the given type.
   */
  public Type getActual() {
    return actual;
  }

  /**
   * Returns the expected types.
   * 
   * @return the expected types.
   */
  public List<Type> getExpected() {
    return expected;
  }
}
