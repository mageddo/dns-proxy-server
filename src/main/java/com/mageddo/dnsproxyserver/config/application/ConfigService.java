package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigPropDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAO;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import com.mageddo.dnsproxyserver.utils.Numbers;
import org.apache.commons.lang3.SystemUtils;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.Comparator;
import java.util.List;

@Singleton
public class ConfigService {

  private final List<MultiSourceConfigDAO> configDAOS;

  @Inject
  public ConfigService(Instance<MultiSourceConfigDAO> configDAOS) {
    this.configDAOS = configDAOS
      .stream()
      .toList()
    ;
  }

  public Config findCurrentConfig() {
    return this.buildCurrentConfig(this.findConfigs());
  }

  private Config buildCurrentConfig(List<Config> configs) {
    final var builder = Config.builder();
    builder.version(ConfigPropDAO.getVersion());
    for (final var config : configs) {
      // todo talvez vai precisasr para cada campo acumular os valores de todos os objetos de config da lista,
      //        jogar numa list e então pegar o primeiro não nulo.
      mapFromTo(config, builder);
    }
    final var config = fillWithDefaultValuesIfNotSet(builder);
    validate(config);
    return config;
  }

  private void validate(Config config) {
    throw new UnsupportedOperationException();
  }

  private static Config fillWithDefaultValuesIfNotSet(Config.ConfigBuilder builder) {
    var tmpRes = builder.build();
    if (tmpRes.getServerProtocol() == null) {
      tmpRes = tmpRes.toBuilder()
        .serverProtocol(SimpleServer.Protocol.UDP_TCP)
        .build();
    }
    if(tmpRes.getDockerHost() == null){
      tmpRes = tmpRes.toBuilder()
        .dockerHost(buildDefaultDockerHost())
        .build();
    }
    return tmpRes;
  }

  static void mapFromTo(Config config, Config.ConfigBuilder builder) {
    builder
      .webServerPort(Numbers.positiveOrNull(config.getWebServerPort()))
      .dnsServerPort(Numbers.positiveOrNull(config.getDnsServerPort()))
      .defaultDns(config.getDefaultDns())
      .logLevel(config.getLogLevel())
      .logFile(config.getLogFile())
      .registerContainerNames(config.getRegisterContainerNames())
      .hostMachineHostname(config.getHostMachineHostname())
      .domain(config.getDomain())
      .mustConfigureDpsNetwork(config.getMustConfigureDpsNetwork())
      .dpsNetworkAutoConnect(config.getDpsNetworkAutoConnect())
      .remoteDnsServers(config.getRemoteDnsServers())
      .configFileRelativePath(config.getConfigFileRelativePath())
      .resolvConfPaths(config.getResolvConfPaths())
      .serverProtocol(config.getServerProtocol())
      .dockerHost(config.getDockerHost())
      .resolvConfOverrideNameServers(config.getResolvConfOverrideNameServers())
      .noRemoteServers(config.getNoRemoteServers())
      .noEntriesResponseCode(config.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(config.getDockerSolverHostMachineFallbackActive())
    ;
  }

  private static URI buildDefaultDockerHost() {
    if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
      return URI.create("unix:///var/run/docker.sock");
    }
    if (SystemUtils.IS_OS_WINDOWS) {
      return URI.create("npipe:////./pipe/docker_engine");
    }
    return null; // todo unsupported OS
  }

  private List<Config> findConfigs() {
    return this.configDAOS
      .stream()
      .sorted(Comparator.comparingInt(MultiSourceConfigDAO::priority))
      .map(MultiSourceConfigDAO::find)
      .toList();
  }
}
