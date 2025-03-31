package com.mageddo.dnsproxyserver.config.provider.legacyenv;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Log;
import com.mageddo.dnsproxyserver.config.Server;
import com.mageddo.dnsproxyserver.config.SolverDocker;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.SolverStub;
import com.mageddo.dnsproxyserver.config.SolverSystem;
import com.mageddo.dnsproxyserver.config.mapper.LogLevelMapper;
import com.mageddo.dnsproxyserver.utils.Booleans;

public class ConfigEnvMapper {
  public static Config toConfig(ConfigEnv config) {
    return Config.builder()
      .server(Server
        .builder()
        .dnsServerNoEntriesResponseCode(config.getNoEntriesResponseCode())
        .build()
      )
      .log(Log
        .builder()
        .file(config.getLogFile())
        .level(LogLevelMapper.mapLogLevelFrom(config.getLogLevel()))
        .build()
      )
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
        .hostMachineFallback(config.getDockerSolverHostMachineFallbackActive())
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
      .solverSystem(SolverSystem
        .builder()
        .hostMachineHostname(config.getHostMachineHostname())
        .build()
      )
      .source(Config.Source.ENV)
      .build();
  }
}
