/**
 * A Lisp-inspired trick to enable an open-ended number of type variables.
 * <p>
 * This package offers two main classes: {@link org.sbrubbles.genericcons.C C}, which builds the type list like Lisp's
 * <a href="http://en.wikipedia.org/wiki/Cons">cons</a>, and {@link org.sbrubbles.genericcons.Types Types}, which
 * provides methods for extracting type information.
 * <p>
 * Basic usage:
 * <pre>
 * Function&lt;?, ?&gt; f = new Function&lt;Integer, C&lt;String, C&lt;String, C&lt;String, String&gt;&gt;&gt;&gt;() {
 *   { // the no-arg constructor
 *     this.types = Types.fromSuperclass(this.getClass(), 1);
 *   }
 *
 *   public Integer execute(Object... objects) {
 *     if(! Types.check(this.types, Arrays.asList(objects))) { // the given objects don't match!
 *   	 return -1;
 *     }
 *
 *     String a = (String) objects[0];
 *     String b = (String) objects[1];
 *     String c = (String) objects[2];
 *     String d = (String) objects[3];
 *
 *     return Math.max(a.length(), b.length(), c.length(), d.length());
 *   }
 * };
 * </pre>
 *
 * @author Humberto Anjos
 */
package org.sbrubbles.genericcons;