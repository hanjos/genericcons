package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeFactory;
import com.coekie.gentyref.TypeToken;
import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TypesFromTest {
  @Test
  public void oneNonConsParameter() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };
    Object[] expected = new Object[] { String.class };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, String.class),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public <T> void oneNonConsParameterWithTypeVariable() {
    JustOneParameter<T> cons = new JustOneParameter<T>() { /**/ };
    Type t = new TypeToken<T>() { /**/ }.getType();
    Object[] expected = { t };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, t),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithTwoTypes() {
    JustOneParameter<C<String, Number>> cons =
      new JustOneParameter<C<String, Number>>() { /**/ };
    Type typeList = encodeCons(String.class, Number.class);
    Object[] expected = { String.class, Number.class };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, typeList),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithFiveTypes() {
    JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> cons =
      new JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/ };
    Type typeList = encodeCons(String.class, Number.class, Object.class, Object[].class, Serializable.class);
    Object[] expected = {
      String.class,
      Number.class,
      Object.class,
      new TypeToken<Object[]>() { /**/ }.getType(),
      Serializable.class };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, typeList),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void threeParameters() {
    JustThreeParameters<Number, String, C<Serializable, Serializable>> cons =
      new JustThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustThreeParameters.class,
        Number.class,
        String.class,
        encodeCons(Serializable.class, Serializable.class)),
      Types.genericSuperclassOf(cons.getClass()));

    Object[] expected_1 = { String.class };

    assertArrayEquals(
      expected_1,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 1).toArray());

    assertArrayEquals(
      expected_1,
      Types.fromSuperclass(cons.getClass(), 1).toArray());

    Object[] expected_2 = { Serializable.class, Serializable.class };

    assertArrayEquals(
      expected_2,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 2).toArray());

    assertArrayEquals(
      expected_2,
      Types.fromSuperclass(cons.getClass(), 2).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithFiveTypes() {
    JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String> cons =
      new JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String>() { /**/ };
    Object[] expected = {
      String.class,
      Number.class,
      Object.class,
      TypeFactory.arrayOf(Object.class),
      Serializable.class };

    assertEquals(
      TypeFactory.parameterizedClass(JustThreeParameters.class,
        encodeCons(String.class, Number.class, Object.class, Object[].class, Serializable.class),
        String.class,
        String.class),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneParameterWithConsWithCompositeTypes() {
    JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> cons
      = new JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/ };
    Object[] expected = {
      String.class,
      listOf(Number.class),
      Object.class,
      mapOf(String.class, Integer.class) };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class,
        encodeCons(String.class,
          listOf(Number.class),
          Object.class,
          mapOf(String.class, Integer.class))),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void sonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter();
    Object[] expected = { String.class };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, String.class),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
      expected,
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void grandsonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter() { /**/ };

    assertNull(Types.genericSuperclassOf(cons.getClass()));

    try {
      Types.from(cons.getClass(), Types::genericSuperclassOf, 0);
      fail("Types.from should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(cons.getClass(), 0);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void nullBaseClass() {
    assertNull(Types.genericSuperclassOf(null));

    try {
      Types.from(null, Types::genericSuperclassOf, 0);
      fail("Types.from with genericSuperclassOf should've thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.from(null, Types.genericInterfaceAt(0), 0);
      fail("Types.from with genericInterface should've thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(null, 0);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void invalidIndex() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, String.class),
      Types.genericSuperclassOf(cons.getClass()));

    try {
      Types.from(cons.getClass(), Types::genericSuperclassOf, -1);
      fail("Types.from with genericSuperclassOf should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.from(cons.getClass(), Types.genericInterfaceAt(0), -1);
      fail("Types.from with genericInterface should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(cons.getClass(), -1);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSelectorGiven() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    Types.from(cons.getClass(), null, 0);
  }

  @Test
  public void nonParameterizedSuperclass() {
    assertNull(Types.genericSuperclassOf(String.class));

    try {
      Types.from(String.class, Types::genericSuperclassOf,-1);
      fail("Types.from with genericSuperclassOf should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(String.class, -1);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void genericSuperclassOfDoesntWorkWithInterfaces() {
    JustOneParameterInterface<String> cons = new JustOneParameterInterface<String>() { /**/ };

    assertNull(Types.genericSuperclassOf(cons.getClass()));

    try {
      Types.from(cons.getClass(), Types::genericSuperclassOf, -1);
      fail("Types.from with genericSuperclassOf should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromSuperclass(cons.getClass(), -1);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void genericInterfaceWorksWithInterfaces() {
    JustOneParameterInterface<String> cons = new JustOneParameterInterface<String>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameterInterface.class, String.class),
      Types.genericInterfaceOf(cons.getClass(), 0));

    assertArrayEquals(
      new Object[] { String.class },
      Types.from(cons.getClass(), Types.genericInterfaceAt(0), 0).toArray());
  }

  @Test(expected = IllegalArgumentException.class)
  public void genericInterfaceFailsIfNoInterfaceIsFoundAtIndex() {
    JustOneParameterInterface<String> cons = new JustOneParameterInterface<String>() { /**/ };

    assertNull(Types.genericInterfaceOf(cons.getClass(), 1));

    Types.from(cons.getClass(), Types.genericInterfaceAt(1), 0);
  }

  public void gettingTheRightGenericSuperinterface() {
    ClassWithMultipleInterface<String, Integer, List<Double>> cons = new ClassWithMultipleInterface<>();

    assertEquals(String.class, Types.genericInterfaceOf(cons.getClass(), 0));
    assertEquals(String.class, Types.genericInterfaceAt(0).genericSupertypeOf(cons.getClass()));

    assertEquals(Integer.class, Types.genericInterfaceOf(cons.getClass(), 1));
    assertEquals(Integer.class, Types.genericInterfaceAt(1).genericSupertypeOf(cons.getClass()));

    assertEquals(listOf(Double.class), Types.genericInterfaceOf(cons.getClass(), 2));
    assertEquals(listOf(Double.class), Types.genericInterfaceAt(2).genericSupertypeOf(cons.getClass()));
  }

  public void invalidIndexGenericSuperinterface() {
    ClassWithMultipleInterface<String, Integer, List<Double>> cons = new ClassWithMultipleInterface<>();

    assertNull(Types.genericInterfaceOf(cons.getClass(), -1));
    assertNull(Types.genericInterfaceAt(-1).genericSupertypeOf(cons.getClass()));

    assertNull(Types.genericInterfaceOf(cons.getClass(), 3));
    assertNull(Types.genericInterfaceAt(3).genericSupertypeOf(cons.getClass()));
  }

  // doesn't work for length == 0, but close enough
  private Type encodeCons(Type... types) {
    if (types.length == 1) {
      return types[0];
    }

    Type[] rest = Arrays.copyOfRange(types, 1, types.length);
    return TypeFactory.parameterizedClass(C.class, types[0], encodeCons(rest));
  }

  private Type listOf(Type type) {
    return TypeFactory.parameterizedClass(List.class, type);
  }

  private Type mapOf(Type key, Type value) {
    return TypeFactory.parameterizedClass(Map.class, key, value);
  }
}
