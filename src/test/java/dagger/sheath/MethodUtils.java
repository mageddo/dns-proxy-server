package dagger.sheath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

public class MethodUtils {

  private MethodUtils() {
  }

  public static Set<Method> getAllMethods(Class<?> clazz) {
    final Set<Method> methods = new LinkedHashSet<>();
    for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
      for (final Method method : c.getDeclaredMethods()) {
        methods.add(method);
      }
    }
    return methods;
  }

  public static Object invoke(Method method, Object instance, boolean forceAccessible,
      Object... args)
      throws InvocationTargetException, IllegalAccessException {
    if (forceAccessible && !method.canAccess(instance)) {
      method.setAccessible(true);
    }
    return method.invoke(instance, args);
  }
}
