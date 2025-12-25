package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import com.mageddo.commons.Collections;
import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Entry;
import com.mageddo.dnsproxyserver.config.Config.Log;
import com.mageddo.dnsproxyserver.config.Config.SolverDocker.DpsNetwork;
import com.mageddo.dnsproxyserver.config.NonResilientCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsproxyserver.utils.Booleans;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;

import org.apache.commons.lang3.EnumUtils;

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

  private static Config.Server mapServer(final ConfigV3.Server s) {
    if (s == null) {
      return null;
    }

    final var web = s.getWeb();
    return Config.Server.builder()
        .host(s.getHost())
        .dns(mapDnsServer(s))
        .doh(mapDomainDohServer(s))
        .webServerPort(web != null ? web.getPort() : null)
        .build();
  }

  private static Config.Server.DoH mapDomainDohServer(ConfigV3.Server s) {
    final var doh = s.getDoh();
    if (doh == null) {
      return null;
    }
    return Config.Server.DoH.builder()
        .port(doh.getPort())
        .build();
  }

  private static Config.Server.Dns mapDnsServer(ConfigV3.Server server) {
    final var dns = server.getDns();
    if (dns == null) {
      return null;
    }
    return Config.Server.Dns
        .builder()
        .protocol(EnumUtils.getEnum(SimpleServer.Protocol.class, dns.getProtocol()))
        .port(dns.getPort())
        .noEntriesResponseCode(dns.getNoEntriesResponseCode())
        .build();
  }

  private static ConfigV3.Server mapServerV3(final Config config) {
    final var server = config.getServer();
    if (server == null) {
      return null;
    }

    return new ConfigV3.Server()
        .setHost(server.getHost())
        .setDns(new ConfigV3.Dns()
            .setProtocol(Objects.toString(config.getServerProtocol(), null))
            .setPort(config.getDnsServerPort())
            .setNoEntriesResponseCode(config.getNoEntriesResponseCode())
        )
        .setDoh(mapDohServer(server))
        .setWeb(new ConfigV3.Web()
            .setPort(config.getWebServerPort())
        );
  }

  private static ConfigV3.DoH mapDohServer(Config.Server server) {
    final var doh = server.getDoh();
    if (doh == null) {
      return null;
    }
    return new ConfigV3.DoH()
        .setPort(doh.getPort());
  }

  /* ================= DEFAULT DNS ================= */

  private static Config.DefaultDns mapDefaultDns(final ConfigV3.DefaultDns d) {
    if (d == null) {
      return null;
    }

    final var resolvConf = d.getResolvConf();
    return Config.DefaultDns.builder()
        .active(d.getActive())
        .resolvConf(
            resolvConf == null
                ? null
                : Config.DefaultDns.ResolvConf.builder()
                .paths(resolvConf.getPaths())
                .overrideNameServers(resolvConf.getOverrideNameServers())
                .build()
        )
        .build();
  }

  private static ConfigV3.DefaultDns mapDefaultDnsV3(final Config.DefaultDns d) {
    if (d == null) {
      return null;
    }

    final var resolvConf = d.getResolvConf();
    return new ConfigV3.DefaultDns()
        .setActive(d.getActive())
        .setResolvConf(
            resolvConf == null
                ? null
                : new ConfigV3.ResolvConf()
                .setPaths(resolvConf.getPaths())
                .setOverrideNameServers(resolvConf.getOverrideNameServers())
        );
  }

  /* ================= LOG ================= */

  private static Log mapLog(final ConfigV3.Log l) {
    if (l == null) {
      return null;
    }

    return Log.builder()
        .level(l.getLevel() != null ? Log.Level.valueOf(l.getLevel()) : null)
        .file(l.getFile())
        .build();
  }

  private static ConfigV3.Log mapLogV3(final Log l) {
    if (l == null) {
      return null;
    }

    final var level = l.getLevel();
    return new ConfigV3.Log()
        .setLevel(level != null ? level.name() : null)
        .setFile(l.getFile());
  }

  /* ================= SOLVERS ================= */

  private static Config.SolverRemote mapSolverRemote(final ConfigV3.Solver s) {
    if (s == null || s.getRemote() == null) {
      return null;
    }

    final var remote = s.getRemote();
    return Config.SolverRemote.builder()
        .active(remote.getActive())
        .dnsServers(
            remote.getDnsServers() == null
                ? emptyList()
                : Collections.map(remote.getDnsServers(), IpAddr::of)
        )
        .circuitBreaker(mapCircuitBreakerToDomain(remote.getCircuitBreaker()))
        .build();
  }

  private static Config.SolverDocker mapSolverDocker(final ConfigV3.Solver s) {
    if (s == null || s.getDocker() == null) {
      return null;
    }
    final var docker = s.getDocker();
    return Config.SolverDocker.builder()
        .registerContainerNames(docker.getRegisterContainerNames())
        .domain(docker.getDomain())
        .hostMachineFallback(docker.getHostMachineFallback())
        .dockerDaemonUri(docker.getDockerDaemonUri() != null
            ? URI.create(docker.getDockerDaemonUri())
            : null
        )
        .dpsNetwork(mapDomainDpsNetwork(s))
        .networks(mapNetworks(docker.getNetworks()))
        .build();
  }

  private static Config.SolverDocker.Networks mapNetworks(ConfigV3.Networks networks) {
    if (networks == null) {
      return null;
    }
    final var preferred = networks.getPreferred();
    return Config.SolverDocker.Networks.builder()
        .preferred(Config.SolverDocker.Networks.Preferred.builder()
            .names(preferred.getNames())
            .overrideDefault(Booleans.getOrDefault(preferred.getOverrideDefault(), false))
            .build()
        )
        .build();
  }

  private static DpsNetwork mapDomainDpsNetwork(ConfigV3.Solver s) {
    final var dpsNetwork = s.getDocker()
        .getDpsNetwork();
    if (dpsNetwork == null) {
      return null;
    }
    return DpsNetwork.builder()
        .name(dpsNetwork.getName())
        .autoCreate(dpsNetwork.getAutoCreate())
        .autoConnect(dpsNetwork.getAutoConnect())
        .configs(mapDomainDpsNetworkConfigs(dpsNetwork.getConfigs()))
        .build();
  }

  private static List<DpsNetwork.NetworkConfig> mapDomainDpsNetworkConfigs(
      List<ConfigV3.DpsNetwork.Config> configs
  ) {
    return Collections.map(configs, ConfigMapper::mapDomainDpsNetworkConfig);
  }

  static DpsNetwork.NetworkConfig mapDomainDpsNetworkConfig(ConfigV3.DpsNetwork.Config config) {
    return DpsNetwork.NetworkConfig.builder()
        .gateway(config.getGateway())
        .ipRange(config.getIpRange())
        .subNet(config.getSubNet())
        .build();
  }

  private static Config.SolverLocal mapSolverLocal(final ConfigV3.Solver s) {
    if (s == null || s.getLocal() == null) {
      return null;
    }
    final var local = s.getLocal();
    return Config.SolverLocal.builder()
        .activeEnv(local.getActiveEnv())
        .envs(local.getEnvs() == null
            ? emptyList()
            : Collections.map(local.getEnvs(), ConfigMapper::mapEnv)
        )
        .build();
  }

  private static Config.Env mapEnv(final ConfigV3.Env e) {
    return Config.Env.of(
        e.getName(),
        e.getHostnames() == null
            ? emptyList()
            : Collections.map(e.getHostnames(), ConfigMapper::mapEntry)
    );
  }

  private static Entry mapEntry(final ConfigV3.Hostname h) {
    return Entry.builder()
        .hostname(h.getHostname())
        .ttl(h.getTtl())
        .type(Entry.Type.valueOf(h.getType()))
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
          .setDnsServers(Collections.map(config.getRemoteDnsServers(), IpAddr::toString))
          .setCircuitBreaker(mapCircuitBreakerToV3(config.getSolverRemoteCircuitBreakerStrategy()))
      );
    }

    final var solverDocker = config.getSolverDocker();
    if (solverDocker != null) {
      final var dpsNetwork = config.getDockerSolverDpsNetwork();
      solver.setDocker(new ConfigV3.Docker()
          .setDomain(config.getDockerDomain())
          .setRegisterContainerNames(config.getRegisterContainerNames())
          .setHostMachineFallback(config.getDockerSolverHostMachineFallbackActive())
          .setDockerDaemonUri(Objects.toString(config.getDockerDaemonUri(), null))
          .setDpsNetwork(
              dpsNetwork == null
                  ? null
                  : new ConfigV3.DpsNetwork()
                  .setName(dpsNetwork.getName())
                  .setAutoCreate(dpsNetwork.getAutoCreate())
                  .setAutoConnect(dpsNetwork.getAutoConnect())
                  .setConfigs(
                      Collections.map(dpsNetwork.getConfigs(), ConfigMapper::mapDpsNetworkConfigV3)
                  )
          )
          .setNetworks(mapNetworksDf(solverDocker.getNetworks()))
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
                              : Collections.map(env.getEntries(), ConfigMapper::mapEntryV3)
                      )
                  )
                  .collect(toList())
          )
      );
    }

    if (config.getSolverStub() != null) {
      solver.setStub(new ConfigV3.Stub()
          .setDomainName(config.getSolverStub()
              .getDomainName()
          )
      );
    }

    if (config.getSolverSystem() != null) {
      solver.setSystem(new ConfigV3.System()
          .setHostMachineHostname(config.getHostMachineHostname())
      );
    }

    return solver;
  }

  private static ConfigV3.Networks mapNetworksDf(Config.SolverDocker.Networks networks) {
    if (networks == null) {
      return null;
    }
    final var preferred = networks.getPreferred();
    return new ConfigV3.Networks()
        .setPreferred(new ConfigV3.Networks.Preferred()
            .setNames(preferred.getNames())
            .setOverrideDefault(preferred.isOverrideDefault())
        );
  }

  private static ConfigV3.Hostname mapEntryV3(Entry entry) {
    return new ConfigV3.Hostname()
        .setHostname(entry.getHostname())
        .setType(entry.getType()
            .name())
        .setIp(entry.getIpAsText())
        .setTarget(entry.getTarget())
        .setTtl(entry.getTtl());
  }

  static ConfigV3.DpsNetwork.Config mapDpsNetworkConfigV3(DpsNetwork.NetworkConfig config) {
    return new ConfigV3.DpsNetwork.Config()
        .setSubNet(config.getSubNet())
        .setIpRange(config.getIpRange())
        .setGateway(config.getGateway());
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
