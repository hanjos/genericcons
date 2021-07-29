package org.sbrubbles.genericcons;

import com.coekie.gentyref.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TypesFromConsTest {
  private static final Type CONS_4_TYPES = new TypeToken<C<String, C<Number, C<Object, List<Double>>>>>() { /**/ }.getType();
  private static final Type CONS_3_TYPES = new TypeToken<C<List<Double>, C<String, Object>>>() { /**/ }.getType();
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

  @Test
  public void buildFromNullOrEmpty() {
    assertNull(Types.cons());
    assertNull(Types.cons((Type[]) null));
    assertNull(Types.cons((List<? extends Type>) null));
    assertNull(Types.cons(Collections.emptyList()));
  }

  @Test
  public void buildFromSingleType() {
    assertEquals(String.class, Types.cons(String.class));
    assertEquals(LIST_OF_DOUBLE_TYPE, Types.cons(LIST_OF_DOUBLE_TYPE));

    assertEquals(String.class, Types.cons(Arrays.asList(String.class)));
    assertEquals(LIST_OF_DOUBLE_TYPE, Types.cons(Arrays.asList(LIST_OF_DOUBLE_TYPE)));
  }

  @Test
  public void buildFromMoreThanOneType() {
    assertEquals(CONS_STRING_OBJECT_TYPE, Types.cons(String.class, Object.class));
    assertEquals(CONS_4_TYPES, Types.cons(String.class, Number.class, Object.class, LIST_OF_DOUBLE_TYPE));

    assertEquals(CONS_STRING_OBJECT_TYPE, Types.cons(Arrays.asList(String.class, Object.class)));
    assertEquals(CONS_4_TYPES, Types.cons(Arrays.asList(String.class, Number.class, Object.class, LIST_OF_DOUBLE_TYPE)));
  }

  @Test
  public void rejectNullTypes() {
    try {
      Types.cons(null, Object.class, null);
      fail();
    } catch(NullPointerException e) { /**/ }

    try {
      Types.cons(String.class, null);
      fail();
    } catch(NullPointerException e) { /**/ }

    try {
      Types.cons(String.class, Object.class, null, LIST_OF_DOUBLE_TYPE);
      fail();
    } catch(NullPointerException e) { /**/ }

    try {
      Types.cons(Arrays.asList(null, Object.class, null));
      fail();
    } catch(NullPointerException e) { /**/ }

    try {
      Types.cons(Arrays.asList(String.class, null));
      fail();
    } catch(NullPointerException e) { /**/ }

    try {
      Types.cons(Arrays.asList(String.class, Object.class, null, LIST_OF_DOUBLE_TYPE));
      fail();
    } catch(NullPointerException e) { /**/ }
  }

  @Test
  public void buildFromConsTypes() {
    assertEquals(CONS_4_TYPES, Types.cons(CONS_4_TYPES));
    assertEquals(CONS_4_TYPES, Types.cons(Arrays.asList(CONS_4_TYPES)));

    assertEquals(CONS_3_TYPES, Types.cons(LIST_OF_DOUBLE_TYPE, CONS_STRING_OBJECT_TYPE));
    assertEquals(CONS_3_TYPES, Types.cons(LIST_OF_DOUBLE_TYPE, String.class, Object.class));
    assertEquals(CONS_3_TYPES, Types.cons(Arrays.asList(LIST_OF_DOUBLE_TYPE, CONS_STRING_OBJECT_TYPE)));
    assertEquals(CONS_3_TYPES, Types.cons(Arrays.asList(LIST_OF_DOUBLE_TYPE, String.class, Object.class)));
  }
}
