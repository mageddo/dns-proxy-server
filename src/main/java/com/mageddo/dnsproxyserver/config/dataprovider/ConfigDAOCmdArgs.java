package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigFieldsValuesMapper;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.utils.Booleans;
import com.mageddo.utils.Files;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigDAOCmdArgs implements ConfigDAO {

  private static String[] args = new String[]{};

  @Override
  public Config find() {
    return toConfig(this.findRaw());
  }

  public ConfigFlag findRaw() {
    return ConfigFlag.parse(args);
  }

  @Override
  public int priority() {
    return 3;
  }

  public static void setArgs(String[] args) {
    ConfigDAOCmdArgs.args = args;
  }

  static Config toConfig(ConfigFlag config) {
    return Config.builder()
      .configPath(Files.pathOf(config.getConfigPath()))
      .registerContainerNames(config.getRegisterContainerNames())
      .domain(config.getDomain())
      .logFile(config.getLogToFile())
      .logLevel(ConfigFieldsValuesMapper.mapLogLevelFrom(config.getLogLevel()))
      .dockerHost(config.getDockerHost())
      .hostMachineHostname(config.getHostMachineHostname())
      .dpsNetworkAutoConnect(config.getDpsNetworkAutoConnect())
      .noEntriesResponseCode(config.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(config.getDockerSolverHostMachineFallbackActive())
      .resolvConfOverrideNameServers(config.getResolvConfOverrideNameServers())
      .mustConfigureDpsNetwork(config.getDpsNetwork())
      .webServerPort(config.getWebServerPort())
      .dnsServerPort(config.getDnsServerPort())
      .defaultDns(config.getDefaultDns())
      .solverRemote(SolverRemote
        .builder()
        .active(Booleans.reverseWhenNotNull(config.getNoRemoteServers()))
        .build()
      )
      .build();
  }

  static String[] getArgs() {
    return args;
  }
}
