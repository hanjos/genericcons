package org.sbrubbles.genericcons;

/**
 * Captures and represents an open-ended list of types. This class isn't supposed to be instantiated or subclassed, 
 * and has no fields or methods; it's a marker class, used to indicate a list in the type declaration, like below:
 * <p>
 * <pre>
 * Function&lt;?, ?&gt; f = new Function&lt;String, C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;() {
 *   { // the no-arg constructor
 *     this.types = Types.fromSuperclass(this.getClass(), 1);
 *   }
 * };
 * </pre>
 * <p>
 * In this example, {@code C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;} encodes that {@code Function}'s
 * second type argument is a list of types: {@code Object}, {@code Number}, {@code String} and {@code Integer}. The 
 * {@link Types} class provides static methods to extract the types. 
 * <p>
 * This list-like structure, similar to Lisp's {@code cons}, is named a <i>cons type</i> (hence the {@code C}), 
 * but using only one letter kept the whole structure more readable.
 * <p>
 * 
 * @author Humberto Anjos
 * @param <First> The first type.
 * @param <Rest> The last type, or a C holding the rest of the types.
 * @see Types
 */
public final class C<First, Rest> {
  private C() { /* preventing instantiation */ }
}
