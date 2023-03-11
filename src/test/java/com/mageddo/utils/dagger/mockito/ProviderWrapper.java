package com.mageddo.utils.dagger.mockito;

import dagger.internal.DoubleCheck;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.util.Objects;

public class ProviderWrapper {

  private final Class<?> type;
  private final Object provider;

  public ProviderWrapper(Object provider, Class<?> type) {
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

      Validate.isTrue(
        Objects.equals(uninitialized, instance) || MockUtil.isMock(instance) || MockUtil.isSpy(instance),
        "Dagger beans were already used, can't mock/spy anymore, please wait DaggerTest to mock them"
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

}
