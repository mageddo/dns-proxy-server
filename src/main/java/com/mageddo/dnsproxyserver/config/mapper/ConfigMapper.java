package com.mageddo.dnsproxyserver.config.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigPropDAO;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.net.IpAddr;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static com.mageddo.dnsproxyserver.utils.ListOfObjectUtils.mapField;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonEmptyListRequiring;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNullRequiring;

public class ConfigMapper {
  public static Config mapFrom(List<Config> configs) {
    final var config = Config.builder()
      .version(ConfigPropDAO.getVersion())
      .webServerPort(Numbers.firstPositive(mapField(Config::getWebServerPort, configs)))
      .dnsServerPort(Numbers.firstPositive(mapField(Config::getDnsServerPort, configs)))
      .defaultDns(firstNonNullRequiring(mapField(Config::getDefaultDns, configs)))
      .logLevel(firstNonNullRequiring(mapField(Config::getLogLevel, configs)))
      .logFile(firstNonNullRequiring(mapField(Config::getLogFile, configs)))
      .registerContainerNames(firstNonNullRequiring(mapField(Config::getRegisterContainerNames, configs)))
      .hostMachineHostname(firstNonNullRequiring(mapField(Config::getHostMachineHostname, configs)))
      .domain(firstNonNullRequiring(mapField(Config::getDomain, configs)))
      .mustConfigureDpsNetwork(firstNonNullRequiring(mapField(Config::getMustConfigureDpsNetwork, configs)))
      .dpsNetworkAutoConnect(firstNonNullRequiring(mapField(Config::getDpsNetworkAutoConnect, configs)))
      .remoteDnsServers(firstNonEmptyListRequiring(mapField(Config::getRemoteDnsServers, configs, buildDefaultDnsServers())))
      .configPath(firstNonNullRequiring(mapField(Config::getConfigPath, configs)))
      .resolvConfPaths(firstNonNullRequiring(mapField(Config::getResolvConfPaths, configs)))
      .serverProtocol(firstNonNullRequiring(mapField(Config::getServerProtocol, configs, SimpleServer.Protocol.UDP_TCP)))
      .dockerHost(firstNonNullRequiring(mapField(Config::getDockerHost, configs, buildDefaultDockerHost())))
      .resolvConfOverrideNameServers(firstNonNullRequiring(mapField(Config::getResolvConfOverrideNameServers, configs)))
      .noRemoteServers(firstNonNullRequiring(mapField(Config::getNoRemoteServers, configs)))
      .noEntriesResponseCode(firstNonNullRequiring(mapField(Config::getNoEntriesResponseCode, configs)))
      .dockerSolverHostMachineFallbackActive(firstNonNullRequiring(mapField(Config::getDockerSolverHostMachineFallbackActive, configs)))
      .build();
    validate(config);
    return config;
  }

  static void validate(Config config) {
    Validate.notNull(config.getVersion());
    Validate.notNull(config.getRemoteDnsServers());
    Validate.isTrue(config.getWebServerPort() != null && config.getWebServerPort() > 0);
    Validate.isTrue(config.getDnsServerPort() != null && config.getDnsServerPort() > 0);
    Validate.notNull(config.getLogFile());
    Validate.notNull(config.getRegisterContainerNames());
    Validate.notNull(config.getHostMachineHostname());
    Validate.notNull(config.getDomain());
    Validate.notNull(config.getMustConfigureDpsNetwork());
    Validate.notNull(config.getDpsNetworkAutoConnect());
    Validate.notNull(config.getResolvConfPaths());
    Validate.notNull(config.getServerProtocol());
  }

  static List<IpAddr> buildDefaultDnsServers() {
    return Collections.singletonList(IpAddr.of("8.8.8.8:53"));
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
}
