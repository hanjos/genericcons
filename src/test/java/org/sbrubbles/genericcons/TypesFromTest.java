package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeFactory;
import com.coekie.gentyref.TypeToken;
import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class TypesFromTest {
  @Test
  public void oneNonConsParameter() {
    OneParameter<String> cons = new OneParameter<String>() { /**/ };
    Object[] expected = new Object[] { String.class };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class, String.class),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public <T> void oneNonConsParameterWithTypeVariable() {
    OneParameter<T> cons = new OneParameter<T>() { /**/ };
    Type t = new TypeToken<T>() { /**/ }.getType();
    Object[] expected = { t };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class, t),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithTwoTypes() {
    OneParameter<C<String, Number>> cons =
      new OneParameter<C<String, Number>>() { /**/ };
    Type typeList = encodeCons(String.class, Number.class);
    Object[] expected = { String.class, Number.class };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class, typeList),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithFiveTypes() {
    OneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> cons =
      new OneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/ };
    Type typeList = encodeCons(String.class, Number.class, Object.class, Object[].class, Serializable.class);
    Object[] expected = {
      String.class,
      Number.class,
      Object.class,
      new TypeToken<Object[]>() { /**/ }.getType(),
      Serializable.class };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class, typeList),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void threeParameters() {
    ThreeParameters<Number, String, C<Serializable, Serializable>> cons =
      new ThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/ };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(ThreeParameters.class,
        Number.class,
        String.class,
        encodeCons(Serializable.class, Serializable.class)),
      actual);

    Object[] expected_1 = { String.class };

    assertArrayEquals(
      expected_1,
      Types.from(actual, 1).toArray());

    assertArrayEquals(
      expected_1,
      Types.fromSuperclass(cons.getClass(), 1).toArray());

    Object[] expected_2 = { Serializable.class, Serializable.class };

    assertArrayEquals(
      expected_2,
      Types.from(actual, 2).toArray());

    assertArrayEquals(
      expected_2,
      Types.fromSuperclass(cons.getClass(), 2).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithFiveTypes() {
    ThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String> cons =
      new ThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String>() { /**/ };
    Object[] expected = {
      String.class,
      Number.class,
      Object.class,
      TypeFactory.arrayOf(Object.class),
      Serializable.class };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(ThreeParameters.class,
        encodeCons(String.class, Number.class, Object.class, Object[].class, Serializable.class),
        String.class,
        String.class),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneParameterWithConsWithCompositeTypes() {
    OneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> cons
      = new OneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/ };
    Object[] expected = {
      String.class,
      listOf(Number.class),
      Object.class,
      mapOf(String.class, Integer.class) };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class,
        encodeCons(String.class,
          listOf(Number.class),
          Object.class,
          mapOf(String.class, Integer.class))),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void sonOfParameterizedType() {
    OneParameter<String> cons = new SonOfOneParameter();
    Object[] expected = { String.class };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class, String.class),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void grandsonOfParameterizedType() {
    OneParameter<String> cons = new SonOfOneParameter() { /**/ };

    Optional<ParameterizedType> actual = Types.genericSuperclassOf(cons.getClass());

    assertFalse(actual.isPresent());

    try {
      Types.fromSuperclass(cons.getClass(), 0);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void nullBaseClass() {
    assertFalse(Types.genericSuperclassOf(null).isPresent());
    assertFalse(Types.genericInterfaceOf(null, 0).isPresent());

    try {
      Types.fromSuperclass(null, 0);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      /* if we're here, we're good */
    }

    try {
      Types.fromInterface(null, 0);
      fail("Types.fromInterface should've thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void invalidIndex() {
    OneParameter<String> cons = new OneParameter<String>() { /**/ };

    ParameterizedType actual = Types.genericSuperclassOf(cons.getClass()).get();

    assertEquals(
      TypeFactory.parameterizedClass(OneParameter.class, String.class),
      actual);

    try {
      Types.from(actual, -1);
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

  @Test(expected = IllegalArgumentException.class)
  public void noTypeGiven() {
    Types.from(null, 0);
  }

  @Test
  public void nonParameterizedSuperclass() {
    Optional<ParameterizedType> actual = Types.genericSuperclassOf(String.class);

    assertFalse(actual.isPresent());

    try {
      Types.fromSuperclass(String.class, -1);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void genericSuperclassOfDoesntWorkWithInterfaces() {
    IOneParameter<String> cons = new IOneParameter<String>() { /**/ };
    Optional<ParameterizedType> actual = Types.genericSuperclassOf(cons.getClass());

    assertFalse(actual.isPresent());

    try {
      Types.fromSuperclass(cons.getClass(), 0);
      fail("Types.fromSuperclass should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void genericInterfaceWorksWithInterfaces() {
    IOneParameter<String> cons = new IOneParameter<String>() { /**/ };
    Object[] expected = { String.class };

    ParameterizedType actual = Types.genericInterfaceOf(cons.getClass(), 0).get();

    assertEquals(
      TypeFactory.parameterizedClass(IOneParameter.class, String.class),
      actual);

    assertArrayEquals(
      expected,
      Types.from(actual, 0).toArray());

    assertArrayEquals(
      expected,
      Types.fromInterface(cons.getClass(),0).toArray());
  }

  @Test
  public void genericInterfaceFailsIfNoInterfaceIsFoundAtIndex() {
    IOneParameter<String> cons = new IOneParameter<String>() { /**/ };
    Optional<ParameterizedType> actual = Types.genericInterfaceOf(cons.getClass(), 1);

    assertFalse(actual.isPresent());
  }

  @Test
  public void gettingTheRightGenericSuperinterface() {
    assertEquals(
      TypeFactory.parameterizedClass(Comparable.class, String.class),
      Types.genericInterfaceOf(String.class, 1).get());

    ClassWithMultipleInterfaces cons = new ClassWithMultipleInterfaces();

    assertEquals(
      TypeFactory.parameterizedClass(IOneParameter.class, String.class),
      Types.genericInterfaceOf(cons.getClass(), 0).get());

    assertEquals(
      TypeFactory.parameterizedClass(ITwoParameters.class, Integer.class, listOf(Double.class)),
      Types.genericInterfaceOf(cons.getClass(), 1).get());
  }

  @Test
  public void nonParameterizedInterface() {
    assertFalse(Types.genericInterfaceOf(String.class, 0).isPresent());

    try {
      Types.fromInterface(String.class, 0);
      fail("Types.fromInterface should've thrown IllegalArgumentException");
    } catch(IllegalArgumentException e) {
      /* if we're here, we're good */
    }
  }

  @Test
  public void invalidIndexGenericSuperinterface() {
    ClassWithMultipleInterfaces cons = new ClassWithMultipleInterfaces();

    assertFalse(Types.genericInterfaceOf(cons.getClass(), -1).isPresent());
    assertFalse(Types.genericInterfaceOf(cons.getClass(), 3).isPresent());
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
