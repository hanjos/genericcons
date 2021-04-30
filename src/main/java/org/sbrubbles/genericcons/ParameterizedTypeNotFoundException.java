package org.sbrubbles.genericcons;

/**
 * Indicates that an expected parameterized type wasn't found.
 *
 * @author Humberto Anjos
 */
public class ParameterizedTypeNotFoundException extends RuntimeException {
  /**
   * Constructs a ParameterizedTypeNotFoundException with a default error message.
   */
  public ParameterizedTypeNotFoundException() {
    this("No parameterized type found");
  }

  /**
   * Constructs a ParameterizedTypeNotFoundException with the given error message.
   *
   * @param message the given error message.
   */
  public ParameterizedTypeNotFoundException(String message) {
    super(message);
  }
}
