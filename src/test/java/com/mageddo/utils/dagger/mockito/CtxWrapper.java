package com.mageddo.utils.dagger.mockito;

import com.mageddo.utils.dagger.mockito.haystack.BindingMethod;
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
    try {
      final var provider = this.findProviderFor(clazz);
      if (provider != null) {
        log.debug("status=beanSolved, from=Provider, beanClass={}", clazz);
        return provider.getValue();
      }
    } catch (Throwable e) {
      log.warn("status=failedToFindByProvider, msg={}", e.getMessage());
    }
    try {
      final var bindingMethod = BindingMethod.findBindingMethod(this);
      if (bindingMethod == null) {
        final Object result = bindingMethod.get(clazz);
        if (result != null) {
          return result;
        }
      }
    } catch (Throwable e) {
      log.warn("status=failedToFindByBinding, msg={}", e.getMessage());
    }

    return null;

    // todo procurar a classe que o obj grah impl estende ou a interface que ele implementa
    //  Pegar a anotação @Component e pegar os modulos
    //  andar pelos metodos de cada modulo procurando pelo método que retorna o tipo da interface desejada
    //  e que tenha @Binds , provides nao serve como ele pode receber um tipo pra internamente montar o
    //  tipo retornado mas daih nao da obter a instancia

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
