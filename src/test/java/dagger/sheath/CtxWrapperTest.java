package dagger.sheath;

import org.junit.jupiter.api.Test;

import dagger.sheath.testing.stub.AppByProvider;
import dagger.sheath.testing.stub.DaggerAppByProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CtxWrapperTest {

  @Test
  void mustFindByCtxMethod() {
    // arrange
    final var ctx = DaggerAppByProvider.create();
    ctx.root();

    final var wrapper = new CtxWrapper(ctx);

    // act
    final var result = wrapper.get(AppByProvider.Root.class);

    // assert
    assertNotNull(result);
  }
}
