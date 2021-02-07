package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class TypesCheckTest {
  private final Iterable<Type> emptyTypeList = new ArrayList<>();
  private final Iterable<Type> singleTypeList = Collections.singletonList(String.class);

  private final Iterable<Object> emptyObjectList = new ArrayList<>();
  private final Iterable<Object> singleObjectList = Collections.singletonList("foo");

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
  
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(Types.check(
      Arrays.asList(String.class, Double.class, Object.class),
      Arrays.asList("", 1.0, new Object()))
    );
  }
  
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(Types.check(
        Arrays.asList(String.class, Double.class, Object.class),
        Arrays.asList(null, 1.0, null)));
  }
  
  @Test
  public void nullTypesMultipleArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(String.class, null, Object.class),
        Arrays.asList(null, 1.0, null)));
  }
  
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(Types.check(
      Collections.singletonList(Serializable.class),
      Collections.singletonList(new ArrayList<>())));
  }
  
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(Types.check(
      Collections.singletonList(Serializable.class),
      Collections.singletonList(new Object())));
  }
  
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(Types.check(
        Arrays.asList(Serializable.class, Object.class, Number.class),
        Arrays.asList(new ArrayList<>(), "", 1.0)));
  }
  
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class, Object.class, Number.class),
        Arrays.asList(new ArrayList<>(), "", false)));
  }
  
  @Test
  public void tooManyArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class),
        Arrays.asList(new ArrayList<>(), "", false, 1.0)));
  }
  
  @Test
  public void tooFewArgumentsMismatch() {
    assertFalse(Types.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class),
        Arrays.asList(new ArrayList<>(), "")));
  }

  @Test
  public void singleTypeMatch() {
    assertTrue(Types.check(String.class, ""));
  }

  @Test
  public void singleSubtypeMatch() {
    assertTrue(Types.check(Serializable.class, new ArrayList<>()));
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
    assertTrue(Types.check(Long.class, 1L));
    assertTrue(Types.check(Boolean.class, false));
    assertTrue(Types.check(Float.class, 1f));
  }
}
