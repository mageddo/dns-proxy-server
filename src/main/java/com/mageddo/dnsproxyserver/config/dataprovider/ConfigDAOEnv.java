package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.CircuitBreaker;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigFieldsValuesMapper;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsproxyserver.utils.Booleans;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigDAOEnv implements ConfigDAO {

  @Override
  public Config find() {
    return toConfig(this.findRaw());
  }

  public ConfigEnv findRaw() {
    return ConfigEnv.fromEnv();
  }

  @Override
  public int priority() {
    return 1;
  }

  static Config toConfig(ConfigEnv config) {
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
        .circuitBreaker(CircuitBreaker.empty())
        .build())
      .build();
  }

}
