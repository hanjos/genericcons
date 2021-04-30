package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeToken;
import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.sbrubbles.genericcons.fixtures.Utils.listOf;
import static org.sbrubbles.genericcons.fixtures.Utils.mapOf;

public class TypesFromTest {
  @Test
  public void oneNonConsParameter() {
    OneParameter<String> c = new OneParameter<String>() { /**/};

    ParameterizedType supertype = Types.genericSuperclassOf(c.getClass()).get();

    assertArrayEquals(
      new Object[] {String.class},
      Types.from(supertype, 0).toArray());

    assertEquals(
      Types.from(supertype, 0),
      Types.fromSuperclass(c.getClass(), 0));
  }

  @Test
  public <T> void oneNonConsParameterWithTypeVariable() {
    OneParameter<T> c = new OneParameter<T>() { /**/};
    Type t = new TypeToken<T>() { /**/}.getType();

    ParameterizedType supertype = Types.genericSuperclassOf(c.getClass()).get();

    assertArrayEquals(
      new Object[] {t},
      Types.from(supertype, 0).toArray());

    assertEquals(
      Types.from(supertype, 0),
      Types.fromSuperclass(c.getClass(), 0));
  }

  @Test
  public void oneConsParameter() {
    OneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> c =
      new OneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/};

    ParameterizedType actual = Types.genericSuperclassOf(c.getClass()).get();

    assertArrayEquals(
      new Object[] {
        String.class,
        Number.class,
        Object.class,
        new TypeToken<Object[]>() { /**/}.getType(),
        Serializable.class},
      Types.from(actual, 0).toArray());

    assertEquals(
      Types.from(actual, 0),
      Types.fromSuperclass(c.getClass(), 0));
  }

  @Test
  public void threeParametersWithCons() {
    ThreeParameters<Number, String, C<Serializable, Serializable>> cons =
      new ThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/};

    ParameterizedType supertype = Types.genericSuperclassOf(cons.getClass()).get();

    assertArrayEquals(
      new Object[] {Number.class},
      Types.from(supertype, 0).toArray());

    assertEquals(
      Types.from(supertype, 0),
      Types.fromSuperclass(cons.getClass(), 0));

    assertArrayEquals(
      new Object[] {String.class},
      Types.from(supertype, 1).toArray());

    assertEquals(
      Types.from(supertype, 1),
      Types.fromSuperclass(cons.getClass(), 1));

    assertArrayEquals(
      new Object[] {Serializable.class, Serializable.class},
      Types.from(supertype, 2).toArray());

    assertEquals(
      Types.from(supertype, 2),
      Types.fromSuperclass(cons.getClass(), 2));
  }

  @Test
  public void consWithCompositeTypes() {
    OneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> cons
      = new OneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/};

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertArrayEquals(
      new Object[] {
        String.class,
        listOf(Number.class),
        Object.class,
        mapOf(String.class, Integer.class)},
      Types.from(actual, 0).toArray());

    assertEquals(
      Types.from(actual, 0),
      Types.fromSuperclass(cons.getClass(), 0));
  }

  @Test
  public void sonOfParameterizedType() {
    OneParameter<String> cons = new SonOfOneParameter();
    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertArrayEquals(
      new Object[] {String.class},
      Types.from(actual, 0).toArray());

    assertEquals(
      Types.from(actual, 0),
      Types.fromSuperclass(cons.getClass(), 0));
  }

  @Test(expected = ParameterizedTypeNotFoundException.class)
  public void grandsonOfParameterizedType() {
    OneParameter<String> cons = new SonOfOneParameter() { /**/};

    Types.fromSuperclass(cons.getClass(), 0);
  }

  @Test
  public void nullType() {
    try {
      Types.from(null, 0);
      fail("Types.from should've thrown NullPointerException");
    } catch (NullPointerException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(null, 0);
      fail("Types.fromSuperclass should've thrown NullPointerException");
    } catch (NullPointerException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromInterface(null, 0);
      fail("Types.fromInterface should've thrown NullPointerException");
    } catch (NullPointerException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void invalidIndex() {
    OneParameter<String> cons = new OneParameter<String>() { /**/};

    try {
      Types.from(Types.genericSuperclassOf(cons.getClass()).get(), -1);
      fail("Types.from with genericSuperclassOf should've thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(cons.getClass(), -1);
      fail("Types.fromSuperclass should've thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      /* if we're here, we're good */
    }
  }

  @Test(expected = ParameterizedTypeNotFoundException.class)
  public void fromSuperclassDoesntWorkWithInterfaces() {
    IOneParameter<String> cons = new IOneParameter<String>() { /**/};
    Types.fromSuperclass(cons.getClass(), 0);
  }

  @Test
  public void fromInterfaceWorksWithInterfaces() {
    IOneParameter<String> cons = new IOneParameter<String>() { /**/};

    ParameterizedType actual = Types.genericInterfaceOf(cons.getClass(), 0).get();

    assertArrayEquals(
      new Object[] {String.class},
      Types.from(actual, 0).toArray());

    assertEquals(
      Types.from(actual, 0),
      Types.fromInterface(cons.getClass(), 0));
  }

  @Test
  public void fromInterfaceGetsOnlyTheFirstInterface() {
    ClassWithMultipleInterfaces c = new ClassWithMultipleInterfaces();
    ParameterizedType actual = Types.genericInterfaceOf(c.getClass(), 0).get();

    assertArrayEquals(
      new Object[] {String.class},
      Types.from(actual, 0).toArray());

    assertEquals(
      Types.from(actual, 0),
      Types.fromInterface(c.getClass(), 0));
  }

  @Test
  public void nonParameterizedSupertypes() {
    try {
      Types.fromSuperclass(String.class, 0);
      fail("Types.fromSuperclass should've thrown ParameterizedTypeNotFoundException");
    } catch (ParameterizedTypeNotFoundException e) {
      /* if we're here, we're good */
    }

    try {
      // XXX the first interface is Serializable, which is not generic, so this call should fail.
      // Comparable<String> is the second interface
      Types.fromInterface(String.class, 0);
      fail("Types.fromInterface should've thrown ParameterizedTypeNotFoundException");
    } catch (ParameterizedTypeNotFoundException e) {
      /* if we're here, we're good */
    }
  }
}
