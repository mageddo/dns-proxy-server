package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverDocker;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsproxyserver.utils.Booleans;

public class ConfigEnvMapper {
  public static Config toConfig(ConfigEnv config) {
    return Config.builder()
      .logFile(config.getLogFile())
      .logLevel(ConfigFieldsValuesMapper.mapLogLevelFrom(config.getLogLevel()))
      .hostMachineHostname(config.getHostMachineHostname())
      .noEntriesResponseCode(config.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(config.getDockerSolverHostMachineFallbackActive())
      .defaultDns(Config.DefaultDns
        .builder()
        .resolvConf(Config.DefaultDns.ResolvConf
          .builder()
          .overrideNameServers(config.getResolvConfOverrideNameServers())
          .paths(config.getResolvConfPath())
          .build()
        )
        .build()
      )
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
      .solverDocker(SolverDocker
        .builder()
        .dpsNetwork(SolverDocker.DpsNetwork
          .builder()
          .autoCreate(config.getDpsNetwork())
          .autoConnect(config.getDpsNetworkAutoConnect())
          .build()
        )
        .dockerDaemonUri(config.getDockerHost())
        .registerContainerNames(config.getRegisterContainerNames())
        .domain(config.getDomain())
        .build()
      )
      .source(Config.Source.ENV)
      .build();
  }
}
