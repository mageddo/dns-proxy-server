package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsproxyserver.utils.Booleans;

public class ConfigEnvMapper {
  public static Config toConfig(ConfigEnv config) {
    return Config.builder()
      .registerContainerNames(config.getRegisterContainerNames())
      .domain(config.getDomain())
      .logFile(config.getLogFile())
      .logLevel(ConfigFieldsValuesMapper.mapLogLevelFrom(config.getLogLevel()))
      .dockerHost(config.getDockerHost())
      .hostMachineHostname(config.getHostMachineHostname())
      .dpsNetworkAutoConnect(config.getDpsNetworkAutoConnect())
      .noEntriesResponseCode(config.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(config.getDockerSolverHostMachineFallbackActive())
      .resolvConfOverrideNameServers(config.getResolvConfOverrideNameServers())
      .mustConfigureDpsNetwork(config.getDpsNetwork())
      .resolvConfPaths(config.getResolvConfPath())
      .solverRemote(SolverRemote
        .builder()
        .active(Booleans.reverseWhenNotNull(config.getNoRemoteServers()))
        .build()
      )
      .solverStub(SolverStub
        .builder()
        .domainName(config.getSolverStubDomainName())
        .build()
      )
      .source(Config.Source.ENV)
      .build();
  }
}
