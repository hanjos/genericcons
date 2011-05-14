package org.sbrubbles.genericcons;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.javaruntype.type.Types;
import org.junit.Test;
import org.sbrubbles.genericcons.Cons;
import org.sbrubbles.genericcons.InvalidTypeException;
import org.sbrubbles.genericcons.MissingTypeParametersException;
import org.sbrubbles.genericcons.UnexpectedTypeException;

public class ConsTest {
  private static class FailedSonOfCons<First, Rest> extends Cons<First, Rest> {
    // empty block
  }
  
  private static class LimpingSonOfCons<Rest> extends Cons<String, Rest> {
    // empty block
  }
  
  private static class FrozenSonOfCons extends Cons<String, Number> {
    // empty block
  }
  
  private static class FrozenGrandsonOfCons extends Cons<String, Number> {
    // empty block
  }
  
  @Test
  public void ctorWithTwoTypes() {
    Cons<String, Number> cons = new Cons<String, Number>() { /**/ };
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER },
        cons.getTypes().toArray());
  }
  
  @Test
  public void ctorWithFiveTypes() {
    Cons<String, Cons<Number, Cons<Object, Cons<Object[], Serializable>>>> cons = 
      new Cons<String, Cons<Number, Cons<Object, Cons<Object[], Serializable>>>>() { /**/ };
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER, Types.OBJECT, Types.ARRAY_OF_OBJECT, Types.SERIALIZABLE },
        cons.getTypes().toArray());
  }
  
  @Test
  public void ctorWithCompositeTypes() {
    Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>> cons = 
      new Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() { /**/ };
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.LIST_OF_NUMBER, Types.OBJECT, Types.MAP_OF_STRING_INTEGER },
        cons.getTypes().toArray());
  }
  
  @SuppressWarnings("rawtypes")
  @Test(expected = MissingTypeParametersException.class)
  public void rawCtor() {
    new Cons() { /**/ };
  }
  
  @Test(expected = InvalidTypeException.class)
  public void namedSubclass() {
    new FailedSonOfCons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>();
  }
  
  @Test(expected = UnexpectedTypeException.class)
  public void anonymousSubclassOfANamedSubclass() {
    new FailedSonOfCons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() { /**/ };
  }
  
  @Test(expected = UnexpectedTypeException.class)
  public void oneTypeVariableFixedFromAnonymousSubclassOfANamedSubclass() {
    new LimpingSonOfCons<Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() { /**/ };
  }
  
  @Test(expected = MissingTypeParametersException.class)
  public void bothTypeVariablesFixedFromAnonymousSubclassOfANamedSubclass() {
    new FrozenSonOfCons() { /**/ };
  }
  
  @SuppressWarnings("synthetic-access")
  @Test(expected = MissingTypeParametersException.class)
  public void bothTypeVariablesFixedFromAnonymousSubclassOfANamedSubclassOfANamedSubclass() {
    new FrozenGrandsonOfCons() { /**/ };
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test(expected = UnsupportedOperationException.class)
  public void immutableGetTypes() {
    Cons<String, Number> cons = new Cons<String, Number>() { /**/ };
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER },
        cons.getTypes().toArray());
    
    ((List) cons.getTypes()).add(Types.ARRAY_OF_BIG_DECIMAL);
  }
  
  @Test
  public void simpleEquality() {
    assertEquals(
        new Cons<String, Number>() { /**/ }, 
        new Cons<String, Number>() { /**/ });
  }
  
  @Test
  public void selfEquality() {
    Cons<String, Number> cons = new Cons<String, Number>() { /**/ };
    
    assertEquals(cons, cons);
  }
  
  @Test
  public void simpleInequality() {
    Cons<String, Number> string_number = new Cons<String, Number>() { /**/ };
    Cons<String, Integer> string_integer = new Cons<String, Integer>() { /**/ };
    
    assertFalse(string_number.equals(string_integer));
  }
  
  @Test
  public void nullInequality() {
    Cons<String, Number> string_number = new Cons<String, Number>() { /**/ };
    
    assertFalse(string_number.equals(null));
  }
  
  @Test
  public void complexEquality() {
    Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>> cons1 = 
      new Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() { /**/ };
      
    Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>> cons2 = 
      new Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() { /**/ };
      
    assertEquals(cons1, cons2);
  }
  
  @Test
  public void hashCodeEquality() {
    assertEquals(
        new Cons<String, Number>() { /**/ }.hashCode(), 
        new Cons<String, Number>() { /**/ }.hashCode());
  }
  
  @Test
  public void simpleToString() {
    assertEquals(
        "<java.lang.String, java.lang.Number>", 
        new Cons<String, Number>() { /**/ }.toString());
  }
  
  @Test
  public void compositeToString() {
    assertEquals(
        "<java.lang.String, java.util.List<java.lang.Number>, java.lang.Object, java.util.Map<java.lang.String,java.lang.Integer>>", 
        new Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() { /**/ }
          .toString());
  }
}
