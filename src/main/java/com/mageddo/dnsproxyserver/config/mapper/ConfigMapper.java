package com.mageddo.dnsproxyserver.config.mapper;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.DefaultDns;
import com.mageddo.dnsproxyserver.config.Config.Env;
import com.mageddo.dnsproxyserver.config.Config.SolverDocker;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.validator.ConfigValidator;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.dnsproxyserver.version.VersionDAO;
import com.mageddo.dnsserver.SimpleServer;
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
    final var store = keyBy(entries, com.mageddo.dnsproxyserver.config.Config.Entry::getHostname);
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
            com.mageddo.dnsproxyserver.config.Config.Entry::getHostname,
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
    final var config = com.mageddo.dnsproxyserver.config.Config.builder()
        .server(com.mageddo.dnsproxyserver.config.Config.Server
            .builder()
            .webServerPort(Numbers.firstPositive(mapField(com.mageddo.dnsproxyserver.config.Config::getWebServerPort, configs)))
            .dnsServerPort(Numbers.firstPositive(mapField(com.mageddo.dnsproxyserver.config.Config::getDnsServerPort, configs)))
            .serverProtocol(firstNonNullRequiring(mapField(
                com.mageddo.dnsproxyserver.config.Config::getServerProtocol, configs)))
            .dnsServerNoEntriesResponseCode(
                firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getNoEntriesResponseCode, configs))
            )
            .build()
        )
        .version(this.versionDAO.findVersion())
        .log(com.mageddo.dnsproxyserver.config.Config.Log
            .builder()
            .level(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getLogLevel, configs)))
            .file(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getLogFile, configs)))
            .build()
        )
        .defaultDns(DefaultDns
            .builder()
            .active(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::isDefaultDnsActive, configs)))
            .resolvConf(DefaultDns.ResolvConf
                .builder()
                .paths(
                    firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getDefaultDnsResolvConfPaths, configs)))
                .overrideNameServers(firstNonNullRequiring(
                    mapField(com.mageddo.dnsproxyserver.config.Config::isResolvConfOverrideNameServersActive, configs)))
                .build())
            .build()
        )
        .solverRemote(com.mageddo.dnsproxyserver.config.Config.SolverRemote
            .builder()
            .active(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::isSolverRemoteActive, configs)))
            .circuitBreaker(firstNonNullRequiring(
                mapField(com.mageddo.dnsproxyserver.config.Config::getSolverRemoteCircuitBreakerStrategy, configs)
            ))
            .dnsServers(firstNonEmptyListRequiring(mapField(
                com.mageddo.dnsproxyserver.config.Config::getRemoteDnsServers, configs)))
            .build()
        )
        .solverStub(com.mageddo.dnsproxyserver.config.Config.SolverStub
            .builder()
            .domainName(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getSolverStubDomainName, configs)))
            .build()
        )
        .solverDocker(SolverDocker
            .builder()
            .dockerDaemonUri(firstNonNullRequiring(mapField(
                com.mageddo.dnsproxyserver.config.Config::getDockerDaemonUri, configs)))
            .registerContainerNames(
                firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getRegisterContainerNames, configs)))
            .domain(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getDockerDomain, configs)))
            .hostMachineFallback(firstNonNullRequiring(
                mapField(com.mageddo.dnsproxyserver.config.Config::getDockerSolverHostMachineFallbackActive, configs)))
            .dpsNetwork(firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getDockerSolverDpsNetwork, configs)))
            .build()
        )
        .solverSystem(com.mageddo.dnsproxyserver.config.Config.SolverSystem
            .builder()
            .hostMachineHostname(
                firstNonNullRequiring(mapField(com.mageddo.dnsproxyserver.config.Config::getHostMachineHostname, configs)))
            .build()
        )
        .solverLocal(com.mageddo.dnsproxyserver.config.Config.SolverLocal
            .builder()
            .activeEnv(firstNonNull(mapField(com.mageddo.dnsproxyserver.config.Config::getActiveEnv, configs)))
            .envs(firstNonEmptyList(mapField(com.mageddo.dnsproxyserver.config.Config::getEnvs, configs)))
            .build()
        )
        .source(com.mageddo.dnsproxyserver.config.Config.Source.MERGED)
        .build();
    ConfigValidator.validate(config);
    return config;
  }

  static Config buildDefault() {
    return com.mageddo.dnsproxyserver.config.Config
        .builder()
        .server(com.mageddo.dnsproxyserver.config.Config.Server
            .builder()
            .serverProtocol(SimpleServer.Protocol.UDP_TCP)
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
        .solverRemote(com.mageddo.dnsproxyserver.config.Config.SolverRemote
            .builder()
            .active(true)
            .circuitBreaker(defaultCircuitBreaker())
            .dnsServers(Collections.singletonList(IpAddr.of("8.8.8.8:53")))
            .build()
        )
        .solverStub(com.mageddo.dnsproxyserver.config.Config.SolverStub.builder()
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
                        .ipRange("172.157.5.3/24")
                        .gateway("172.157.5.1")
                        .build(),
                    SolverDocker.DpsNetwork.NetworkConfig.builder()
                        .subNet("fc00:5c6f:db50::/64")
                        .gateway("fc00:5c6f:db50::1")
                        .build()
                ))
                .build()
            )
            .build()
        )
        .solverLocal(com.mageddo.dnsproxyserver.config.Config.SolverLocal.builder()
            .activeEnv(Env.DEFAULT_ENV)
            .envs(List.of(defaultEnv()))
            .build()
        )
        .source(com.mageddo.dnsproxyserver.config.Config.Source.DEFAULT)
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
    return com.mageddo.dnsproxyserver.config.Config.Entry
        .builder()
        .type(com.mageddo.dnsproxyserver.config.Config.Entry.Type.A)
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
