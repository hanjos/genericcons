package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeToken;
import org.junit.Test;
import org.sbrubbles.genericcons.fixtures.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.sbrubbles.genericcons.fixtures.Utils.*;


@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TypesSuppliersTest {
  @Test
  public void genericSuperclassOfOneNonConsParameter() {
    OneParameter<String> c = new OneParameter<String>() { /**/};

    assertEquals(
      type(OneParameter.class, String.class),
      Types.genericSuperclassOf(c.getClass()).get());
  }

  @Test
  public <T> void genericSuperclassOfOneParameterWithTypeVariable() {
    OneParameter<T> c = new OneParameter<T>() { /**/};
    Type t = new TypeToken<T>() { /**/}.getType();

    assertEquals(
      type(OneParameter.class, t),
      Types.genericSuperclassOf(c.getClass()).get());
  }

  @Test
  public void genericSuperclassOfOneParameterWithCons() {
    OneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>> c =
      new OneParameter<C<String, C<Number, C<Object, C<Object[], Serializable>>>>>() { /**/};

    assertEquals(
      type(OneParameter.class,
        cons(String.class, Number.class, Object.class, Object[].class, Serializable.class)),
      Types.genericSuperclassOf(c.getClass()).get());
  }

  @Test
  public void genericSuperclassOfThreeParameters() {
    ThreeParameters<Number, String, C<Serializable, Serializable>> c =
      new ThreeParameters<Number, String, C<Serializable, Serializable>>() { /**/};

    assertEquals(
      type(ThreeParameters.class,
        Number.class,
        String.class,
        cons(Serializable.class, Serializable.class)),
      Types.genericSuperclassOf(c.getClass()).get());
  }

  @Test
  public void genericSuperclassOfOneParameterWithConsAndCompositeTypes() {
    OneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>> c
      = new OneParameter<C<String, C<List<Number>, C<Object, Map<String, Integer>>>>>() { /**/};

    assertEquals(
      type(OneParameter.class,
        cons(String.class,
          listOf(Number.class),
          Object.class,
          mapOf(String.class, Integer.class))),
      Types.genericSuperclassOf(c.getClass()).get());
  }

  @Test
  public void genericSuperclassOfSonOfParameterizedType() {
    OneParameter<String> c = new SonOfOneParameter();

    assertEquals(
      type(OneParameter.class, String.class),
      Types.genericSuperclassOf(c.getClass()).get());
  }

  @Test
  public void genericSuperclassOfGrandsonOfParameterizedType() {
    OneParameter<String> c = new SonOfOneParameter() { /**/};

    assertFalse(Types.genericSuperclassOf(c.getClass()).isPresent());
  }

  @Test
  public void genericSuperclassOfDoesntWorkWithInterfaces() {
    IOneParameter<String> c = new IOneParameter<String>() { /**/};

    assertFalse(Types.genericSuperclassOf(c.getClass()).isPresent());
  }

  @Test
  public void genericInterfaceOfWorksWithInterfaces() {
    IOneParameter<String> c = new IOneParameter<String>() { /**/};

    assertEquals(
      type(IOneParameter.class, String.class),
      Types.genericInterfaceOf(c.getClass(), 0).get());
  }

  @Test
  public void genericInterfaceFailsIfNoInterfaceIsFoundAtIndex() {
    IOneParameter<String> c = new IOneParameter<String>() { /**/};

    assertFalse(Types.genericInterfaceOf(c.getClass(), 1).isPresent());
  }

  @Test
  public void gettingTheRightGenericSuperinterface() {
    assertEquals(
      type(Comparable.class, String.class),
      Types.genericInterfaceOf(String.class, 1).get());

    ClassWithMultipleInterfaces c = new ClassWithMultipleInterfaces();

    assertEquals(
      type(IOneParameter.class, String.class),
      Types.genericInterfaceOf(c.getClass(), 0).get());

    assertEquals(
      type(ITwoParameters.class, Integer.class, listOf(Double.class)),
      Types.genericInterfaceOf(c.getClass(), 1).get());
  }

  @Test
  public void nonParameterizedSupertypes() {
    assertFalse(Types.genericSuperclassOf(String.class).isPresent());
    assertFalse(Types.genericInterfaceOf(String.class, 0).isPresent());
  }

  @Test
  public void nullBaseClass() {
    assertFalse(Types.genericSuperclassOf(null).isPresent());
    assertFalse(Types.genericInterfaceOf(null, 0).isPresent());
  }

  @Test
  public void invalidIndexGenericSuperinterface() {
    ClassWithMultipleInterfaces c = new ClassWithMultipleInterfaces();

    assertFalse(Types.genericInterfaceOf(c.getClass(), -1).isPresent());
    assertFalse(Types.genericInterfaceOf(c.getClass(), 3).isPresent());
  }
}
