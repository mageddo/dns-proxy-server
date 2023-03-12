package com.mageddo.utils.dagger.mockito.haystack;

import com.mageddo.utils.dagger.mockito.CtxWrapper;
import jdk.jfr.Name;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BindingMethod {

  private final Function<Class, Object> mapper;

  public BindingMethod(Function<Class, Object> mapper) {
    this.mapper = mapper;
  }

  public <T> T get(Class<T> clazz) {
    return (T) this.mapper.apply(clazz);
  }

  public static BindingMethod findBindingMethod(Object ctx) {
    return findBindingMethod(new CtxWrapper(ctx));
  }

  public static BindingMethod findBindingMethod(CtxWrapper ctx) {
    final var methods = MethodUtils.getMethodsListWithAnnotation(ctx.getCtxClass(), Name.class, true, true)
        .stream()
        .filter(it -> it.getAnnotation(Name.class).value().equals("bindings"))
        .collect(Collectors.toList());

    for (final var method : methods) {
      if (isGetByClass(method)) {
        return buildGetByClass(ctx, method);
      } else if (isGetBindingsMap(method)) {
        return buildGetByBindingMaps(ctx, method);
      }
    }
    return null;
  }

  static BindingMethod buildGetByBindingMaps(CtxWrapper ctx, Method method) {
    return new BindingMethod(clazz -> {
      try {
        final Map<Class<?>, Provider<?>> bindings = (Map<Class<?>, Provider<?>>) method.invoke(ctx.getCtx());
        final Provider<?> provider = bindings.get(clazz);
        if (provider != null) {
          return provider.get();
        }
        return null;
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  static BindingMethod buildGetByClass(CtxWrapper ctx, Method method) {
    return new BindingMethod(clazz -> {
      try {
        return method.invoke(ctx.getCtx(), clazz);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  static boolean isGetByClass(Method m) {
    return m.getReturnType() != Void.TYPE
        && m.getParameterTypes().length == 1
        && m.getParameterTypes()[0].isAssignableFrom(Class.class)
        ;
  }

  static boolean isGetBindingsMap(Method m) {
    return m.getParameterTypes().length == 0
        && m.getReturnType().isAssignableFrom(Map.class)
        ;
  }
}
