package org.sbrubbles.genericcons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;

public class MatchesTypeTest {
  @Test
  public void typeMatch() {
    assertTrue(C.matchesType(String.class, ""));
  }
  
  @Test
  public void subtypeMatch() {
    assertTrue(C.matchesType(Serializable.class, new ArrayList<Object>()));
  }
  
  @Test
  public void typeMismatch() {
    assertFalse(C.matchesType(String.class, new Object()));
  }
  
  @Test
  public void nullTypeMismatch() {
    assertFalse(C.matchesType(null, ""));
  }

  @Test
  public void nullObjectMatch() {
    assertTrue(C.matchesType(String.class, null));
  }
}
