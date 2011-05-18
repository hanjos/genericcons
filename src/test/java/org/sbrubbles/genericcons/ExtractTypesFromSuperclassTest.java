package org.sbrubbles.genericcons;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import org.javaruntype.type.Types;
import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.JustOneParameter;
import org.sbrubbles.genericcons.fixtures.JustThreeParameters;
import org.sbrubbles.genericcons.fixtures.SonOfJustOneParameter;

public class ExtractTypesFromSuperclassTest {
  @Test
  public void oneNonConsParameter() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    assertArrayEquals(
        new Object[] { Types.STRING }, 
        C.extractTypesFromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public <T> void oneNonConsParameterWithTypeVariable() {
    JustOneParameter<T> cons = new JustOneParameter<T>() { /**/ };

    try {
      C.extractTypesFromSuperclass(cons.getClass(), 0);
      fail();
    } catch (InvalidTypeException e) {
      assertTrue(e.getType() instanceof TypeVariable);
      assertEquals("T", ((TypeVariable<?>) e.getType()).getName());
    }
  }

  @Test
  public void oneConsParameterWithTwoTypes() {
    JustOneParameter<C<String, Number>> cons = 
      new JustOneParameter<C<String, Number>>() { /**/};

    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER }, 
        C.extractTypesFromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithFiveTypes() {
    JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> cons = 
      new JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/ };

    assertArrayEquals(
        new Object[] { 
            Types.STRING, 
            Types.NUMBER, 
            Types.OBJECT, 
            Types.ARRAY_OF_OBJECT,
            Types.SERIALIZABLE }, 
        C.extractTypesFromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void threeParametersSelectingTheNonCons() {
    JustThreeParameters<Number, String, C<Serializable, Serializable>> cons = 
      new JustThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/ };

    assertArrayEquals(
        new Object[] { Types.STRING }, 
        C.extractTypesFromSuperclass(cons.getClass(), 1).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithTwoTypes() {
    JustThreeParameters<Number, String, C<Serializable, Serializable>> cons = 
      new JustThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/ };

    assertArrayEquals(
        new Object[] { Types.SERIALIZABLE, Types.SERIALIZABLE },
        C.extractTypesFromSuperclass(cons.getClass(), 2).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithFiveTypes() {
    JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String> cons = 
      new JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String>() { /**/ };

    assertArrayEquals(
        new Object[] { 
            Types.STRING, 
            Types.NUMBER, 
            Types.OBJECT, 
            Types.ARRAY_OF_OBJECT,
            Types.SERIALIZABLE }, 
        C.extractTypesFromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneParameterWithConsWithCompositeTypes() {
    JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> cons 
      = new JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/ };

    assertArrayEquals(
        new Object[] { 
            Types.STRING, 
            Types.LIST_OF_NUMBER, 
            Types.OBJECT, 
            Types.MAP_OF_STRING_INTEGER }, 
        C.extractTypesFromSuperclass(cons.getClass(), 0).toArray());
  }
  
  @Test
  public void sonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter();

    assertArrayEquals(
        new Object[] { Types.STRING }, 
        C.extractTypesFromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void grandsonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter() { /**/ };
    
    try {
      C.extractTypesFromSuperclass(cons.getClass(), 0);
      fail();
    } catch (TypeParametersNotFoundException e) {
      assertEquals(cons.getClass(), e.getBaseType());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullBaseClass() {
    C.extractTypesFromSuperclass(null, 0);
  }

  @Test
  public void invalidIndex() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    try {
      C.extractTypesFromSuperclass(cons.getClass(), -1);
      fail();
    } catch (TypeParametersNotFoundException e) {
      assertEquals(cons.getClass(), e.getBaseType());
      assertEquals(ArrayIndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  @Test
  public void nonParameterizedSuperclass() {
    try {
      C.extractTypesFromSuperclass(String.class, 0);
      fail();
    } catch (TypeParametersNotFoundException e) {
      assertEquals(String.class, e.getBaseType());
    }
  }
}
