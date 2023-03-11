package com.mageddo.utils.dagger.mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CtxWrapper {

  private final Object delegate;

  public CtxWrapper(Object delegate) {
    this.delegate = delegate;
    this.validate();
  }

  public Object get(Class<?> clazz) {
    try {
      final var m = findMethod();
      return m.invoke(this.delegate, clazz);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(String.format("failed to obtain instance of: %s ", clazz.getName()));
    }
  }

  private void validate() {
    try {
      this.findMethod();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Method findMethod() throws NoSuchMethodException {
    return this.delegate.getClass().getMethod("get", Class.class);
  }

  public Object getDelegate() {
    return delegate;
  }
}
