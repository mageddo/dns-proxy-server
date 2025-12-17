package com.mageddo.dnsproxyserver.config.mapper;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.DefaultDns;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.validator.ConfigValidator;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.dnsproxyserver.version.VersionDAO;
import com.mageddo.dnsserver.SimpleServer;
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

  public static Config add(Config config, Config.Env def) {
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

    final var store = keyBy(config.getEnvs(), Config.Env::getName);
    store.computeIfPresent(envKey, (key, env) -> replaceEntry(env, entry));
    store.computeIfAbsent(envKey, __ -> Config.Env.of(envKey, List.of(entry)));

    return config.toBuilder()
        .solverLocal(config.getSolverLocal()
            .toBuilder()
            .envs(new ArrayList<>(store.values()))
            .build())
        .build();
  }

  private static Config.Env replaceEntry(Config.Env env, Config.Entry entry) {
    return env.toBuilder()
        .entries(replaceEntry(env.getEntries(), entry))
        .build();
  }

  private static List<Config.Entry> replaceEntry(
      List<Config.Entry> entries, Config.Entry entry
  ) {
    final var store = keyBy(entries, Config.Entry::getId);
    store.put(entry.getId(), entry);
    return new ArrayList<>(store.values());
  }

  public static Config add(Config config, String env) {
    return add(config, Config.Env.of(env, Collections.emptyList()));
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

  static List<Config.Env> removeHostName(
      Config config, String envKey, String hostname
  ) {
    final var envsStore = keyBy(config.getEnvs(), Config.Env::getName);
    if (!envsStore.containsKey(envKey)) {
      return null;
    }
    final var env = envsStore.get(envKey);
    final var entryStore = keyBy(env.getEntries(), Config.Entry::getHostname);
    if (!entryStore.containsKey(hostname)) {
      return null;
    }
    entryStore.remove(hostname);
    final var updatedEnv = env.toBuilder()
        .entries(new ArrayList<>(entryStore.values()))
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
        .server(Config.Server
            .builder()
            .webServerPort(Numbers.firstPositive(mapField(Config::getWebServerPort, configs)))
            .dnsServerPort(Numbers.firstPositive(mapField(Config::getDnsServerPort, configs)))
            .serverProtocol(firstNonNullRequiring(mapField(Config::getServerProtocol, configs)))
            .dnsServerNoEntriesResponseCode(
                firstNonNullRequiring(mapField(Config::getNoEntriesResponseCode, configs))
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
                    firstNonNullRequiring(mapField(Config::getDefaultDnsResolvConfPaths, configs)))
                .overrideNameServers(firstNonNullRequiring(
                    mapField(Config::isResolvConfOverrideNameServersActive, configs)))
                .build())
            .build()
        )
        .solverRemote(Config.SolverRemote
            .builder()
            .active(firstNonNullRequiring(mapField(Config::isSolverRemoteActive, configs)))
            .circuitBreaker(firstNonNullRequiring(
                mapField(Config::getSolverRemoteCircuitBreakerStrategy, configs)
            ))
            .dnsServers(firstNonEmptyListRequiring(mapField(Config::getRemoteDnsServers, configs)))
            .build()
        )
        .solverStub(Config.SolverStub
            .builder()
            .domainName(firstNonNullRequiring(mapField(Config::getSolverStubDomainName, configs)))
            .build()
        )
        .solverDocker(Config.SolverDocker
            .builder()
            .dockerDaemonUri(firstNonNullRequiring(mapField(Config::getDockerDaemonUri, configs)))
            .registerContainerNames(
                firstNonNullRequiring(mapField(Config::getRegisterContainerNames, configs)))
            .domain(firstNonNullRequiring(mapField(Config::getDockerDomain, configs)))
            .hostMachineFallback(firstNonNullRequiring(
                mapField(Config::getDockerSolverHostMachineFallbackActive, configs)))
            .dpsNetwork(firstNonNullRequiring(mapField(Config::getDockerSolverDpsNetwork, configs)))
            .build()
        )
        .solverSystem(Config.SolverSystem
            .builder()
            .hostMachineHostname(
                firstNonNullRequiring(mapField(Config::getHostMachineHostname, configs)))
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

  private static Config buildDefault() {
    return Config
        .builder()
        .server(Config.Server
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
        .solverDocker(Config.SolverDocker
            .builder()
            .dockerDaemonUri(buildDefaultDockerHost())
            .build()
        )
        .source(Config.Source.DEFAULT)
        .build();
  }

  public static StaticThresholdCircuitBreakerStrategyConfig defaultCircuitBreaker() {
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
