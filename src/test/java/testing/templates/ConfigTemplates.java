package testing.templates;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.SolverDocker;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
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
      .serverProtocol(SimpleServer.Protocol.UDP_TCP)
      .hostMachineHostname("host.docker")
      .configPath(Paths.get("/tmp/config.json"))
      .webServerPort(8080)
      .version("3.0.0")
      .dnsServerPort(53)
      .logLevel(LogLevel.WARNING)
      .solverRemote(SolverRemote
        .builder()
        .active(true)
        .build()
      )
      .noEntriesResponseCode(3)
      .dockerSolverHostMachineFallbackActive(true)
      .solverDocker(SolverDocker
        .builder()
        .domain("docker")
        .registerContainerNames(false)
        .dpsNetwork(SolverDocker.DpsNetwork
          .builder()
          .autoConnect(false)
          .autoCreate(false)
          .build()
        )
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
