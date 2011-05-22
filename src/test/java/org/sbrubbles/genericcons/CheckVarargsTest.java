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
    C.check(null).onVarargs(new Object());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgument() {
    C.check(new ArrayList<Type>()).onVarargs((Object[]) null);
  }
  
  @Test
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.check(new ArrayList<Type>()).onVarargs());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.check(Arrays.asList(String.class)).onVarargs("", ""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.check(Arrays.asList(String.class)).onVarargs(""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.check(Arrays.asList(String.class, Double.class, Object.class))
        .onVarargs("", new Double(1.0), new Object()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.check(Arrays.asList(String.class, Double.class, Object.class))
        .onVarargs(null, new Double(1.0), null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.check(Arrays.asList(String.class, null, Object.class))
        .onVarargs(null, new Double(1.0), null));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.check(Arrays.asList(Serializable.class))
        .onVarargs(new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.check(Arrays.asList(Serializable.class))
        .onVarargs(new Object()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.check(Arrays.asList(Serializable.class, Object.class, Number.class))
        .onVarargs(new ArrayList<Object>(), "", 1.0));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.check(Arrays.asList(Serializable.class, Object.class, Number.class))
        .onVarargs(new ArrayList<Object>(), "", false));
  }
}
