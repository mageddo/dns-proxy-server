package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2;
import com.mageddo.dnsproxyserver.utils.Booleans;
import org.apache.commons.lang3.ObjectUtils;

import java.nio.file.Path;

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
      .build();
  }

  static SolverRemote toSolverRemote(ConfigJson json) {
    if(nothingIsSet(json)){
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
      // fixme #533 need to create a dynamic json parser for different strategies,
      //      then a dynamic mapper to the solver remote
      .circuitBreaker(StaticThresholdCircuitBreakerStrategyConfig
        .builder()
        .failureThreshold(circuitBreaker.getFailureThreshold())
        .failureThresholdCapacity(circuitBreaker.getFailureThresholdCapacity())
        .successThreshold(circuitBreaker.getSuccessThreshold())
        .testDelay(circuitBreaker.getTestDelay())
        .build()
      )
      .build();
  }
}
