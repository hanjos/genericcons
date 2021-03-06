package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class TypesFromConsTest {
  private static final Type CONS_4_TYPES = new TypeToken<C<String, C<Number, C<Object, List<Double>>>>>() { /**/ }.getType();
  private static final Type CONS_STRING_OBJECT_TYPE = new TypeToken<C<String, Object>>() { /**/ }.getType();
  private static final Type LIST_OF_DOUBLE_TYPE = new TypeToken<List<Double>>() { /**/ }.getType();

  @Test
  public void extractFromParameterlessType() {
    assertArrayEquals(
        new Object[] { String.class },
        Types.fromCons(String.class).toArray());
  }
  
  @Test
  public void extractFromSingleParameterizedType() {
    assertArrayEquals(
        new Object[] { LIST_OF_DOUBLE_TYPE },
        Types.fromCons(LIST_OF_DOUBLE_TYPE).toArray());
  }
  
  @Test
  public void extractFromTwoValuedConsType() {
    assertArrayEquals(
        new Object[] { String.class, Object.class },
        Types.fromCons(CONS_STRING_OBJECT_TYPE).toArray());
  }
  
  @Test
  public void extractFromFourValuedConsType() {
    assertArrayEquals(
        new Object[] { String.class, Number.class, Object.class, LIST_OF_DOUBLE_TYPE },
        Types.fromCons(CONS_4_TYPES).toArray());
  }
  
  @Test
  public void extractFromNull() {
    assertTrue(Types.fromCons(null).isEmpty());
  }
}
