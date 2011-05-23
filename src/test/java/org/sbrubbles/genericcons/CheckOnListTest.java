package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class CheckOnListTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgumentWithTypeArray() {
    C.check().onList(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgumentWithListOfTypes() {
    C.check(new ArrayList<Type>()).onList(null);
  }
  
  @Test
  public void emptyTypeArrayAgainstEmptyObjectList() {
    assertTrue(C.check().onList(new ArrayList<Object>()));
  }
  
  @Test
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.check(new ArrayList<Type>()).onList(new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.check(Arrays.asList(String.class)).onList(Arrays.asList("", "")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.check(Arrays.asList(String.class)).onList(Arrays.asList("")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.check(
        Arrays.asList(String.class, Double.class, Object.class)).onList(
        Arrays.asList("", new Double(1.0), new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.check(
        Arrays.asList(String.class, Double.class, Object.class)).onList(
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.check(
        Arrays.asList(String.class, null, Object.class)).onList(
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.check(
        Arrays.asList(Serializable.class)).onList(
        Arrays.asList(new ArrayList<Object>())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class)).onList(
        Arrays.asList(new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.check(
        Arrays.asList(Serializable.class, Object.class, Number.class)).onList(
        Arrays.asList(new ArrayList<Object>(), "", 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class, Object.class, Number.class)).onList(
        Arrays.asList(new ArrayList<Object>(), "", false)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooManyArgumentsMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class)).onList(
        Arrays.asList(new ArrayList<Object>(), "", false, 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooFewArgumentsMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class)).onList(
        Arrays.asList(new ArrayList<Object>(), "")));
  }
}
