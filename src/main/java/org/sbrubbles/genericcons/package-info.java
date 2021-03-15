/**
 * A Lisp-inspired trick to enable an open-ended number of type variables.
 * <p>
 * This package offers two main classes: {@link org.sbrubbles.genericcons.C C}, which builds the type list like Lisp's
 * <a href="http://en.wikipedia.org/wiki/Cons">cons</a>, and {@link org.sbrubbles.genericcons.Types Types}, which
 * provides methods for extracting type information.
 * <p>
 * Basic usage:
 * <pre>
 * A&lt;?, ?&gt; f = new A&lt;Integer, C&lt;String, C&lt;String, String&gt;&gt;&gt;() {
 *   public Integer execute(Object... objects) {
 *     List&lt;? extends Type&gt; types = Types.fromSuperclass(this.getClass(), 1);
 *
 *     if(! Types.check(types, Arrays.asList(objects))) {
 *       return -1;
 *     }
 *
 *     String a = (String) objects[0];
 *     String b = (String) objects[1];
 *     String c = (String) objects[2];
 *
 *     return Math.max(a.length(), b.length(), c.length());
 *   }
 * };
 * </pre>
 *
 * @author Humberto Anjos
 */
package org.sbrubbles.genericcons;