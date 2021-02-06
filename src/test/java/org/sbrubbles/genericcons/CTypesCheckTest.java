package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class CTypesCheckTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgumentWithTypeArray() {
    C.types().check(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgumentWithListOfTypes() {
    C.types(new ArrayList<Type>()).check(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void emptyTypeArrayAgainstEmptyObjectList() {
    assertTrue(C.types().check(new ArrayList<Object>()));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.types(new ArrayList<Type>()).check(new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.types(Arrays.asList(String.class)).check(Arrays.asList("", "")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.types(Arrays.asList(String.class)).check(Arrays.asList("")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.types(
        Arrays.asList(String.class, Double.class, Object.class)).check(
        Arrays.asList("", new Double(1.0), new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.types(
        Arrays.asList(String.class, Double.class, Object.class)).check(
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.types(
        Arrays.asList(String.class, null, Object.class)).check(
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.types(
        Arrays.asList(Serializable.class)).check(
        Arrays.asList(new ArrayList<Object>())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.types(
        Arrays.asList(Serializable.class)).check(
        Arrays.asList(new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.types(
        Arrays.asList(Serializable.class, Object.class, Number.class)).check(
        Arrays.asList(new ArrayList<Object>(), "", 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.types(
        Arrays.asList(Serializable.class, Object.class, Number.class)).check(
        Arrays.asList(new ArrayList<Object>(), "", false)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooManyArgumentsMismatch() {
    assertFalse(C.types(
        Arrays.asList(Serializable.class, Object.class, Boolean.class)).check(
        Arrays.asList(new ArrayList<Object>(), "", false, 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooFewArgumentsMismatch() {
    assertFalse(C.types(
        Arrays.asList(Serializable.class, Object.class, Boolean.class)).check(
        Arrays.asList(new ArrayList<Object>(), "")));
  }
}
