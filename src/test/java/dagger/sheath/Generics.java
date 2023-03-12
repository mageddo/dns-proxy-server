package dagger.sheath;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Generics {

  private Generics() {
  }

  public static Type getFirstTypeArg(Type type) {
    final var args = getTypeArgs(type);
    return args != null && args.length > 0 ? args[0] : null;
  }

  public static Type[] getTypeArgs(Type type) {
    if (!(type instanceof ParameterizedType)) {
      return null;
    }
    final var ptype = (ParameterizedType) type;
    return ptype.getActualTypeArguments();
  }

  public static Type[] getFieldArgs(Field f) {
    return getTypeArgs(f.getGenericType());
  }

  public static Type getFirstFieldArg(Field f) {
    return getFirstTypeArg(f.getGenericType());
  }

}
