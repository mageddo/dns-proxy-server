package dagger.sheath.binding;

import dagger.sheath.CtxWrapper;

import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class BindingMapMethod {

  private final Object ctx;
  private final Method method;

  public BindingMapMethod(Object ctx, Method method) {
    this.ctx = ctx;
    this.method = method;
  }

  public <T> Provider<T> get(Class<T> clazz) {
    try {
      final Map<Class<?>, Provider<?>> bindings = (Map<Class<?>, Provider<?>>) this.method.invoke(this.ctx);
      return (Provider<T>) bindings.get(clazz);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  public static BindingMapMethod of(CtxWrapper ctx, Method method) {
    if (isGetBindingsMap(method)) {
      return buildGetByBindingMaps(ctx, method);
    }
    return null;
  }

  private static boolean isGetBindingsMap(Method m) {
    return m.getParameterTypes().length == 0
        && m.getReturnType().isAssignableFrom(Map.class)
        ;
  }

  private static BindingMapMethod buildGetByBindingMaps(CtxWrapper ctx, Method method) {
    return new BindingMapMethod(ctx.getCtx(), method);
  }

}
