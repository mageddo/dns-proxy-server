package dagger.sheath.testing.stub;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import jdk.jfr.Name;
import org.apache.commons.lang3.Validate;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

@Component(modules = AppByGetClass.MainModule.class)
public interface AppByGetClass {

  Root root();

  @Name("bindings")
  default <T> T get(Class<T> clazz) {
    final var v = whatever().get(clazz);
    Validate.notNull(v, "Bean not found for class: %s", clazz.getName());
    return (T) v.get();
  }

  Map<Class<?>, Provider<Object>> whatever();

  class Root {

    final Iface iface;

    @Inject
    public Root(Iface iface) {
      this.iface = iface;
    }

  }

  interface Iface {
    String stuff();
  }

  class IfaceImpl implements Iface {
    @Inject
    public IfaceImpl() {
    }

    @Override
    public String stuff() {
      return "do";
    }
  }

  @Module
  interface MainModule {

    @Binds
    Iface iface(IfaceImpl impl);

    @Binds
    @IntoMap
    @ClassKey(Iface.class)
    Object b1(Iface bean);
  }

}
