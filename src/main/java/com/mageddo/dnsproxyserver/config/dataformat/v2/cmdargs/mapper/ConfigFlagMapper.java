package com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.config.mapper.LogLevelMapper;
import com.mageddo.dnsproxyserver.utils.Booleans;
import com.mageddo.utils.Files;

public class ConfigFlagMapper {
  public static Config toConfig(ConfigFlag config) {
    return Config.builder()
        .server(Config.Server
            .builder()
            .dnsServerNoEntriesResponseCode(config.getNoEntriesResponseCode())
            .webServerPort(config.getWebServerPort())
            .dnsServerPort(config.getDnsServerPort())
            .build()
        )
        .configPath(Files.pathOf(config.getConfigFilePath()))
        .log(Config.Log
            .builder()
            .file(config.getLogToFile())
            .level(LogLevelMapper.mapLogLevelFrom(config.getLogLevel()))
            .build()
        )
        .defaultDns(Config.DefaultDns.builder()
            .active(config.getDefaultDns())
            .resolvConf(Config.DefaultDns.ResolvConf
                .builder()
                .overrideNameServers(config.getResolvConfOverrideNameServers())
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
            .domainName(config.getStubSolverDomainName())
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
        .source(Config.Source.FLAG)
        .build();
  }
}
