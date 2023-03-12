package com.mageddo.utils.dagger.mockito;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
public class CtxWrapper {

  private final Object ctx;

  public CtxWrapper(Object ctx) {
    this.ctx = ctx;
    this.validate();
  }

  public Object get(Class<?> clazz) {
    final var provider = this.findProviderFor(clazz);
    if (provider != null) {
      log.debug("status=beanSolved, from=Provider, beanClass={}", clazz);
      return provider.getValue();
    }
    throw new UnsupportedOperationException();
//    this.findBindingsMethod();
  }

  private void validate() {
//    try {
//      this.findMethod();
//    } catch (NoSuchMethodException e) {
//      throw new RuntimeException(e);
//    }
  }

//  private Method findMethod() throws NoSuchMethodException {
//    return this.ctx.getClass().getMethod("get", Class.class);
//  }

  public Object getCtx() {
    return ctx;
  }

  public void initializeWithMockOrThrows(Class<?> type) {
    final var provider = this.findProviderFor(type);
    provider.mock();
  }

  ProviderWrapper findProviderFor(Class<?> type) {
    try {
      final var field = findFirstProviderFieldWithType(this.ctx.getClass(), type);
      if (field == null) {
        return null;
      }
//      Validate.notNull(field, "No provider found for type: %s", type);
      final Object provider = FieldUtils.readField(field, this.ctx, true);
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

  public Class<?> getCtxClass() {
    return this.ctx.getClass();
  }
}
