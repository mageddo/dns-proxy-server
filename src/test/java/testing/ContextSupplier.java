package testing;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.provider.cmdargs.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.net.SocketUtils;

import java.util.function.Supplier;

public class ContextSupplier implements Supplier<Context> {
  @Override
  public Context get() {
    final var port = SocketUtils.findRandomFreePort();
    Configs.clear();
    ConfigDAOCmdArgs.setArgs(new String[]{"--web-server-port=" + port});
    Configs.getInstance();
    return Context.create();
  }

}
