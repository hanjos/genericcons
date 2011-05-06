package org.sbrubbles.variablegenerics;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.javaruntype.type.Type;
import org.javaruntype.type.Types;

public class Cons<First, Rest> {
  private List<? extends Type<?>> types;
  
  public Cons() {
    types = Collections.unmodifiableList(detectTypes());
  }

  private List<? extends Type<?>> detectTypes() {
    java.lang.reflect.Type superclass = this.getClass().getGenericSuperclass();
    
    if(! (superclass instanceof ParameterizedType))
      throw new MissingTypeParametersException(this.getClass());
    
    ParameterizedType cons = (ParameterizedType) superclass;
    if(cons.getRawType() != Cons.class)
      throw new UnexpectedTypeException(cons, Cons.class);
    
    return getTypesFrom(cons);
  }

  private List<? extends Type<?>> getTypesFrom(java.lang.reflect.Type type) {
    assert type != null;
    
    List<Type<?>> result = new ArrayList<Type<?>>();
    
    // end of recursion, add it and return
    if(! (type instanceof ParameterizedType) || ((ParameterizedType) type).getRawType() != Cons.class) {
      result.add(Types.forJavaLangReflectType(type));
      return result;
    }
    
    // recurse
    ParameterizedType cons = (ParameterizedType) type;
    
    java.lang.reflect.Type[] actualTypes = cons.getActualTypeArguments();
    assert actualTypes.length == 2;
    
    result.add(Types.forJavaLangReflectType(actualTypes[0]));
    result.addAll(getTypesFrom(actualTypes[1]));
    
    return result;
  }

  public final List<? extends Type<?>> getTypes() {
    return types;
  }
  
  // TODO check if a given argument list matches the types specified here
  // TODO equality
  // TODO toString
}
