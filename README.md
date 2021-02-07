![CI](https://github.com/hanjos/genericcons/workflows/CI/badge.svg) [![Javadocs](https://img.shields.io/static/v1?label=Javadocs&message=0.4&color=informational&logo=read-the-docs)][v0.4] [![Maven package](https://img.shields.io/static/v1?label=Maven&message=0.4&color=orange&logo=apache-maven)](https://github.com/hanjos/genericcons/packages/611536)

Generic Cons
------------

Who said Java generics can't accept an open-ended number of type variables? 

Instead of trying to write something like

```java
// doesn't compile!
Function<?, ?...> func = new Function<Integer, String, String, String, String>() {
  public Integer execute(String a, String b, String c, String d) {
    return Math.max(a.length(), b.length(), c.length(), d.length());
  }
};
```

and raging at your compiler in vain and frustration, write

```java
// works like a charm! Or should, haven't tested it :P
Function<?, ?> f = new Function<Integer, C<String, C<String, C<String, String>>>>() {
  { // the no-arg constructor
    this.types = Types.fromSuperclass(this.getClass(), 1);
  }
  
  public Integer execute(Object... objects) {
    if(! Types.check(this.types, Arrays.asList(objects))) // the given objects don't match!
  	  return -1;
  	    
    String a = (String) objects[0];
    String b = (String) objects[1];
    String c = (String) objects[2];
    String d = (String) objects[3];

    return Math.max(a.length(), b.length(), c.length(), d.length());
  }
};
```

and be happy!


That's a lot of work!
---------------------

Hey, I never said the solution was pretty. Or useful :)


How?
----

Some [type token][1] chicanery and good ol' [cons][2] for inspiration.


Why?
----

It occurred to me one day, and I haven't contributed to Java's grand tradition of gross overengineering for quite some time, so there you go :P

[1]: http://gafter.blogspot.com/2006/12/super-type-tokens.html
[2]: http://en.wikipedia.org/wiki/Cons
[v0.1]: https://sbrubbles.org/genericcons/docs/0.1/apidocs/index.html
[v0.2]: https://sbrubbles.org/genericcons/docs/0.2/apidocs/index.html
[v0.3]: https://sbrubbles.org/genericcons/docs/0.3/apidocs/index.html
[v0.4]: https://sbrubbles.org/genericcons/docs/0.4/apidocs/index.html
