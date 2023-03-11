package com.mageddo.utils.dagger.mockito;

import java.lang.reflect.InvocationTargetException;

public class CtxWrapper {

  private final Object delegate;

  public CtxWrapper(Object delegate) {
    this.delegate = delegate;
  }

  public Object get(Class<?> clazz) {
    try {
      final var m = this.delegate.getClass().getMethod("get", Class.class);
      return m.invoke(this.delegate, clazz);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(String.format("failed to obtain instance of: %s ", clazz.getName()));
    }
  }

  public Object getDelegate() {
    return delegate;
  }
}
