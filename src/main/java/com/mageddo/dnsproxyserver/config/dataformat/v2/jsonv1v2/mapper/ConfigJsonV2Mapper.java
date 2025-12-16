package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.mapper;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Log;
import com.mageddo.dnsproxyserver.config.Server;
import com.mageddo.dnsproxyserver.config.SolverDocker;
import com.mageddo.dnsproxyserver.config.SolverLocal;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.mapper.LogLevelMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJson;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJsonV2;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJsonV2.CanaryRateThresholdCircuitBreaker;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJsonV2.StaticThresholdCircuitBreaker;
import com.mageddo.dnsproxyserver.utils.Booleans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.nio.file.Path;

@Slf4j
public class ConfigJsonV2Mapper {

  public static Config toConfig(ConfigJson json, Path configFileAbsolutePath) {
    return Config.builder()
      .server(Server
        .builder()
        .dnsServerNoEntriesResponseCode(json.getNoEntriesResponseCode())
        .webServerPort(json.getWebServerPort())
        .dnsServerPort(json.getDnsServerPort())
        .serverProtocol(json.getServerProtocol())
        .build()
      )
      .defaultDns(Config.DefaultDns
        .builder()
        .active(json.getDefaultDns())
        .resolvConf(Config.DefaultDns.ResolvConf
          .builder()
          .overrideNameServers(json.getResolvConfOverrideNameServers())
          .build()
        )
        .build()
      )
      .log(Log
        .builder()
        .level(LogLevelMapper.mapLogLevelFrom(json.getLogLevel()))
        .file(LogLevelMapper.mapLogFileFrom(json.getLogFile()))
        .build()
      )
      .configPath(configFileAbsolutePath)
      .solverRemote(toSolverRemote(json))
      .solverStub(toSolverStub(json.getSolverStub()))
      .solverLocal(SolverLocal
        .builder()
        .activeEnv(json.getActiveEnv())
        .envs(json.getEnvs())
        .build()
      )
      .solverDocker(SolverDocker
        .builder()
        .dpsNetwork(SolverDocker.DpsNetwork
          .builder()
          .autoCreate(json.getDpsNetwork())
          .autoConnect(json.getDpsNetworkAutoConnect())
          .build()
        )
        .hostMachineFallback(json.getDockerSolverHostMachineFallbackActive())
        .dockerDaemonUri(json.getDockerHost())
        .registerContainerNames(json.getRegisterContainerNames())
        .domain(json.getDomain())
        .build()
      )
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
    return ObjectUtils.allNull(
      json.getNoRemoteServers(),
      json.getSolverRemote(),
      json.getSolverRemoteCircuitBreaker(),
      json.hasRemoteDnsServers() ? json.getRemoteDnsServers() : null
    );
  }

  static boolean isPossibleToBuildComplete(ConfigJson json) {
    return ObjectUtils.allNotNull(json.getSolverRemote(), json.getSolverRemoteCircuitBreaker());
  }

  static SolverRemote buildSimpleSolverRemote(ConfigJson json) {
    return SolverRemote
      .builder()
      .active(Booleans.reverseWhenNotNull(json.getNoRemoteServers()))
      .dnsServers(json.getRemoteDnsServers())
      .build();
  }

  static SolverRemote buildCompleteSolverRemote(ConfigJson json, ConfigJsonV2.CircuitBreaker circuitBreaker) {
    return SolverRemote
      .builder()
      .active(Booleans.reverseWhenNotNull(json.getNoRemoteServers()))
      .circuitBreaker(mapCircuitBreaker(circuitBreaker))
      .dnsServers(json.getRemoteDnsServers())
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
