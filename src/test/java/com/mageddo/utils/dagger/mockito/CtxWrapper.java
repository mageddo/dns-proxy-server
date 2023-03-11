package com.mageddo.utils.dagger.mockito;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
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

  public void initializeWithMockOrThrows(Class<?> type) {
    final var provider = this.findProviderFor(type);
    provider.mock();
  }

  ProviderWrapper findProviderFor(Class<?> type) {
    try {
      final var field = findFirstProviderFieldWithType(this.delegate.getClass(), type);
      final Object provider = FieldUtils.readField(field, this.delegate, true);
      return new ProviderWrapper(provider, type);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  static Field findFirstProviderFieldWithType(final Class<?> clazz, Class<?> wantedType) {
    final var fields = FieldUtils.getAllFields(clazz);
    for (final Field field : fields) {
      final var fieldType = Generics.getFirstFieldArg(field);
      if (Objects.equals(wantedType, fieldType)) {
        return field;
      }
    }
    return null;
  }
}
