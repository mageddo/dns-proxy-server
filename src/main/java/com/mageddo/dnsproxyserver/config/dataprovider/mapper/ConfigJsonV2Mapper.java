package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2.CanaryRateThresholdCircuitBreaker;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2.StaticThresholdCircuitBreaker;
import com.mageddo.dnsproxyserver.utils.Booleans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.nio.file.Path;

@Slf4j
public class ConfigJsonV2Mapper {

  public static Config toConfig(ConfigJson json, Path configFileAbsolutePath) {
    return Config.builder()
      .webServerPort(json.getWebServerPort())
      .dnsServerPort(json.getDnsServerPort())
      .defaultDns(json.getDefaultDns())
      .logLevel(ConfigFieldsValuesMapper.mapLogLevelFrom(json.getLogLevel()))
      .logFile(ConfigFieldsValuesMapper.mapLogFileFrom(json.getLogFile()))
      .registerContainerNames(json.getRegisterContainerNames())
      .hostMachineHostname(json.getHostMachineHostname())
      .domain(json.getDomain())
      .mustConfigureDpsNetwork(json.getDpsNetwork())
      .dpsNetworkAutoConnect(json.getDpsNetworkAutoConnect())
      .remoteDnsServers(json.getRemoteDnsServers())
      .serverProtocol(json.getServerProtocol())
      .dockerHost(json.getDockerHost())
      .resolvConfOverrideNameServers(json.getResolvConfOverrideNameServers())
      .noEntriesResponseCode(json.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(json.getDockerSolverHostMachineFallbackActive())
      .configPath(configFileAbsolutePath)
      .solverRemote(toSolverRemote(json))
      .solverStub(toSolverStub(json.getSolverStub()))
      .source(Config.Source.JSON)
      .build();
  }

  private static SolverStub toSolverStub(ConfigJsonV2.SolverStub solverStub) {
    if (solverStub == null) {
      return null;
    }
    return SolverStub
      .builder()
      .domainName(solverStub.getDomainName())
      .build();
  }

  static SolverRemote toSolverRemote(ConfigJson json) {
    if (nothingIsSet(json)) {
      return null;
    } else if (isPossibleToBuildComplete(json)) {
      return buildCompleteSolverRemote(json, json.getSolverRemoteCircuitBreaker());
    }
    return buildSimpleSolverRemote(json);
  }

  static boolean nothingIsSet(ConfigJson json) {
    return ObjectUtils.allNull(json.getNoRemoteServers(), json.getSolverRemote(), json.getSolverRemoteCircuitBreaker());
  }

  static boolean isPossibleToBuildComplete(ConfigJson json) {
    return ObjectUtils.allNotNull(json.getSolverRemote(), json.getSolverRemoteCircuitBreaker());
  }

  static SolverRemote buildSimpleSolverRemote(ConfigJson json) {
    return SolverRemote
      .builder()
      .active(Booleans.reverseWhenNotNull(json.getNoRemoteServers()))
      .build();
  }

  static SolverRemote buildCompleteSolverRemote(ConfigJson json, ConfigJsonV2.CircuitBreaker circuitBreaker) {
    return SolverRemote
      .builder()
      .active(Booleans.reverseWhenNotNull(json.getNoRemoteServers()))
      .circuitBreaker(mapCircuitBreaker(circuitBreaker))
      .build();
  }

  private static CircuitBreakerStrategyConfig mapCircuitBreaker(ConfigJsonV2.CircuitBreaker circuitBreaker) {
    log.debug("circuitBreakerConfigStrategy={}", circuitBreaker.strategy());
    return switch (circuitBreaker.strategy()) {
      case STATIC_THRESHOLD -> mapFromStaticCircuitBreaker((StaticThresholdCircuitBreaker) circuitBreaker);
      case CANARY_RATE_THRESHOLD ->
        mapFromCanaryRateThresholdCircuitBreaker((CanaryRateThresholdCircuitBreaker) circuitBreaker);
      default -> throw new UnsupportedOperationException("Unrecognized circuit breaker: " + circuitBreaker.strategy());
    };
  }

  private static CircuitBreakerStrategyConfig mapFromCanaryRateThresholdCircuitBreaker(
    CanaryRateThresholdCircuitBreaker circuitBreaker
  ) {
    return CanaryRateThresholdCircuitBreakerStrategyConfig.builder()
      .failureRateThreshold(circuitBreaker.getFailureRateThreshold())
      .minimumNumberOfCalls(circuitBreaker.getMinimumNumberOfCalls())
      .permittedNumberOfCallsInHalfOpenState(circuitBreaker.getPermittedNumberOfCallsInHalfOpenState())
      .build();
  }

  private static CircuitBreakerStrategyConfig mapFromStaticCircuitBreaker(StaticThresholdCircuitBreaker circuitBreaker) {
    return StaticThresholdCircuitBreakerStrategyConfig
      .builder()
      .failureThreshold(circuitBreaker.getFailureThreshold())
      .failureThresholdCapacity(circuitBreaker.getFailureThresholdCapacity())
      .successThreshold(circuitBreaker.getSuccessThreshold())
      .testDelay(circuitBreaker.getTestDelay())
      .build();
  }
}
