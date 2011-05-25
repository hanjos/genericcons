package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;

public class UtilsCheckTest {
  @Test
  public void typeMatch() {
    assertTrue(Utils.check(String.class, ""));
  }
  
  @Test
  public void subtypeMatch() {
    assertTrue(Utils.check(Serializable.class, new ArrayList<Object>()));
  }
  
  @Test
  public void typeMismatch() {
    assertFalse(Utils.check(String.class, new Object()));
  }
  
  @Test
  public void nullTypeMismatch() {
    assertFalse(Utils.check(null, ""));
  }

  @Test
  public void nullObjectMatch() {
    assertTrue(Utils.check(String.class, null));
  }
}
