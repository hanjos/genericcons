package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class CheckIterableTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullTypesArgument() {
    C.check(null, Arrays.asList(new Object()));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgument() {
    C.check(new ArrayList<Type>(), null);
  }
  
  @Test
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.check(new ArrayList<Type>(), new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.check(Arrays.asList(String.class), Arrays.asList("", "")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.check(Arrays.asList(String.class), Arrays.asList("")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.check(
        Arrays.asList(String.class, Double.class, Object.class), 
        Arrays.asList("", new Double(1.0), new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.check(
        Arrays.asList(String.class, Double.class, Object.class), 
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.check(
        Arrays.asList(String.class, null, Object.class), 
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.check(
        Arrays.asList(Serializable.class), 
        Arrays.asList(new ArrayList<Object>())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class), 
        Arrays.asList(new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.check(
        Arrays.asList(Serializable.class, Object.class, Number.class), 
        Arrays.asList(new ArrayList<Object>(), "", 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class, Object.class, Number.class), 
        Arrays.asList(new ArrayList<Object>(), "", false)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooManyArgumentsMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class), 
        Arrays.asList(new ArrayList<Object>(), "", false, 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooFewArgumentsMismatch() {
    assertFalse(C.check(
        Arrays.asList(Serializable.class, Object.class, Boolean.class), 
        Arrays.asList(new ArrayList<Object>(), "")));
  }
}
