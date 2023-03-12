package com.mageddo.utils.dagger.mockito.haystack;

import org.junit.jupiter.api.Test;
import sheath.stubing.AppByBinding;
import sheath.stubing.AppByProvider;
import sheath.stubing.DaggerAppByBinding;
import sheath.stubing.DaggerAppByProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BindingMethodTest {

  @Test
  void wontFindBindingMethodWhenTheCtxDontSupportsIt(){
    // arrange
    final var ctx = DaggerAppByProvider.create();
    final var nop = ctx.root();

    // act
    final var bindingMethod = BindingMethod.findBindingsMethod(ctx);

    // assert
    assertNull(bindingMethod.get(AppByProvider.Iface.class));
  }

  @Test
  void mustFindBeanUsingGetByClass(){
    // arrange
    final var ctx = DaggerAppByBinding.create();
    final var nop = ctx.root();

    // act
    final var bindingMethod = BindingMethod.findBindingsMethod(ctx);

    // assert
    final var instance = bindingMethod.get(AppByBinding.Iface.class);
    assertNotNull(instance);
    assertEquals("do", instance.stuff());
  }

}
