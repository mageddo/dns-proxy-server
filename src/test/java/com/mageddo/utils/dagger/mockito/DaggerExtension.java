package com.mageddo.utils.dagger.mockito;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

public class DaggerExtension implements Extension, BeforeAllCallback, BeforeEachCallback, ParameterResolver {

  private final static ExtensionContext.Namespace DAGGER = create("dagger2");
  private final static String DAGGER_CTX = "DAGGER_CTX";

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    final var settings = this.mustFindDaggerTestSettings(context);
    final var ctx = MethodUtils.invokeStaticMethod(settings.component(), settings.createMethod());
    context.getStore(DAGGER).put(DAGGER_CTX, ctx);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    final var testInstances = context.getRequiredTestInstances().getAllInstances();
    for (Object instance : testInstances) {
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

}
