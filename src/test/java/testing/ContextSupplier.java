package testing;

import java.util.function.Supplier;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.net.SocketUtils;

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
