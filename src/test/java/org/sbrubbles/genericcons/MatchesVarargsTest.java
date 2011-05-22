package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class MatchesVarargsTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullTypesArgument() {
    C.matchesVarargs(null, new Object());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgument() {
    C.matchesVarargs(new ArrayList<Type>(), (Object[]) null);
  }
  
  @Test
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.matchesVarargs(new ArrayList<Type>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.matchesVarargs(Arrays.asList(String.class), "", ""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.matchesVarargs(Arrays.asList(String.class), ""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.matchesVarargs(
        Arrays.asList(String.class, Double.class, Object.class), 
        "", new Double(1.0), new Object()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.matchesVarargs(
        Arrays.asList(String.class, Double.class, Object.class), 
        null, new Double(1.0), null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.matchesVarargs(
        Arrays.asList(String.class, null, Object.class), 
        null, new Double(1.0), null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.matchesVarargs(
        Arrays.asList(Serializable.class), 
        new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.matchesVarargs(
        Arrays.asList(Serializable.class), 
        new Object()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.matchesVarargs(
        Arrays.asList(Serializable.class, Object.class, Number.class), 
        new ArrayList<Object>(), "", 1.0));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.matchesVarargs(
        Arrays.asList(Serializable.class, Object.class, Number.class), 
        new ArrayList<Object>(), "", false));
  }
}
