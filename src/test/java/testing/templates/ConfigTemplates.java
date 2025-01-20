package testing.templates;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.Server;
import com.mageddo.dnsproxyserver.config.SolverDocker;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.SolverSystem;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsserver.SimpleServer;

import java.nio.file.Paths;

public class ConfigTemplates {

  public static Config defaultWithoutId() {
    return defaultBuilder()
      .build();
  }

  private static Config.ConfigBuilder defaultBuilder() {
    return Config
      .builder()
      .server(Server
        .builder()
        .serverProtocol(SimpleServer.Protocol.UDP_TCP)
        .webServerPort(8080)
        .dnsServerPort(53)
        .dnsServerNoEntriesResponseCode(3)
        .build()
      )
      .logFile("/tmp/dps.log")
      .defaultDns(Config.DefaultDns
        .builder()
        .active(true)
        .resolvConf(Config.DefaultDns.ResolvConf
          .builder()
          .paths(ConfigEnv.DEFAULT_RESOLV_CONF_PATH)
          .overrideNameServers(true)
          .build()
        )
        .build()
      )
      .configPath(Paths.get("/tmp/config.json"))
      .version("3.0.0")
      .logLevel(LogLevel.WARNING)
      .solverRemote(SolverRemote
        .builder()
        .active(true)
        .build()
      )
      .solverDocker(SolverDocker
        .builder()
        .domain("docker")
        .registerContainerNames(false)
        .hostMachineFallback(true)
        .dpsNetwork(SolverDocker.DpsNetwork
          .builder()
          .autoConnect(false)
          .autoCreate(false)
          .build()
        )
        .build()
      )
      .solverSystem(SolverSystem
        .builder()
        .hostMachineHostname("host.docker")
        .build()
      )
      .source(Config.Source.TESTS_TEMPLATE)
      ;
  }


  public static Config withRegisterContainerNames() {
    final var builder = defaultBuilder();
    final var tmp = builder.build();
    return builder
      .solverDocker(
        tmp.getSolverDocker()
          .toBuilder()
          .registerContainerNames(true)
          .build()
      )
      .build();
  }

  public static Config withSolverRemoteDisabled() {
    return defaultBuilder()
      .solverRemote(SolverRemote
        .builder()
        .active(false)
        .build()
      )
      .build();
  }

  public static Config acmeSolverStub() {
    return defaultBuilder()
      .solverStub(SolverStub.builder()
        .domainName("acme")
        .build()
      )
      .build();
  }
}
