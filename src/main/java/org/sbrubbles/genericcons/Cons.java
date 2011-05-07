package org.sbrubbles.genericcons;

import org.javaruntype.type.Type;
import org.javaruntype.type.Types;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
      result.add(convertToFullType(type));
      return result;
    }
    
    // recurse
    ParameterizedType cons = (ParameterizedType) type;
    
    java.lang.reflect.Type[] actualTypes = cons.getActualTypeArguments();
    assert actualTypes.length == 2;
    
    result.add(convertToFullType(actualTypes[0]));
    result.addAll(getTypesFrom(actualTypes[1]));
    
    return result;
  }

  private Type<?> convertToFullType(java.lang.reflect.Type type) throws InvalidTypeException {
    try {
      return Types.forJavaLangReflectType(type);
    } catch (Throwable t) {
      throw new InvalidTypeException(type, t);
    }
  }

  public final List<? extends Type<?>> getTypes() {
    return types;
  }
  
  @Override
  public final boolean equals(Object obj) {
    if(this == obj)
      return true;
    
    // obj should be an anonymous subclass of Cons
    // so getClass() comparisons fail
    if(obj == null || ! (obj instanceof Cons))
      return false;
    
    final Cons<?, ?> other = (Cons<?, ?>) obj;
    return (this.types.equals(other.types));
  }
  
  @Override
  public final int hashCode() {
    return this.types.hashCode();
  }
  
  @Override
  public final String toString() {
    String typesStr = this.types.toString();
    return "<" + typesStr.substring(1, typesStr.length() - 1) + ">";
  }
  
  // TODO check if a given argument list matches the types specified here
}
