package org.sbrubbles.genericcons;

import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import com.coekie.gentyref.TypeToken;

public class ExtractTypesFromConsTest {
  private static final Type CONS_4_TYPES = new TypeToken<C<String, C<Number, C<Object, List<Double>>>>>() { /**/ }.getType();
  private static final Type CONS_STRING_OBJECT_TYPE = new TypeToken<C<String, Object>>() { /**/ }.getType();
  private static final Type LIST_OF_DOUBLE_TYPE = new TypeToken<List<Double>>() { /**/ }.getType();

  @Test
  public void extractFromParameterlessType() {
    assertArrayEquals(
        new Object[] { String.class },
        Types.extractFromCons(String.class).toArray());
  }
  
  @Test
  public void extractFromSingleParameteredType() {
    assertArrayEquals(
        new Object[] { LIST_OF_DOUBLE_TYPE },
        Types.extractFromCons(LIST_OF_DOUBLE_TYPE).toArray());
  }
  
  @Test
  public void extractFromTwoValuedConsType() {
    assertArrayEquals(
        new Object[] { String.class, Object.class },
        Types.extractFromCons(CONS_STRING_OBJECT_TYPE).toArray());
  }
  
  @Test
  public void extractFromFourValuedConsType() {
    assertArrayEquals(
        new Object[] { String.class, Number.class, Object.class, LIST_OF_DOUBLE_TYPE },
        Types.extractFromCons(CONS_4_TYPES).toArray());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void extractFromNull() {
    Types.extractFromCons(null);
  }
}
