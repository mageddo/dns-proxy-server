package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo;

import java.net.URI;
import java.util.List;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IpAddr;

/**
 * @deprecated see #594.
 */
@Deprecated
public interface ConfigJson {

  String getActiveEnv();

  Integer getWebServerPort();

  Integer getDnsServerPort();

  Boolean getDefaultDns();

  String getLogLevel();

  String getLogFile();

  Boolean getRegisterContainerNames();

  String getHostMachineHostname();

  String getDomain();

  Boolean getDpsNetwork();

  Boolean getDpsNetworkAutoConnect();

  List<IpAddr> getRemoteDnsServers();

  List<Config.Env> getEnvs();

  SimpleServer.Protocol getServerProtocol();

  URI getDockerHost();

  Boolean getResolvConfOverrideNameServers();

  Boolean getNoRemoteServers();

  Integer getNoEntriesResponseCode();

  Boolean getDockerSolverHostMachineFallbackActive();

  ConfigJsonV2.SolverRemote getSolverRemote();

  ConfigJsonV2.SolverStub getSolverStub();

  ConfigJsonV2.CircuitBreaker getSolverRemoteCircuitBreaker();

  default boolean hasRemoteDnsServers() {
    return getRemoteDnsServers() != null && !getRemoteDnsServers().isEmpty();
  }
}
