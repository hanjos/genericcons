package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class CheckVarargsTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullTypesArgument() {
    C.checkVarargs(null, new Object());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgument() {
    C.checkVarargs(new ArrayList<Type>(), (Object[]) null);
  }
  
  @Test
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.checkVarargs(new ArrayList<Type>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.checkVarargs(Arrays.asList(String.class), "", ""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.checkVarargs(Arrays.asList(String.class), ""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.checkVarargs(
        Arrays.asList(String.class, Double.class, Object.class), 
        "", new Double(1.0), new Object()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.checkVarargs(
        Arrays.asList(String.class, Double.class, Object.class), 
        null, new Double(1.0), null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.checkVarargs(
        Arrays.asList(String.class, null, Object.class), 
        null, new Double(1.0), null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.checkVarargs(
        Arrays.asList(Serializable.class), 
        new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.checkVarargs(
        Arrays.asList(Serializable.class), 
        new Object()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.checkVarargs(
        Arrays.asList(Serializable.class, Object.class, Number.class), 
        new ArrayList<Object>(), "", 1.0));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.checkVarargs(
        Arrays.asList(Serializable.class, Object.class, Number.class), 
        new ArrayList<Object>(), "", false));
  }
}
