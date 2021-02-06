package org.sbrubbles.genericcons;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.JustOneParameter;
import org.sbrubbles.genericcons.fixtures.JustThreeParameters;
import org.sbrubbles.genericcons.fixtures.SonOfJustOneParameter;

import com.coekie.gentyref.TypeToken;

public class TypesFromSuperclassTest {
  @Test
  public void oneNonConsParameter() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    assertArrayEquals(
        new Object[] { String.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public <T> void oneNonConsParameterWithTypeVariable() {
    JustOneParameter<T> cons = new JustOneParameter<T>() { /**/ };

    assertArrayEquals(
        new Object[] { new TypeToken<T>() { /**/ }.getType() },
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithTwoTypes() {
    JustOneParameter<C<String, Number>> cons = 
      new JustOneParameter<C<String, Number>>() { /**/};

    assertArrayEquals(
        new Object[] { String.class, Number.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test
  public void oneConsParameterWithFiveTypes() {
    JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> cons = 
      new JustOneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/ };

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
  public void threeParametersSelectingTheNonCons() {
    JustThreeParameters<Number, String, C<Serializable, Serializable>> cons = 
      new JustThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/ };

    assertArrayEquals(
        new Object[] { String.class }, 
        Types.fromSuperclass(cons.getClass(), 1).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithTwoTypes() {
    JustThreeParameters<Number, String, C<Serializable, Serializable>> cons = 
      new JustThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/ };

    assertArrayEquals(
        new Object[] { Serializable.class, Serializable.class },
        Types.fromSuperclass(cons.getClass(), 2).toArray());
  }

  @Test
  public void threeParametersSelectingTheConsWithFiveTypes() {
    JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String> cons = 
      new JustThreeParameters<C<String, C<Number, C<Object, C<Object[], Serializable>>>>, String, String>() { /**/ };

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
  public void oneParameterWithConsWithCompositeTypes() {
    JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> cons 
      = new JustOneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/ };

    assertArrayEquals(
        new Object[] { 
            String.class, 
            new TypeToken<List<Number>>() { /**/ }.getType(), 
            Object.class, 
            new TypeToken<Map<String, Integer>>() { /**/ }.getType() }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }
  
  @Test
  public void sonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter();

    assertArrayEquals(
        new Object[] { String.class }, 
        Types.fromSuperclass(cons.getClass(), 0).toArray());
  }

  @Test(expected = IllegalArgumentException.class)
  public void grandsonOfParameterizedType() {
    JustOneParameter<String> cons = new SonOfJustOneParameter() { /**/ };
    
    Types.fromSuperclass(cons.getClass(), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullBaseClass() {
    Types.fromSuperclass(null, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidIndex() {
    JustOneParameter<String> cons = new JustOneParameter<String>() { /**/ };

    Types.fromSuperclass(cons.getClass(), -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonParameterizedSuperclass() {
    Types.fromSuperclass(String.class, 0);
  }
}
