package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;

public class TypesCheckTest {
  @Test
  public void typeMatch() {
    assertTrue(Types.check(String.class, ""));
  }
  
  @Test
  public void subtypeMatch() {
    assertTrue(Types.check(Serializable.class, new ArrayList<Object>()));
  }
  
  @Test
  public void typeMismatch() {
    assertFalse(Types.check(String.class, new Object()));
  }
  
  @Test
  public void nullTypeMismatch() {
    assertFalse(Types.check(null, ""));
  }

  @Test
  public void nullObjectMatch() {
    assertTrue(Types.check(String.class, null));
  }
}
