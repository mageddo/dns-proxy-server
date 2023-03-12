package dagger.sheath.binding;

import com.mageddo.commons.lang.Objects;
import dagger.sheath.CtxWrapper;
import jdk.jfr.Name;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
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
    final var methods = filterBindingMethods(ctx);
    for (final var method : methods) {
      final var bindingMethod = BindingMapMethod.of(ctx, method);
      if (bindingMethod != null) {
        return new BindingMethod(clazz -> Objects.mapOrNull(bindingMethod.get(clazz), Provider::get));
      } else if (isGetByClass(method)) {
        return buildGetByClass(ctx, method);
      }
    }
    return null;
  }

  public static BindingMapMethod findBindingMap(CtxWrapper ctx) {
    final var methods = filterBindingMethods(ctx);
    for (final var method : methods) {
      final var bindingMethod = BindingMapMethod.of(ctx, method);
      if (bindingMethod != null) {
        return bindingMethod;
      }
    }
    return null;
  }

  static List<Method> filterBindingMethods(CtxWrapper ctx) {
    return MethodUtils.getMethodsListWithAnnotation(ctx.getCtxClass(), Name.class, true, true)
        .stream()
        .filter(it -> it.getAnnotation(Name.class).value().equals("bindings"))
        .collect(Collectors.toList());
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

}
