package dagger.sheath;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderWrapper {

  private final Class<?> type;
  private final Object provider;

  private ProviderWrapper(Object provider, Class<?> type) {
    this.provider = provider;
    this.type = type;
  }

  public void mock() {
    this.initialize(Mockito.mock(this.type));
  }

  public void spy() {
    this.initialize(Mockito.spy(this.type));
  }

  void initialize(Object o) {
    try {
      final Object uninitialized = this.findUnitializedValue();
      final var instanceField = FieldUtils.getField(DoubleCheck.class, "instance", true);
      final var instance = FieldUtils.readField(instanceField, this.provider, true);
      if (MockUtil.isMock(instance) || MockUtil.isSpy(instance)) {
        log.debug("status=alreadyMocked, type={}", this.type);
        return;
      }
      Validate.isTrue(
          Objects.equals(uninitialized, instance),
          "Dagger beans were already used, can't mock/spy anymore, please wait DaggerTest to mock"
              + " them"
      );
      FieldUtils.writeField(instanceField, this.provider, o, true);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  Object findUnitializedValue() {
    try {
      final var uninitializedField = FieldUtils.getField(DoubleCheck.class, "UNINITIALIZED", true);
      return FieldUtils.readField(uninitializedField, this.provider, true);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public Object getValue() {
    try {
      return MethodUtils.invokeMethod(this.provider, "get");
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  public static ProviderWrapper from(Object o, Class<?> type) {
    try {
      if (o instanceof DelegateFactory<?>) {
        final var delegateField = FieldUtils.getField(DelegateFactory.class, "delegate", true);
        final var provider = FieldUtils.readField(delegateField, o);
        validateIsProviderClass(provider);
        return new ProviderWrapper(provider, type);
      }
      Validate.notNull(o, "Object is type=%s", type);
      if (isDoubleCheckClass(o)) {
        return new ProviderWrapper(o, type);
      }
      final var providerClass = ClassUtils.getName(o);
      Validate.isTrue(isGeneratedFactory(providerClass), "Unknown dagger provider: %s",
          providerClass
      );
      return new ProviderWrapper(o, type);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private static boolean isGeneratedFactory(String providerClass) {
    return !providerClass.startsWith("dagger.");
  }

  private static void validateIsProviderClass(Object provider) {
    Validate.isTrue(isDoubleCheckClass(provider), "Not the expected provider class!: %s", provider);
  }

  private static boolean isDoubleCheckClass(Object provider) {
    return Objects.equals(DoubleCheck.class, provider.getClass());
  }
}
