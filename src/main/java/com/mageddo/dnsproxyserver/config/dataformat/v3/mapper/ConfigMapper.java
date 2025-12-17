package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import java.net.URI;
import java.util.Objects;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.NonResilientCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class ConfigMapper {

  public static final int VERSION = 3;

  public static Config of(final ConfigV3 c, Config.Source source) {
    if (c == null) {
      return null;
    }

    return Config.builder()
        .version(String.valueOf(c.getVersion()))
        .server(mapServer(c.getServer()))
        .defaultDns(mapDefaultDns(c.getDefaultDns()))
        .log(mapLog(c.getLog()))
        .solverRemote(mapSolverRemote(c.getSolver()))
        .solverDocker(mapSolverDocker(c.getSolver()))
        .solverLocal(mapSolverLocal(c.getSolver()))
        .solverStub(mapSolverStub(c.getSolver()))
        .solverSystem(mapSolverSystem(c.getSolver()))
        .source(source)
        .build();
  }

  public static ConfigV3 toV3(final Config config) {
    if (config == null) {
      return null;
    }

    return new ConfigV3()
        .setVersion(VERSION)
        .setServer(mapServerV3(config))
        .setDefaultDns(mapDefaultDnsV3(config.getDefaultDns()))
        .setLog(mapLogV3(config.getLog()))
        .setSolver(mapSolverV3(config));
  }

  /* ================= SERVER ================= */

  private static Config.Server mapServer(final ConfigV3.Server s) {
    if (s == null) {
      return null;
    }

    return Config.Server.builder()
        .dnsServerPort(s.getDns() != null ? s.getDns()
            .getPort() : null)
        .dnsServerNoEntriesResponseCode(
            s.getDns() != null ? s.getDns()
                .getNoEntriesResponseCode() : null
        )
        .webServerPort(s.getWeb() != null ? s.getWeb()
            .getPort() : null)
        .serverProtocol(
            s.getProtocol() != null
                ? SimpleServer.Protocol.valueOf(s.getProtocol())
                : null
        )
        .build();
  }

  private static ConfigV3.Server mapServerV3(final Config config) {
    if (config.getServer() == null) {
      return null;
    }

    return new ConfigV3.Server()
        .setDns(new ConfigV3.Dns()
            .setPort(config.getDnsServerPort())
            .setNoEntriesResponseCode(config.getNoEntriesResponseCode())
        )
        .setWeb(new ConfigV3.Web()
            .setPort(config.getWebServerPort())
        )
        .setProtocol(Objects.toString(config.getServerProtocol(), null));
  }

  /* ================= DEFAULT DNS ================= */

  private static Config.DefaultDns mapDefaultDns(final ConfigV3.DefaultDns d) {
    if (d == null) {
      return null;
    }

    return Config.DefaultDns.builder()
        .active(d.getActive())
        .resolvConf(
            d.getResolvConf() == null
                ? null
                : Config.DefaultDns.ResolvConf.builder()
                .paths(d.getResolvConf()
                    .getPaths())
                .overrideNameServers(d.getResolvConf()
                    .getOverrideNameServers())
                .build()
        )
        .build();
  }

  private static ConfigV3.DefaultDns mapDefaultDnsV3(final Config.DefaultDns d) {
    if (d == null) {
      return null;
    }

    return new ConfigV3.DefaultDns()
        .setActive(d.getActive())
        .setResolvConf(
            d.getResolvConf() == null
                ? null
                : new ConfigV3.ResolvConf()
                .setPaths(d.getResolvConf()
                    .getPaths())
                .setOverrideNameServers(d.getResolvConf()
                    .getOverrideNameServers())
        );
  }

  /* ================= LOG ================= */

  private static Config.Log mapLog(final ConfigV3.Log l) {
    if (l == null) {
      return null;
    }

    return Config.Log.builder()
        .level(l.getLevel() != null ? Config.Log.Level.valueOf(l.getLevel()) : null)
        .file(l.getFile())
        .build();
  }

  private static ConfigV3.Log mapLogV3(final Config.Log l) {
    if (l == null) {
      return null;
    }

    return new ConfigV3.Log()
        .setLevel(l.getLevel() != null ? l.getLevel()
            .name() : null)
        .setFile(l.getFile());
  }

  /* ================= SOLVERS ================= */

  private static Config.SolverRemote mapSolverRemote(final ConfigV3.Solver s) {
    if (s == null || s.getRemote() == null) {
      return null;
    }

    return Config.SolverRemote.builder()
        .active(s.getRemote()
            .getActive())
        .dnsServers(
            s.getRemote()
                .getDnsServers() == null
                ? emptyList()
                : s.getRemote()
                .getDnsServers()
                .stream()
                .map(IpAddr::of)
                .collect(toList())
        )
        .circuitBreaker(mapCircuitBreakerToDomain(s.getRemote()
            .getCircuitBreaker()))
        .build();
  }

  private static Config.SolverDocker mapSolverDocker(final ConfigV3.Solver s) {
    if (s == null || s.getDocker() == null) {
      return null;
    }

    return Config.SolverDocker.builder()
        .registerContainerNames(s.getDocker()
            .getRegisterContainerNames())
        .domain(s.getDocker()
            .getDomain())
        .hostMachineFallback(s.getDocker()
            .getHostMachineFallback())
        .dockerDaemonUri(
            s.getDocker()
                .getDockerDaemonUri() != null
                ? URI.create(s.getDocker()
                .getDockerDaemonUri())
                : null
        )
        .dpsNetwork(
            s.getDocker()
                .getDpsNetwork() == null
                ? null
                : Config.SolverDocker.DpsNetwork.builder()
                .autoCreate(s.getDocker()
                    .getDpsNetwork()
                    .getAutoCreate())
                .autoConnect(s.getDocker()
                    .getDpsNetwork()
                    .getAutoConnect())
                .build()
        )
        .build();
  }

  private static Config.SolverLocal mapSolverLocal(final ConfigV3.Solver s) {
    if (s == null || s.getLocal() == null) {
      return null;
    }

    return Config.SolverLocal.builder()
        .activeEnv(s.getLocal()
            .getActiveEnv())
        .envs(
            s.getLocal()
                .getEnvs() == null
                ? emptyList()
                : s.getLocal()
                .getEnvs()
                .stream()
                .map(ConfigMapper::mapEnv)
                .collect(toList())
        )
        .build();
  }

  private static Config.Env mapEnv(final ConfigV3.Env e) {
    return Config.Env.of(
        e.getName(),
        e.getHostnames() == null
            ? emptyList()
            : e.getHostnames()
            .stream()
            .map(ConfigMapper::mapEntry)
            .collect(toList())
    );
  }

  private static Config.Entry mapEntry(final ConfigV3.Hostname h) {
    return Config.Entry.builder()
        .hostname(h.getHostname())
        .ttl(h.getTtl())
        .type(Config.Entry.Type.valueOf(h.getType()))
        .target(h.getTarget())
        .ip(h.getIp() != null ? IP.of(h.getIp()) : null)
        .build();
  }

  private static Config.SolverStub mapSolverStub(final ConfigV3.Solver s) {
    if (s == null || s.getStub() == null) {
      return null;
    }

    return Config.SolverStub.builder()
        .domainName(s.getStub()
            .getDomainName())
        .build();
  }

  private static Config.SolverSystem mapSolverSystem(final ConfigV3.Solver s) {
    if (s == null || s.getSystem() == null) {
      return null;
    }

    return Config.SolverSystem.builder()
        .hostMachineHostname(s.getSystem()
            .getHostMachineHostname())
        .build();
  }

  private static ConfigV3.Solver mapSolverV3(final Config config) {
    final var solver = new ConfigV3.Solver();

    if (config.getSolverRemote() != null) {
      solver.setRemote(new ConfigV3.Remote()
          .setActive(config.isSolverRemoteActive())
          .setDnsServers(
              config.getRemoteDnsServers()
                  .stream()
                  .map(IpAddr::toString)
                  .collect(toList())
          )
          .setCircuitBreaker(mapCircuitBreakerToV3(config.getSolverRemoteCircuitBreakerStrategy()))
      );
    }

    if (config.getSolverDocker() != null) {
      solver.setDocker(new ConfigV3.Docker()
              .setDomain(config.getDockerDomain())
              .setRegisterContainerNames(config.getRegisterContainerNames())
              .setHostMachineFallback(config.getDockerSolverHostMachineFallbackActive())
              .setDockerDaemonUri(Objects.toString(config.getDockerDaemonUri(), null))
              .setDpsNetwork(
                  config.getDockerSolverDpsNetwork() == null
                      ? null
                      : new ConfigV3.DpsNetwork()
//                      .setName(config.getDockerSolverDpsNetwork().getName())
                      .setAutoCreate(config.getDockerSolverDpsNetwork()
                          .getAutoCreate())
                      .setAutoConnect(config.getDockerSolverDpsNetwork()
                          .getAutoConnect())
              )
      );
    }

    if (config.getSolverLocal() != null) {
      solver.setLocal(new ConfigV3.Local()
          .setActiveEnv(config.getActiveEnv())
          .setEnvs(
              config.getEnvs() == null
                  ? emptyList()
                  : config.getEnvs()
                  .stream()
                  .map(env -> new ConfigV3.Env()
                      .setName(env.getName())
                      .setHostnames(
                          env.getEntries() == null
                              ? emptyList()
                              : env.getEntries()
                              .stream()
                              .map(entry -> new ConfigV3.Hostname()
                                  .setHostname(entry.getHostname())
                                  .setType(entry.getType()
                                      .name())
                                  .setIp(entry.getIpAsText())
                                  .setTarget(entry.getTarget())
                                  .setTtl(entry.getTtl())
                              )
                              .collect(toList())
                      )
                  )
                  .collect(toList())
          )
      );
    }

    if (config.getSolverStub() != null) {
      solver.setStub(new ConfigV3.Stub()
          .setDomainName(config.getSolverStub()
              .getDomainName())
      );
    }

    if (config.getSolverSystem() != null) {
      solver.setSystem(new ConfigV3.System()
          .setHostMachineHostname(config.getHostMachineHostname())
      );
    }

    return solver;
  }

  /* ================= CIRCUIT BREAKER ================= */

  private static CircuitBreakerStrategyConfig mapCircuitBreakerToDomain(
      CircuitBreakerStrategyConfig cb
  ) {
    if (cb == null) {
      return null;
    }

    return switch (cb.getType()) {
      case STATIC_THRESHOLD -> {
        final var st = (ConfigV3.StaticThreshold) cb;
        yield StaticThresholdCircuitBreakerStrategyConfig.builder()
            .failureThreshold(st.getFailureThreshold())
            .failureThresholdCapacity(st.getFailureThresholdCapacity())
            .successThreshold(st.getSuccessThreshold())
            .testDelay(st.getTestDelay())
            .build();
      }
      case CANARY_RATE_THRESHOLD -> {
        final var cr = (ConfigV3.CanaryRateThreshold) cb;
        yield CanaryRateThresholdCircuitBreakerStrategyConfig.builder()
            .failureRateThreshold(cr.getFailureRateThreshold())
            .minimumNumberOfCalls(cr.getMinimumNumberOfCalls())
            .permittedNumberOfCallsInHalfOpenState(cr.getPermittedNumberOfCallsInHalfOpenState())
            .build();
      }
      case NON_RESILIENT -> new NonResilientCircuitBreakerStrategyConfig();
    };
  }

  static CircuitBreakerStrategyConfig mapCircuitBreakerToV3(
      CircuitBreakerStrategyConfig strategy
  ) {
    if (strategy == null) {
      return null;
    }

    return switch (strategy.getType()) {
      case STATIC_THRESHOLD -> {
        final var st = (StaticThresholdCircuitBreakerStrategyConfig) strategy;
        yield new ConfigV3.StaticThreshold()
            .setFailureThreshold(st.getFailureThreshold())
            .setFailureThresholdCapacity(st.getFailureThresholdCapacity())
            .setSuccessThreshold(st.getSuccessThreshold())
            .setTestDelay(st.getTestDelay());
      }
      case CANARY_RATE_THRESHOLD -> {
        final var cr = (CanaryRateThresholdCircuitBreakerStrategyConfig) strategy;
        yield new ConfigV3.CanaryRateThreshold()
            .setFailureRateThreshold(cr.getFailureRateThreshold())
            .setMinimumNumberOfCalls(cr.getMinimumNumberOfCalls())
            .setPermittedNumberOfCallsInHalfOpenState(
                cr.getPermittedNumberOfCallsInHalfOpenState());
      }
      default -> null;
    };
  }
}
