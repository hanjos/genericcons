package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeFactory;
import com.coekie.gentyref.TypeToken;
import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.JustOneParameter;
import org.sbrubbles.genericcons.fixtures.JustThreeParameters;
import org.sbrubbles.genericcons.fixtures.SonOfJustOneParameter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TypesFromSuperclassTest {
  @Test
  public void oneNonConsParameter() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, String.class),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { String.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public <T> void oneNonConsParameterWithTypeVariable() {
    JustOneParameter<T> cons = new JustOneParameter<T>() { /**/ };
    Type t = new TypeToken<T>() { /**/ }.getType();

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, t),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { t },
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithTwoTypes() {
    JustOneParameter<C<String, Number>> cons = 
      new JustOneParameter<C<String, Number>>() { /**/};
    Type typeList = encodeCons(String.class, Number.class);

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, typeList),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { String.class, Number.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithFiveTypes() {
    JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> cons = 
      new JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/ };
    Type typeList = encodeCons(String.class, Number.class, Object.class, Object[].class, Serializable.class);

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, typeList),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { 
            String.class, 
            Number.class, 
            Object.class, 
            new TypeToken<Object[]>() { /**/ }.getType(),
            Serializable.class }, 
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

    assertArrayEquals(
        new Object[] { String.class }, 
        Types.fromSuperclass(cons.getClass(), 1).toArray());

    assertArrayEquals(
      new Object[] { Serializable.class, Serializable.class },
      Types.fromSuperclass(cons.getClass(), 2).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithFiveTypes() {
    JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String> cons = 
      new JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustThreeParameters.class,
        encodeCons(String.class, Number.class, Object.class, Object[].class, Serializable.class),
        String.class,
        String.class),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { 
            String.class, 
            Number.class, 
            Object.class, 
            TypeFactory.arrayOf(Object.class),
            Serializable.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneParameterWithConsWithCompositeTypes() {
    JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> cons 
      = new JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class,
        encodeCons(String.class,
          listOf(Number.class),
          Object.class,
          mapOf(String.class, Integer.class))),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { 
            String.class,
            listOf(Number.class),
            Object.class,
            mapOf(String.class, Integer.class) },
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }
  
  @Test
  public void sonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter();

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, String.class),
      Types.genericSuperclassOf(cons.getClass()));

    assertArrayEquals(
        new Object[] { String.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test(expected = IllegalArgumentException.class)
  public void grandsonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter() { /**/ };

    assertNull(Types.genericSuperclassOf(cons.getClass()));

    Types.fromSuperclass(cons.getClass(), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullBaseClass() {
    assertNull(Types.genericSuperclassOf(null));

    Types.fromSuperclass(null, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidIndex() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    assertEquals(
      TypeFactory.parameterizedClass(JustOneParameter.class, String.class),
      Types.genericSuperclassOf(cons.getClass()));

    Types.fromSuperclass(cons.getClass(), -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonParameterizedSuperclass() {
    assertNull(Types.genericSuperclassOf(String.class));

    Types.fromSuperclass(String.class, 0);
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
