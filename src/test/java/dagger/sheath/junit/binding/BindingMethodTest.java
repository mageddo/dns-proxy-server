package dagger.sheath.junit.binding;

import org.junit.jupiter.api.Test;
import sheath.stubing.AppByBindingMap;
import sheath.stubing.AppByGetClass;
import sheath.stubing.DaggerAppByBindingMap;
import sheath.stubing.DaggerAppByGetClass;
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
    final var bindingMethod = BindingMethod.findBindingMethod(ctx);

    // assert
    assertNull(bindingMethod);
  }

  @Test
  void mustFindBeanUsingBindingMap(){
    // arrange
    final var ctx = DaggerAppByBindingMap.create();
    final var nop = ctx.root();

    // act
    final var bindingMethod = BindingMethod.findBindingMethod(ctx);

    // assert
    assertNotNull(bindingMethod);
    final var instance = bindingMethod.get(AppByBindingMap.Iface.class);
    assertNotNull(instance);
    assertEquals("do", instance.stuff());
  }

  @Test
  void mustFindBeanUsingByGetClass(){
    // arrange
    final var ctx = DaggerAppByGetClass.create();
    final var nop = ctx.root();

    // act
    final var bindingMethod = BindingMethod.findBindingMethod(ctx);

    // assert
    assertNotNull(bindingMethod);
    final var instance = bindingMethod.get(AppByGetClass.Iface.class);
    assertNotNull(instance);
    assertEquals("do", instance.stuff());
  }

}
