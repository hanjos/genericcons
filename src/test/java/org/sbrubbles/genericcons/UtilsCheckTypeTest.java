package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;

public class UtilsCheckTypeTest {
  @Test
  public void typeMatch() {
    assertTrue(Utils.checkType(String.class, ""));
  }
  
  @Test
  public void subtypeMatch() {
    assertTrue(Utils.checkType(Serializable.class, new ArrayList<Object>()));
  }
  
  @Test
  public void typeMismatch() {
    assertFalse(Utils.checkType(String.class, new Object()));
  }
  
  @Test
  public void nullTypeMismatch() {
    assertFalse(Utils.checkType(null, ""));
  }

  @Test
  public void nullObjectMatch() {
    assertTrue(Utils.checkType(String.class, null));
  }
}
