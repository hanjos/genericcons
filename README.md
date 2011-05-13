Generic Cons
------------

Who said Java generics can't accept an open-ended number of type variables? 

Instead of trying to write something like
```java
// doesn't compile!
Function <?, ?> func = new Function <Integer, String, String, String, String>() {
  public Integer execute(String a, String b, String c, String d) {
    return Math.max(a.length(), b.length(), c.length(), d.length());
  }
};
```

and raging at your compiler in vain and frustration, write

```java
// works like a charm! Or should, haven't tested it
Function <?, ?> func = new Function <Integer, Cons<String, Cons<String, Cons<String, String>>>>() {
  {
    super(Integer.class, new Cons<String, Cons<String, Cons<String, String>>>() {});
  }

  public Integer execute(Object... objects) {
    String a = (String) objects[0];
    String b = (String) objects[1];
    String c = (String) objects[2];
    String d = (String) objects[3];

    return Math.max(a.length(), b.length(), c.length(), d.length());
  }
};
```

and be happy!


That's actually more work!
--------------------------

Hey, I never said the solution was pretty :)


Why?
----

It occurred to me one day, and I haven't contributed to Java's grand tradition of gross overengineering for quite some time, so there you go :P

