package org.sbrubbles.genericcons;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

public class CheckTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullTypeArray() {
    C.check((Type[]) null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void nullTypeList() {
    C.check((List<Type>) null);
  }
}
