package dagger.sheath.junit;

import dagger.sheath.EventHandler;
import dagger.sheath.NopEventHandler;
import dagger.sheath.NopSupplier;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Supplier;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DaggerExtension.class)
public @interface DaggerTest {

  /**
   * Instead of let DaggerTest create a dagger component directly using {@link #component()}, you can
   * specify a customized, no args Supplier to provide the component instance, it's useful when you need to run
   * some code at the tests before create the dagger component instance.
   * <p>
   * Obs: Be aware you can't access the Component Beans which will be mocked or spied otherwise DaggerTest won't
   * be able to mock them as Dagger will already has initialized them. If you need to access them,
   * then use {@link #eventsHandler()}
   */
  Class<? extends Supplier<?>> initializer() default NopSupplier.class;

  /**
   * Dagger componenent class, it can be the class annotated with `@Component` or the Dagger component generated class.
   */
  Class<?> component() default Void.class;

  /**
   * static Method on {@link #component()} to create the Dagger component instance.
   */
  String createMethod() default "create";

  /**
   *A handler class which you can listen to intermediate events on the test and do things with the Dagger Component.
   */
  Class<? extends EventHandler<?>> eventsHandler() default NopEventHandler.class;

}
