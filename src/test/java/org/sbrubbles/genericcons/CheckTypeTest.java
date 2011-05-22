package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;

public class CheckTypeTest {
  @Test
  public void typeMatch() {
    assertTrue(C.checkType(String.class, ""));
  }
  
  @Test
  public void subtypeMatch() {
    assertTrue(C.checkType(Serializable.class, new ArrayList<Object>()));
  }
  
  @Test
  public void typeMismatch() {
    assertFalse(C.checkType(String.class, new Object()));
  }
  
  @Test
  public void nullTypeMismatch() {
    assertFalse(C.checkType(null, ""));
  }

  @Test
  public void nullObjectMatch() {
    assertTrue(C.checkType(String.class, null));
  }
}
