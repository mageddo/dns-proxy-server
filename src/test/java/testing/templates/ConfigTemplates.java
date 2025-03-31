package testing.templates;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Log;
import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.Server;
import com.mageddo.dnsproxyserver.config.SolverDocker;
import com.mageddo.dnsproxyserver.config.SolverLocal;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.SolverSystem;
import com.mageddo.dnsproxyserver.config.provider.legacyenv.ConfigEnv;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IP;

import java.nio.file.Paths;
import java.util.List;

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
      .log(Log
        .builder()
        .file("/tmp/dps.log")
        .level(LogLevel.WARNING)
        .build()
      )
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

  public static Config acmeSolverLocal() {
    return defaultBuilder()
      .solverLocal(SolverLocal.builder()
        .activeEnv(Config.Env.DEFAULT_ENV)
        .envs(List.of(
          Config.Env.of("", List.of(
            Config.Entry
              .builder()
              .hostname("acme.com")
              .ip(IP.of("192.168.0.3"))
              .type(Config.Entry.Type.A)
              .ttl(300)
              .build()
          ))
        ))
        .build()
      )
      .build();
  }
}
