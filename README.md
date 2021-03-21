![CI](https://github.com/hanjos/genericcons/workflows/CI/badge.svg) [![Javadocs](https://img.shields.io/static/v1?label=Javadocs&message=0.5&color=informational&logo=read-the-docs)][v0.5] [![Maven package](https://img.shields.io/static/v1?label=Maven&message=0.5&color=orange&logo=apache-maven)](https://github.com/hanjos/genericcons/packages/611536)

Who said Java generics can't accept an open-ended number of type variables? Just write something like

```java
Function<?, ?> f = new Function<Integer, C<String, C<String, String>>>() {
  public Integer execute(Object... objects) {
    List<? extends Type> types = Types.fromSuperclass(this.getClass(), 1);
    
    if(! Types.check(types, Arrays.asList(objects))) { // the given objects don't match!
  	  return -1;
    }
  	    
    String a = (String) objects[0];
    String b = (String) objects[1];
    String c = (String) objects[2];

    return Math.max(a.length(), b.length(), c.length());
  }
};
```

Hey, I never said the solution was pretty. Or useful :)

# How?

Some [type token][1] chicanery and good ol' [cons][2] for inspiration.

# Why?

It occurred to me one day, and I haven't contributed to Java's grand tradition of gross overengineering for quite some time, so there you go :P

# Usage?

Basically, [Maven][apache-maven] and [GitHub Packages][github-packages]:

1. Add https://maven.pkg.github.com/hanjos/genericcons as a repo in your `pom.xml`;
1. Setup a [personal access token][pat] in your `settings.xml` with package reading rights;
1. Add the dependency in your `pom.xml`. The versions available can be seen [here][packages].
1. And you're good to go!

Of course, one can always download the code and `mvn install`...

[1]: http://gafter.blogspot.com/2006/12/super-type-tokens.html
[2]: http://en.wikipedia.org/wiki/Cons
[v0.5]: https://sbrubbles.org/genericcons/docs/0.5/apidocs/index.html
[apache-maven]: https://maven.apache.org/
[packages]: https://github.com/hanjos/genericcons/packages
[pat]: https://docs.github.com/en/packages/guides/configuring-apache-maven-for-use-with-github-packages#authenticating-with-a-personal-access-token
[github-packages]: https://docs.github.com/en/packages/guides/configuring-apache-maven-for-use-with-github-packages
