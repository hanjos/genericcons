package org.sbrubbles.genericcons;

/**
 * A marker class, used to build and represent an open-ended list of types in a generic declaration.
 * Like below:
 * <p>
 * <pre>
 * Function&lt;?, ?&gt; f = new Function&lt;String, C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;() {
 *   // ...
 * };
 * </pre>
 * <p>
 * {@code C<Object, C<Number, C<String, Integer>>>>} encodes a list of types:
 * {@code Object}, {@code Number}, {@code String} and {@code Integer}. The {@link Types} class provides methods to
 * extract the types for later use.
 * <p>
 * This list-like structure is named a <i>cons type</i> (hence the {@code C}), due to its similarities to
 * Lisp's {@code cons}. Using only one letter for the class kept the whole structure more readable.
 * <p>
 * This class isn't supposed to be instantiated or subclassed, and has no fields or methods.
 * 
 * @author Humberto Anjos
 * @param <First> The first type.
 * @param <Rest> The last type, or a C holding the rest of the types.
 * @see Types
 */
public final class C<First, Rest> {
  private C() { /* preventing instantiation */ }
}
