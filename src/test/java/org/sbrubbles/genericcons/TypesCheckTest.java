package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class TypesCheckTest {
  private final Iterable<Type> emptyTypeList = new ArrayList<>();
  private final Iterable<Type> singleTypeList = Arrays.asList(String.class);

  private final Iterable<Object> emptyObjectList = new ArrayList<>();
  private final Iterable<Object> singleObjectList = Arrays.asList("foo");

  @Test
  public void nullInputMismatch() {
    assertFalse(Types.check(emptyTypeList, null));
    assertFalse(Types.check(singleTypeList, null));
    assertFalse(Types.check((Iterable<? extends Type>) null, emptyObjectList));
    assertFalse(Types.check((Iterable<? extends Type>) null, singleObjectList));
    assertFalse(Types.check((Iterable<? extends Type>) null, null));
  }
  
  @Test
  public void emptyTypesAgainstEmptyObjectsMatch() {
    assertTrue(Types.check(emptyTypeList, emptyObjectList));
  }
  
  @Test
  public void listSizeMismatch() {
    assertFalse(Types.check(emptyTypeList, singleObjectList));
    assertFalse(Types.check(singleTypeList, emptyObjectList));
    assertFalse(Types.check(singleTypeList, Arrays.asList("", "")));
  }
  
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(Types.check(singleTypeList, singleObjectList));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(Types.check(
      Arrays.asList(String.class, Double.class, Object.class),
      Arrays.asList("", new Double(1.0), new Object()))
    );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(Types.check(
        Arrays.asList(String.class, Double.class, Object.class),
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(String.class, null, Object.class),
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(Types.check(
        Arrays.asList(Serializable.class),
        Arrays.asList(new ArrayList<Object>())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class),
        Arrays.asList(new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(Types.check(
        Arrays.asList(Serializable.class, Object.class, Number.class),
        Arrays.asList(new ArrayList<Object>(), "", 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class, Object.class, Number.class),
        Arrays.asList(new ArrayList<Object>(), "", false)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooManyArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class),
        Arrays.asList(new ArrayList<Object>(), "", false, 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooFewArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class),
        Arrays.asList(new ArrayList<Object>(), "")));
  }

  @Test
  public void singleTypeMatch() {
    assertTrue(Types.check(String.class, ""));
  }

  @Test
  public void singleSubtypeMatch() {
    assertTrue(Types.check(Serializable.class, new ArrayList<Object>()));
  }

  @Test
  public void singleTypeMismatch() {
    assertFalse(Types.check(String.class, new Object()));
  }

  @Test
  public void singleNullTypeMismatch() {
    assertFalse(Types.check(null, ""));
    assertFalse(Types.check((Type) null, null));
  }

  @Test
  public void singleNullObjectMatch() {
    assertTrue(Types.check(String.class, null));
  }

  @Test
  public void primitiveTypesMismatch() {
    assertFalse(Types.check(int.class, 1));
  }

  @Test
  public void autoboxTypesMatch() {
    assertTrue(Types.check(Integer.class, 1));
    assertTrue(Types.check(Long.class, 1l));
    assertTrue(Types.check(Boolean.class, false));
    assertTrue(Types.check(Float.class, 1f));
  }
}
