package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.utils.Booleans;
import com.mageddo.utils.Files;

public class ConfigFlagMapper {
  public static Config toConfig(ConfigFlag config) {
    return Config.builder()
      .configPath(Files.pathOf(config.getConfigFilePath()))
      .registerContainerNames(config.getRegisterContainerNames())
      .domain(config.getDomain())
      .logFile(config.getLogToFile())
      .logLevel(ConfigFieldsValuesMapper.mapLogLevelFrom(config.getLogLevel()))
      .dockerHost(config.getDockerHost())
      .hostMachineHostname(config.getHostMachineHostname())
      .dpsNetworkAutoConnect(config.getDpsNetworkAutoConnect())
      .noEntriesResponseCode(config.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(config.getDockerSolverHostMachineFallbackActive())
      .mustConfigureDpsNetwork(config.getDpsNetwork())
      .webServerPort(config.getWebServerPort())
      .dnsServerPort(config.getDnsServerPort())
      .defaultDns(Config.DefaultDns.builder()
        .active(config.getDefaultDns())
        .resolvConf(Config.DefaultDns.ResolvConf
          .builder()
          .overrideNameServers(config.getResolvConfOverrideNameServers())
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
        .domainName(config.getStubSolverDomainName())
        .build()
      )
      .source(Config.Source.FLAG)
      .build();
  }
}
