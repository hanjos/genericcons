package org.sbrubbles.variablegenerics;

import static org.junit.Assert.assertArrayEquals;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.javaruntype.exceptions.TypeValidationException;
import org.javaruntype.type.Types;
import org.junit.Test;

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
    Cons<String, Number> cons = new Cons<String, Number>() {};
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER },
        cons.getTypes().toArray());
  }
  
  @Test
  public void ctorWithFiveTypes() {
    Cons<String, Cons<Number, Cons<Object, Cons<Object[], Serializable>>>> cons = 
      new Cons<String, Cons<Number, Cons<Object, Cons<Object[], Serializable>>>>() {};
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER, Types.OBJECT, Types.ARRAY_OF_OBJECT, Types.SERIALIZABLE },
        cons.getTypes().toArray());
  }
  
  @Test
  public void ctorWithCompositeTypes() {
    Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>> cons = 
      new Cons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() {};
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.LIST_OF_NUMBER, Types.OBJECT, Types.MAP_OF_STRING_INTEGER },
        cons.getTypes().toArray());
  }
  
  @Test(expected = TypeValidationException.class)
  public void namedSubclass() {
    new FailedSonOfCons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>();
  }
  
  @Test(expected = UnexpectedTypeException.class)
  public void anonymousSubclassOfANamedSubclass() {
    new FailedSonOfCons<String, Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() {};
  }
  
  @Test(expected = UnexpectedTypeException.class)
  public void oneTypeVariableFixedFromAnonymousSubclassOfANamedSubclass() {
    new LimpingSonOfCons<Cons<List<Number>, Cons<Object, Map<String, Integer>>>>() {};
  }
  
  @Test(expected = MissingTypeParametersException.class)
  public void bothTypeVariablesFixedFromAnonymousSubclassOfANamedSubclass() {
    new FrozenSonOfCons() {};
  }
  
  @Test(expected = MissingTypeParametersException.class)
  public void bothTypeVariablesFixedFromAnonymousSubclassOfANamedSubclassOfANamedSubclass() {
    new FrozenGrandsonOfCons() {};
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test(expected = UnsupportedOperationException.class)
  public void immutableGetTypes() {
    Cons<String, Number> cons = new Cons<String, Number>() {};
    
    assertArrayEquals(
        new Object[] { Types.STRING, Types.NUMBER },
        cons.getTypes().toArray());
    
    ((List) cons.getTypes()).add(Types.ARRAY_OF_BIG_DECIMAL);
  }
}
