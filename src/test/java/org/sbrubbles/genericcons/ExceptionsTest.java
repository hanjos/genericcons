package org.sbrubbles.genericcons;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ExceptionsTest {
  @Test
  public void exceptionsShouldBeSerializable() throws IOException, ClassNotFoundException {
    assertExceptionIsSerializable(new ParameterizedTypeNotFoundException());
  }

  private void assertExceptionIsSerializable(Throwable original) throws IOException, ClassNotFoundException {
    Class<? extends Throwable> type = original.getClass();

    // checking that the types match
    Throwable copy = type.cast(deserialize(serialize(original)));

    // XXX .equals won't work between exceptions, but here, if the types and messages match, we're good
    assertEquals(original.getMessage(), copy.getMessage());
  }

  private byte[] serialize(Object o) throws IOException {
    try (ByteArrayOutputStream bs = new ByteArrayOutputStream(1024);
         ObjectOutputStream os = new ObjectOutputStream(bs)) {
      os.writeObject(o);
      os.flush();

      return bs.toByteArray();
    }
  }

  private Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
         ObjectInputStream os = new ObjectInputStream(bs)) {
      return os.readObject();
    }
  }
}
