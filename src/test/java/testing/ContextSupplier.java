package testing;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.di.Context;

import java.util.function.Supplier;

public class ContextSupplier implements Supplier<Context> {
  @Override
  public Context get() {
    Configs.clear();
    Configs.getInstance();
    return Context.create();
  }

}
