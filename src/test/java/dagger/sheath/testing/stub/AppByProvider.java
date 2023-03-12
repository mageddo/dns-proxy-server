package dagger.sheath.testing.stub;

import dagger.Binds;
import dagger.Component;
import dagger.Module;

import javax.inject.Inject;

@Component(modules = AppByProvider.MainModule.class)
public interface AppByProvider {

  Root root();

  static class Root {

    final Iface iface;

    @Inject
    public Root(Iface iface) {
      this.iface = iface;
    }

  }

  interface Iface {
  }

  static class IfaceImpl implements Iface {
    @Inject
    public IfaceImpl() {
    }
  }

  @Module
  interface MainModule {
    @Binds
    Iface iface(IfaceImpl impl);
  }

}
