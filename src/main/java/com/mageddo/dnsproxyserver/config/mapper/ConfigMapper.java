package com.mageddo.dnsproxyserver.config.mapper;

import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategy;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigPropDAO;
import com.mageddo.dnsproxyserver.config.validator.ConfigValidator;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IpAddr;
import org.apache.commons.lang3.SystemUtils;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mageddo.dnsproxyserver.utils.ListOfObjectUtils.mapField;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonEmptyListRequiring;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNullRequiring;

public class ConfigMapper {
  public static Config mapFrom(List<Config> configs) {
    final var configsWithDefault = new ArrayList<>(configs);
    configsWithDefault.add(buildDefault());
    return mapFrom0(configsWithDefault);
  }

  private static Config mapFrom0(List<Config> configs) {
    final var config = Config.builder()
      .version(ConfigPropDAO.getVersion())
      .webServerPort(Numbers.firstPositive(mapField(Config::getWebServerPort, configs)))
      .dnsServerPort(Numbers.firstPositive(mapField(Config::getDnsServerPort, configs)))
      .defaultDns(firstNonNullRequiring(mapField(Config::getDefaultDns, configs)))
      .logLevel(firstNonNullRequiring(mapField(Config::getLogLevel, configs)))
      .logFile(firstNonNullRequiring(mapField(Config::getLogFile, configs)))
      .registerContainerNames(firstNonNullRequiring(mapField(Config::getRegisterContainerNames, configs)))
      .hostMachineHostname(firstNonNullRequiring(mapField(Config::getHostMachineHostname, configs)))
      .domain(firstNonNullRequiring(mapField(Config::getDomain, configs)))
      .mustConfigureDpsNetwork(firstNonNullRequiring(mapField(Config::getMustConfigureDpsNetwork, configs)))
      .dpsNetworkAutoConnect(firstNonNullRequiring(mapField(Config::getDpsNetworkAutoConnect, configs)))
      .remoteDnsServers(firstNonEmptyListRequiring(mapField(Config::getRemoteDnsServers, configs)))
      .configPath(firstNonNullRequiring(mapField(Config::getConfigPath, configs)))
      .resolvConfPaths(firstNonNullRequiring(mapField(Config::getResolvConfPaths, configs)))
      .serverProtocol(firstNonNullRequiring(mapField(Config::getServerProtocol, configs)))
      .dockerHost(firstNonNullRequiring(mapField(Config::getDockerHost, configs)))
      .resolvConfOverrideNameServers(firstNonNullRequiring(mapField(Config::getResolvConfOverrideNameServers, configs)))
      .noEntriesResponseCode(firstNonNullRequiring(mapField(Config::getNoEntriesResponseCode, configs)))
      .dockerSolverHostMachineFallbackActive(firstNonNullRequiring(mapField(Config::getDockerSolverHostMachineFallbackActive, configs)))
      .solverRemote(SolverRemote
        .builder()
        .active(firstNonNullRequiring(mapField(Config::isSolverRemoteActive, configs)))
        .circuitBreaker(firstNonNullRequiring(mapField(Config::getSolverRemoteCircuitBreaker, configs)))
        .build()
      )
      .build();
    ConfigValidator.validate(config);
    return config;
  }

  private static Config buildDefault() {
    return Config
      .builder()
      .serverProtocol(SimpleServer.Protocol.UDP_TCP)
      .dockerHost(buildDefaultDockerHost())
      .remoteDnsServers(Collections.singletonList(IpAddr.of("8.8.8.8:53")))
      .solverRemote(SolverRemote
        .builder()
        .active(true)
        .circuitBreaker(defaultCircuitBreaker())
        .build()
      )
      .build();
  }

  public static StaticThresholdCircuitBreakerStrategy defaultCircuitBreaker() {
    return StaticThresholdCircuitBreakerStrategy
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
