package testing;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.di.Context;

import java.util.function.Supplier;

public class ContextSupplier implements Supplier<Context> {
  @Override
  public Context get() {
    Configs.clear();
    Configs.getInstance(new String[]{"--web-server-port=9944"});
    return Context.create();
  }

}
