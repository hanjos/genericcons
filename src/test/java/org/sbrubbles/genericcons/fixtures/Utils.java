package org.sbrubbles.genericcons.fixtures;

import com.coekie.gentyref.TypeFactory;
import org.sbrubbles.genericcons.C;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {
  public static Type type(Class<?> cls, Type... arguments) {
    return TypeFactory.parameterizedClass(cls, arguments);
  }

  // doesn't work for length == 0, but close enough
  public static Type cons(Type... types) {
    if (types.length == 1) {
      return types[0];
    }

    Type[] rest = Arrays.copyOfRange(types, 1, types.length);
    return TypeFactory.parameterizedClass(C.class, types[0], cons(rest));
  }

  public static Type listOf(Type type) {
    return TypeFactory.parameterizedClass(List.class, type);
  }

  public static Type mapOf(Type key, Type value) {
    return TypeFactory.parameterizedClass(Map.class, key, value);
  }
}
