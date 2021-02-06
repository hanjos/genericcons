package org.sbrubbles.genericcons;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

public class CTypesTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullTypeArray() {
    C.types((Type[]) null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullTypeList() {
    C.types((List<Type>) null);
  }
}
