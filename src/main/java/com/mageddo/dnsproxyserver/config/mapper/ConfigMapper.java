package com.mageddo.dnsproxyserver.config.mapper;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.lang.ValueResolver;
import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.DefaultDns;
import com.mageddo.dnsproxyserver.config.Config.Env;
import com.mageddo.dnsproxyserver.config.Config.Server;
import com.mageddo.dnsproxyserver.config.Config.SolverDocker;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.validator.ConfigValidator;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.dnsproxyserver.version.VersionDAO;
import com.mageddo.dnsserver.SimpleServer.Protocol;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;

import org.apache.commons.lang3.SystemUtils;

import lombok.RequiredArgsConstructor;

import static com.mageddo.commons.Collections.keyBy;
import static com.mageddo.dnsproxyserver.utils.ListOfObjectUtils.mapField;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonEmptyList;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonEmptyListRequiring;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNull;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNullRequiring;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigMapper {

  public static final String RESOLV_CONF_DEFAULT_PATHS = "/host/etc/systemd/resolved.conf,"
      + "/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf";
  private final VersionDAO versionDAO;

  public static Config add(Config config, Env def) {
    final var envs = new ArrayList<>(config.getEnvs());
    envs.add(def);
    return config.toBuilder()
        .solverLocal(config.getSolverLocal()
            .toBuilder()
            .envs(envs)
            .build())
        .build();
  }

  public static Config replace(Config config, String envKey, Config.Entry entry) {

    final var store = keyBy(config.getEnvs(), Env::getName);
    store.computeIfPresent(envKey, (key, env) -> replaceEntry(env, entry));
    store.computeIfAbsent(envKey, __ -> Env.of(envKey, List.of(entry)));

    return config.toBuilder()
        .solverLocal(config.getSolverLocal()
            .toBuilder()
            .envs(new ArrayList<>(store.values()))
            .build())
        .build();
  }

  private static Env replaceEntry(Env env, Config.Entry entry) {
    return env.toBuilder()
        .entries(replaceEntry(env.getEntries(), entry))
        .build();
  }

  static List<Config.Entry> replaceEntry(
      List<Config.Entry> entries, Config.Entry entry
  ) {
    final var store = keyBy(entries, Config.Entry::getHostname);
    store.put(entry.getHostname(), entry);
    return new ArrayList<>(store.values());
  }

  public static Config add(Config config, String env) {
    return add(config, Env.of(env, Collections.emptyList()));
  }

  public static Config remove(Config config, String envKey, String hostname) {

    final var envs = removeHostName(config, envKey, hostname);
    if (envs == null) {
      return null;
    }
    return config.toBuilder()
        .solverLocal(config.getSolverLocal()
            .toBuilder()
            .envs(envs)
            .build()
        )
        .build();
  }

  static List<Env> removeHostName(
      Config config, String envKey, String hostname
  ) {
    final var envsStore = keyBy(config.getEnvs(), Env::getName);
    if (!envsStore.containsKey(envKey)) {
      return null;
    }
    final var env = envsStore.get(envKey);
    final var entryStore = env.getEntries()
        .stream()
        .collect(Collectors.groupingBy(
            Config.Entry::getHostname,
            Collectors.reducing((a, b) -> a)
        ));

    if (!entryStore.containsKey(hostname)) {
      return null;
    }
    entryStore.remove(hostname);
    final var updatedEnv = env.toBuilder()
        .entries(entryStore.values()
            .stream()
            .map(it -> it.orElse(null))
            .toList()
        )
        .build();

    envsStore.put(envKey, updatedEnv);

    return new ArrayList<>(envsStore.values());
  }

  public Config mapFrom(List<Config> configs) {
    final var configsWithDefault = new ArrayList<>(configs);
    configsWithDefault.add(buildDefault());
    return mapFrom0(configsWithDefault);
  }

  private Config mapFrom0(List<Config> configs) {

    final var config = Config.builder()
        .server(Server
            .builder()
            .host(ValueResolver.findFirstOrThrow(
                configs,
                Config::getServer,
                Server::getHost
            ))
            .webServerPort(Numbers.firstPositive(mapField(Config::getWebServerPort, configs)))
            .dns(Server.Dns.builder()
                .protocol(ValueResolver.findFirstOrThrow(
                    configs,
                    Config::getDnsServer,
                    Server.Dns::getProtocol
                ))
                .port(ValueResolver.findFirstOrThrow(
                    configs,
                    Config::getDnsServer,
                    Server.Dns::getPort
                ))
                .noEntriesResponseCode(ValueResolver.findFirstOrThrow(
                    configs,
                    Config::getDnsServer,
                    Server.Dns::getNoEntriesResponseCode
                ))
                .build()
            )
            .build()
        )
        .version(this.versionDAO.findVersion())
        .log(Config.Log
            .builder()
            .level(firstNonNullRequiring(mapField(Config::getLogLevel, configs)))
            .file(firstNonNullRequiring(mapField(Config::getLogFile, configs)))
            .build()
        )
        .defaultDns(DefaultDns
            .builder()
            .active(firstNonNullRequiring(mapField(Config::isDefaultDnsActive, configs)))
            .resolvConf(DefaultDns.ResolvConf
                .builder()
                .paths(
                    firstNonNullRequiring(mapField(Config::getDefaultDnsResolvConfPaths, configs))
                )
                .overrideNameServers(firstNonNullRequiring(
                    mapField(Config::isResolvConfOverrideNameServersActive, configs))
                )
                .build()
            )
            .build()
        )
        .solverRemote(Config.SolverRemote
            .builder()
            .active(firstNonNullRequiring(mapField(Config::isSolverRemoteActive, configs)))
            .circuitBreaker(firstNonNullRequiring(
                mapField(Config::getSolverRemoteCircuitBreakerStrategy, configs)
            ))
            .dnsServers(firstNonEmptyListRequiring(mapField(
                Config::getRemoteDnsServers, configs)))
            .build()
        )
        .solverStub(Config.SolverStub
            .builder()
            .domainName(firstNonNullRequiring(mapField(Config::getSolverStubDomainName, configs)))
            .build()
        )
        .solverDocker(SolverDocker
            .builder()
            .dockerDaemonUri(firstNonNullRequiring(mapField(Config::getDockerDaemonUri, configs)))
            .registerContainerNames(
                firstNonNullRequiring(mapField(Config::getRegisterContainerNames, configs))
            )
            .domain(firstNonNullRequiring(mapField(Config::getDockerDomain, configs)))
            .hostMachineFallback(firstNonNullRequiring(
                mapField(Config::getDockerSolverHostMachineFallbackActive, configs)
            ))
            .dpsNetwork(
                SolverDocker.DpsNetwork.builder()
                    .name(ValueResolver.findFirstOrThrow(
                        configs,
                        Config::getDockerSolverDpsNetwork,
                        SolverDocker.DpsNetwork::getName
                    ))
                    .autoCreate(ValueResolver.findFirstOrThrow(
                        configs,
                        Config::getDockerSolverDpsNetwork,
                        SolverDocker.DpsNetwork::getAutoCreate
                    ))
                    .autoConnect(ValueResolver.findFirstOrThrow(
                        configs,
                        Config::getDockerSolverDpsNetwork,
                        SolverDocker.DpsNetwork::getAutoConnect
                    ))
                    .configs(ValueResolver.findFirstOrThrow(
                        configs,
                        Config::getDockerSolverDpsNetwork,
                        SolverDocker.DpsNetwork::getConfigs
                    ))
                    .build()
            )
            .networks(SolverDocker.Networks.builder()
                .preferred(ValueResolver.findFirstOrThrow(
                    configs,
                    Config::getDockerSolverNetworks,
                    SolverDocker.Networks::getPreferred
                ))
                .build()
            )
            .build()
        )
        .solverSystem(Config.SolverSystem
            .builder()
            .hostMachineHostname(
                firstNonNullRequiring(mapField(Config::getHostMachineHostname, configs))
            )
            .build()
        )
        .solverLocal(Config.SolverLocal
            .builder()
            .activeEnv(firstNonNull(mapField(Config::getActiveEnv, configs)))
            .envs(firstNonEmptyList(mapField(Config::getEnvs, configs)))
            .build()
        )
        .source(Config.Source.MERGED)
        .build();
    ConfigValidator.validate(config);
    return config;
  }

  static Config buildDefault() {
    return Config
        .builder()
        .server(Server.builder()
            .host("0.0.0.0")
            .dns(Server.Dns.builder()
                .protocol(Protocol.UDP_TCP)
                .port(53)
                .noEntriesResponseCode(3)
                .build()
            )
            .webServerPort(5380)
            .build()
        )
        .defaultDns(DefaultDns.builder()
            .active(true)
            .resolvConf(DefaultDns.ResolvConf.builder()
                .paths(RESOLV_CONF_DEFAULT_PATHS)
                .overrideNameServers(true)
                .build()
            )
            .build()
        )
        .solverRemote(Config.SolverRemote
            .builder()
            .active(true)
            .circuitBreaker(defaultCircuitBreaker())
            .dnsServers(Collections.singletonList(IpAddr.of("8.8.8.8:53")))
            .build()
        )
        .solverStub(Config.SolverStub.builder()
            .domainName("stub")
            .build()
        )
        .solverDocker(SolverDocker
            .builder()
            .dockerDaemonUri(buildDefaultDockerHost())
            .dpsNetwork(SolverDocker.DpsNetwork.builder()
                .autoConnect(false)
                .autoCreate(false)
                .name(Network.Name.DPS.lowerCaseName())
                .configs(List.of(
                    SolverDocker.DpsNetwork.NetworkConfig.builder()
                        .subNet("172.157.0.0/16")
                        .ipRange("172.157.5.0/24")
                        .gateway("172.157.5.1")
                        .build(),
                    SolverDocker.DpsNetwork.NetworkConfig.builder()
                        .subNet("fc00:5c6f:db50::/64")
                        .gateway("fc00:5c6f:db50::1")
                        .build()
                ))
                .build()
            )
            .networks(SolverDocker.Networks.builder()
                .preferred(SolverDocker.Networks.Preferred.builder()
                    .overrideDefault(false)
                    .build()
                )
                .build()
            )
            .build()
        )
        .solverLocal(Config.SolverLocal.builder()
            .activeEnv(Env.DEFAULT_ENV)
            .envs(List.of(defaultEnv()))
            .build()
        )
        .source(Config.Source.DEFAULT)
        .build();
  }

  public static CircuitBreakerStrategyConfig defaultCircuitBreaker() {
    return CanaryRateThresholdCircuitBreakerStrategyConfig.builder()
        .failureRateThreshold(21)
        .minimumNumberOfCalls(50)
        .permittedNumberOfCallsInHalfOpenState(10)
        .build();
  }

  static Env defaultEnv() {
    return Env.of(Env.DEFAULT_ENV, List.of(aSampleEntry()));
  }

  static Config.Entry aSampleEntry() {
    return Config.Entry
        .builder()
        .type(Config.Entry.Type.A)
        .hostname("dps-sample.dev")
        .ip(IP.of("192.168.0.254"))
        .ttl(30)
        .id(1L)
        .build();
  }

  public static StaticThresholdCircuitBreakerStrategyConfig staticThresholdCircuitBreakerConfig() {
    return StaticThresholdCircuitBreakerStrategyConfig
        .builder()
        .failureThreshold(3)
        .failureThresholdCapacity(10)
        .successThreshold(5)
        .testDelay(Duration.ofSeconds(20))
        .build();
  }

  private static URI buildDefaultDockerHost() {
    if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
      return URI.create("unix:///var/run/docker.sock");
    }
    if (SystemUtils.IS_OS_WINDOWS) {
      return URI.create("npipe:////./pipe/docker_engine");
    }
    return null; // todo unsupported OS
  }
}
