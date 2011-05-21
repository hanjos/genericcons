package org.sbrubbles.genericcons;

import java.util.ArrayList;

import org.javaruntype.type.Types;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatchesTypeTest {
  @Test
  public void typeMatch() {
    assertTrue(C.matchesType(Types.STRING, ""));
  }
  
  @Test
  public void subtypeMatch() {
    assertTrue(C.matchesType(Types.SERIALIZABLE, new ArrayList<Object>()));
  }
  
  @Test
  public void typeMismatch() {
    assertFalse(C.matchesType(Types.STRING, new Object()));
  }
  
  @Test
  public void nullTypeMismatch() {
    assertFalse(C.matchesType(null, ""));
  }

  @Test
  public void nullObjectMismatch() {
    assertTrue(C.matchesType(Types.STRING, null));
  }
}
