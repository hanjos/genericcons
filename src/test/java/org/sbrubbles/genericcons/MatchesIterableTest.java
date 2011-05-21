package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.javaruntype.type.Type;
import org.javaruntype.type.Types;
import org.junit.Test;

public class MatchesIterableTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullTypesArgument() {
    C.matches(null, Arrays.asList(new Object()));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullObjectsArgument() {
    C.matches(new ArrayList<Type<?>>(), null);
  }
  
  @Test
  public void emptyTypeListAgainstEmptyObjectList() {
    assertTrue(C.matches(new ArrayList<Type<?>>(), new ArrayList<Object>()));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void listSizeMismatch() {
    assertFalse(C.matches(Arrays.asList(Types.STRING), Arrays.asList("", "")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypeSingleArgumentMatch() {
    assertTrue(C.matches(Arrays.asList(Types.STRING), Arrays.asList("")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sameTypesMultipleArgumentsMatch() {
    assertTrue(C.matches(
        Arrays.asList(Types.STRING, Types.DOUBLE, Types.OBJECT), 
        Arrays.asList("", new Double(1.0), new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullObjectsMultipleArgumentsMatch() {
    assertTrue(C.matches(
        Arrays.asList(Types.STRING, Types.DOUBLE, Types.OBJECT), 
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void nullTypesMultipleArgumentsMatch() {
    assertFalse(C.matches(
        Arrays.asList(Types.STRING, null, Types.OBJECT), 
        Arrays.asList(null, new Double(1.0), null)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMatch() {
    assertTrue(C.matches(
        Arrays.asList(Types.SERIALIZABLE), 
        Arrays.asList(new ArrayList<Object>())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeSingleArgumentMismatch() {
    assertFalse(C.matches(
        Arrays.asList(Types.SERIALIZABLE), 
        Arrays.asList(new Object())));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMatch() {
    assertTrue(C.matches(
        Arrays.asList(Types.SERIALIZABLE, Types.OBJECT, Types.NUMBER), 
        Arrays.asList(new ArrayList<Object>(), "", 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void subtypeMultipleArgumentsMismatch() {
    assertFalse(C.matches(
        Arrays.asList(Types.SERIALIZABLE, Types.OBJECT, Types.NUMBER), 
        Arrays.asList(new ArrayList<Object>(), "", false)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooManyArgumentsMismatch() {
    assertFalse(C.matches(
        Arrays.asList(Types.SERIALIZABLE, Types.OBJECT, Types.BOOLEAN), 
        Arrays.asList(new ArrayList<Object>(), "", false, 1.0)));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void tooFewArgumentsMismatch() {
    assertFalse(C.matches(
        Arrays.asList(Types.SERIALIZABLE, Types.OBJECT, Types.BOOLEAN), 
        Arrays.asList(new ArrayList<Object>(), "")));
  }
}
