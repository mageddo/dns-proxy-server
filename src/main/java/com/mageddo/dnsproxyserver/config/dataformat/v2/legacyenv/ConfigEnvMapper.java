package com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.mapper.LogLevelMapper;
import com.mageddo.dnsproxyserver.utils.Booleans;

public class ConfigEnvMapper {
  public static Config toConfig(ConfigEnv config) {
    return Config.builder()
        .server(Config.Server
            .builder()
            .dns(Config.Server.Dns.builder()
                .noEntriesResponseCode(config.getNoEntriesResponseCode())
                .build()
            )
            .build()
        )
        .log(Config.Log
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
        .solverRemote(Config.SolverRemote
            .builder()
            .active(Booleans.reverseWhenNotNull(config.getNoRemoteServers()))
            .build()
        )
        .solverStub(Config.SolverStub
            .builder()
            .domainName(config.getSolverStubDomainName())
            .build()
        )
        .solverDocker(Config.SolverDocker
            .builder()
            .hostMachineFallback(config.getDockerSolverHostMachineFallbackActive())
            .dpsNetwork(Config.SolverDocker.DpsNetwork
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
        .solverSystem(Config.SolverSystem
            .builder()
            .hostMachineHostname(config.getHostMachineHostname())
            .build()
        )
        .source(Config.Source.ENV)
        .build();
  }
}
