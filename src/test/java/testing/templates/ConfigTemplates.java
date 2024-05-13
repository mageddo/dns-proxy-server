package testing.templates;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigEnv;
import com.mageddo.dnsproxyserver.config.entrypoint.LogLevel;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;

import java.nio.file.Paths;

public class ConfigTemplates {
  public static Config withoutId() {
    return defaultBuilder()
      .build();
  }

  private static Config.ConfigBuilder defaultBuilder() {
    return Config
      .builder()
      .logFile("/tmp/dps.log")
      .defaultDns(false)
      .dpsNetworkAutoConnect(false)
      .hostMachineHostname("host.docker")
      .configPath(Paths.get("/tmp/config.json"))
      .registerContainerNames(false)
      .mustConfigureDpsNetwork(false)
      .webServerPort(8080)
      .version("3.0.0")
      .dnsServerPort(53)
      .domain("com")
      .logLevel(LogLevel.WARNING)
      .resolvConfPaths(ConfigEnv.DEFAULT_RESOLV_CONF_PATH)
      .serverProtocol(SimpleServer.Protocol.UDP_TCP)
      ;
  }


}
