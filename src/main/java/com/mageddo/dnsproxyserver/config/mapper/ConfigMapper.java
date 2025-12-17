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
import com.mageddo.dnsproxyserver.version.VersionDAO;
import com.mageddo.dnsproxyserver.config.validator.ConfigValidator;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IpAddr;

import org.apache.commons.lang3.SystemUtils;

import lombok.RequiredArgsConstructor;

import static com.mageddo.dnsproxyserver.utils.ListOfObjectUtils.mapField;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonEmptyListRequiring;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNull;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNullRequiring;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigMapper {

  public static final String RESOLV_CONF_DEFAULT_PATHS = "/host/etc/systemd/resolved.conf,"
      + "/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf";
  private final VersionDAO versionDAO;

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
                firstNonNullRequiring(mapField(Config::getNoEntriesResponseCode, configs)))
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
                mapField(Config::getSolverRemoteCircuitBreakerStrategy, configs)))
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
            .envs(firstNonNull(mapField(Config::getEnvs, configs)))
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
