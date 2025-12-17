package dagger.sheath.binding;

import org.junit.jupiter.api.Test;

import dagger.sheath.testing.stub.AppByBindingMap;
import dagger.sheath.testing.stub.AppByGetClass;
import dagger.sheath.testing.stub.DaggerAppByBindingMap;
import dagger.sheath.testing.stub.DaggerAppByGetClass;
import dagger.sheath.testing.stub.DaggerAppByProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BindingMethodTest {

  @Test
  void wontFindBindingMethodWhenTheCtxDontSupportsIt() {
    // arrange
    final var ctx = DaggerAppByProvider.create();
    final var nop = ctx.root();

    // act
    final var bindingMethod = BindingMethod.findBindingMethod(ctx);

    // assert
    assertNull(bindingMethod);
  }

  @Test
  void mustFindBeanUsingBindingMap() {
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
  void mustFindBeanUsingByGetClass() {
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
