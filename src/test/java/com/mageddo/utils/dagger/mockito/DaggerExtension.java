package com.mageddo.utils.dagger.mockito;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DaggerExtension implements Extension, BeforeEachCallback, ParameterResolver {

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
    if (pClass.equals(ContextConsumer.class)) {
      return new ContextConsumer();
    }
    return null;
  }
}
