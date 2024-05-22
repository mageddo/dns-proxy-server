package com.mageddo.dnsproxyserver.config.dataprovider.vo;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import com.mageddo.net.IpAddr;

import java.net.URI;
import java.util.List;

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

}
