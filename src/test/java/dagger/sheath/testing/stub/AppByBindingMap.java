package dagger.sheath.testing.stub;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import jdk.jfr.Name;

@Component(modules = AppByBindingMap.MainModule.class)
public interface AppByBindingMap {

  Root root();

  @Name("bindings")
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
