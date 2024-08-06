package dagger.sheath.junit;

import dagger.sheath.CtxWrapper;
import dagger.sheath.EventHandler;
import dagger.sheath.InjectMock;
import dagger.sheath.InjectSpy;
import dagger.sheath.NopSupplier;
import dagger.sheath.ProviderWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * Inspired on MockitoExtension and QuarkusTest
 */
@Slf4j
public class DaggerExtension implements Extension, BeforeAllCallback, AfterAllCallback,
  BeforeEachCallback, ParameterResolver {

  private final static ExtensionContext.Namespace DAGGER = create("dagger2");
  private final static String
    DAGGER_CTX = "DAGGER_CTX",
    DAGGER_CTX_WRAPPER = "DAGGER_CTX_WRAPPER",
    DAGGER_LIFECYCLE_HANDLER = "DAGGER_LIFECYCLE_HANDLER",
    DAGGER_SETUP = "DAGGER_SETUP";

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {

    final var settings = this.mustFindDaggerTestSettings(context);
    final var ctx = createCtx(settings);

    context.getStore(DAGGER).put(DAGGER_CTX, ctx);
    context.getStore(DAGGER).put(DAGGER_CTX_WRAPPER, new CtxWrapper(ctx));
    context.getStore(DAGGER).put(DAGGER_LIFECYCLE_HANDLER, createInstance(settings.eventsHandler()));

  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    triggerAfterAllEvent(context);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    log.debug("status=beforeEach");
    injectMocksAndSpies(context);
    injectFields(context);
    resetMocks(context);
    if (!Boolean.TRUE.equals(context.getStore(DAGGER).get(DAGGER_SETUP, Boolean.class))) {
      triggerSetupEvent(context);
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

  void resetMocks(ExtensionContext context) {
    final var testInstances = context.getRequiredTestInstances().getAllInstances();
    for (Object instance : testInstances) {
      final var fields = FieldUtils.getAllFields(instance.getClass());
      for (Field field : fields) {
        try {
          final var fieldValue = FieldUtils.readField(field, instance, true);
          if (MockUtil.isMock(fieldValue) || MockUtil.isSpy(fieldValue)) {
            Mockito.reset(fieldValue);
          }
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  void injectMocksAndSpies(ExtensionContext context) {
    final var testInstances = context.getRequiredTestInstances().getAllInstances();
    final var ctxWrapper = findCtxWrapper(context);
    for (Object testInstance : testInstances) {

      {
        final var fields = FieldUtils.getFieldsListWithAnnotation(testInstance.getClass(), InjectMock.class);
        inject(
          ctxWrapper,
          testInstance,
          fields,
          ProviderWrapper::mock,
          MockUtil::isMock
        );
      }
      {
        final var fields = FieldUtils.getFieldsListWithAnnotation(testInstance.getClass(), InjectSpy.class);
        inject(
          ctxWrapper,
          testInstance,
          fields,
          ProviderWrapper::spy,
          MockUtil::isSpy
        );
      }
    }
  }

  private static void inject(
    CtxWrapper ctxWrapper,
    Object instance,
    List<Field> fields,
    Consumer<ProviderWrapper> initializer,
    Predicate<Object> validator
  ) {
    for (Field field : fields) {
      try {
        log.debug("status=injectMockSpyInField, field={} {}", field.getType().getSimpleName(), field.getName());
        ctxWrapper.initializeWithOrThrows(field.getType(), initializer);
        final var mock = ctxWrapper.get(field.getType());
        if (!validator.test(mock)) {
          throw new IllegalStateException(String.format("Mock/Stub didn't work for type: %s", field.getType()));
        }
        writeTo(instance, field, mock);
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  DaggerTest mustFindDaggerTestSettings(ExtensionContext context) {
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
    for (Object testInstance : testInstances) {
      final var fields = FieldUtils.getFieldsListWithAnnotation(testInstance.getClass(), Inject.class);
      for (Field field : fields) {
        final var foundInstance = ctx.get(field.getType());
        writeTo(testInstance, field, foundInstance);
      }
    }
  }

  private static void writeTo(Object testInstance, Field field, Object foundInstance) throws IllegalAccessException {
    FieldUtils.writeField(field, testInstance, foundInstance, true);
    log.debug(
      "status=written, testClass={}, field={}, value={}, classToFind={}, generic={}",
      field.getName(), ClassUtils.getSimpleName(testInstance), foundInstance, field.getType(), field.getGenericType()
    );
  }

  static CtxWrapper findCtxWrapper(ExtensionContext context) {
    return context.getStore(DAGGER).get(DAGGER_CTX_WRAPPER, CtxWrapper.class);
  }

  static Object findCtx(ExtensionContext context) {
    return context.getStore(DAGGER).get(DAGGER_CTX);
  }

  static Object createCtx(DaggerTest settings) throws Exception {
    if (settings.component() != Void.class) {
      log.debug("strategy=component, component={}, method={}", settings.component(), settings.createMethod());
      return MethodUtils.invokeStaticMethod(settings.component(), settings.createMethod());
    } else if (settings.initializer() != NopSupplier.class) {
      final var instance = (Supplier) createInstance(settings.initializer());
      return instance.get();
    }
    throw new IllegalArgumentException("You need to inform either: component or initializer options");
  }

  static void triggerSetupEvent(ExtensionContext context) {
    findLifecycleHander(context).afterSetup(findCtx(context));
    final var parent = context.getParent().get();
    parent.getStore(DAGGER).put(DAGGER_SETUP, true);
    log.debug(
      "status=triggeredSetupEvent, context={}, parent={}, root={}",
      context.hashCode(), parent.hashCode(), context.getRoot().hashCode()
    );
  }

  private void triggerAfterAllEvent(ExtensionContext context) {
    findLifecycleHander(context).afterAll(findCtx(context));
    log.debug(
      "status=triggeredAfterAllEvent"
    );
  }

  private static EventHandler findLifecycleHander(ExtensionContext context) {
    return context
      .getStore(DAGGER)
      .get(DAGGER_LIFECYCLE_HANDLER, EventHandler.class);
  }

  static Object createInstance(Class<?> clazz) throws Exception {
    return clazz.getDeclaredConstructor().newInstance();
  }

}
