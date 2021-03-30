package org.sbrubbles.genericcons;

import java.lang.reflect.Type;

/**
 * A marker class, used to encode an open-ended list of types in a generic declaration.
 * Like below:
 * <p>
 * <pre>
 * Fn&lt;?, ?&gt; f = new Fn&lt;String, C&lt;Object, C&lt;Number, C&lt;String, Integer&gt;&gt;&gt;&gt;() {
 *   // ...
 * };
 * </pre>
 * <p>
 * {@code C<Object, C<Number, C<String, Integer>>>>} represents a list of types:
 * {@code Object}, {@code Number}, {@code String} and {@code Integer}.
 * <p>
 * This list-like structure is named a <i>cons</i> (hence the {@code C}), due to its similarities to
 * Lisp's {@code cons}. Using only one letter kept the whole thing more readable (hey, it could be worse).
 * <p>
 * The {@link Types} class provides methods to {@linkplain Types#fromCons(Type) extract
 * types from conses}.
 * <p>
 * This class isn't supposed to be instantiated or subclassed, and declares no fields or methods.
 * 
 * @author Humberto Anjos
 * @param <First> The first type.
 * @param <Rest> The last type, or a C holding the rest of the types.
 * @see Types
 */
public final class C<First, Rest> {
  private C() { /* preventing instantiation */ }
}
