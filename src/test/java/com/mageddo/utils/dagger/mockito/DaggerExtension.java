package com.mageddo.utils.dagger.mockito;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * Inspired on MockitoExtension and QuarkusTest
 */
@Slf4j
public class DaggerExtension implements Extension, BeforeAllCallback, BeforeEachCallback, ParameterResolver {

  private final static ExtensionContext.Namespace DAGGER = create("dagger2");
  private final static String
    DAGGER_CTX = "DAGGER_CTX",
    DAGGER_CTX_WRAPPER = "DAGGER_CTX_WRAPPER";

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {

    final var settings = this.mustFindDaggerTestSettings(context);
    final var ctx = MethodUtils.invokeStaticMethod(settings.component(), settings.createMethod());

    context.getStore(DAGGER).put(DAGGER_CTX, ctx);
    context.getStore(DAGGER).put(DAGGER_CTX_WRAPPER, new CtxWrapper(ctx));

  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    injectMocks(context);
    injectFields(context);
  }

  void injectMocks(ExtensionContext context) {
    final var testInstances = context.getRequiredTestInstances().getAllInstances();
    for (Object instance : testInstances) {
      final var fields = FieldUtils.getFieldsListWithAnnotation(instance.getClass(), InjectMock.class);
      for (Field field : fields) {
        try {
          log.debug("status=injectMockField, field={} {}", field.getType().getSimpleName(), field.getName());
          // creating a mock for the field
          final Object mock = Mockito.mock(field.getType());
          FieldUtils.writeField(field, instance, mock, true);

          // replacing real bean with the created mock
          mockDaggerCtxBean(mock, field.getType(), findCtx(context));

        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  void mockDaggerCtxBean(Object mock, Class<?> mockClazz, Object daggerGraph) {
    final var fields = FieldUtils.getAllFields(daggerGraph.getClass());
    for (final Field field : fields) {
      try {
        final var fieldType = Generics.getFirstFieldArg(field);
        log.trace("status=daggerMocking, mockClass={}, fieldType={}", mockClazz, fieldType);
        if (Objects.equals(mockClazz, fieldType)) {
          log.debug("status=daggerMocking, class={}", fieldType);
          FieldUtils.writeField(field, daggerGraph, (Provider<?>) () -> mock, true);
        }
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    return resolveParameter(parameterContext, extensionContext) != null;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    final Class<?> pClass = parameterContext.getParameter().getType();
    final var daggerCtx = extensionContext.getStore(DAGGER).get(DAGGER_CTX);
    if (pClass.isAssignableFrom(daggerCtx.getClass())) {
      return daggerCtx;
    }
    return null;
  }

  private DaggerTest mustFindDaggerTestSettings(ExtensionContext context) {
    return this.findAnnotation(context, DaggerTest.class)
      .orElseThrow(() -> new IllegalArgumentException("You need to use @DaggerTest annotation"))
      ;
  }

  <T extends Annotation> Optional<T> findAnnotation(final ExtensionContext context, final Class<T> ann) {
    ExtensionContext currentContext = context;
    Optional<T> annotation;
    do {
      annotation = AnnotationSupport.findAnnotation(currentContext.getElement(), ann);

      if (!currentContext.getParent().isPresent()) {
        break;
      }

      currentContext = currentContext.getParent().get();
    } while (!annotation.isPresent() && currentContext != context.getRoot());

    return annotation;
  }


  static void injectFields(ExtensionContext context) throws IllegalAccessException {
    final var ctx = findCtxWrapper(context);
    final var testInstances = context.getRequiredTestInstances().getAllInstances();
    for (Object instance : testInstances) {
      final var fields = FieldUtils.getFieldsListWithAnnotation(instance.getClass(), Inject.class);
      for (Field field : fields) {
        FieldUtils.writeField(field, instance, getAndReset(ctx, field), true);
      }
    }
  }

  static Object getAndReset(CtxWrapper ctx, Field field) {
    final var instance = ctx.get(field.getType());
    if (MockUtil.isMock(instance) || MockUtil.isSpy(instance)) {
      Mockito.reset(instance);
    }
    return instance;
  }

  static CtxWrapper findCtxWrapper(ExtensionContext context) {
    return context.getStore(DAGGER).get(DAGGER_CTX_WRAPPER, CtxWrapper.class);
  }

  static Object findCtx(ExtensionContext context) {
    return context.getStore(DAGGER).get(DAGGER_CTX);
  }
}
