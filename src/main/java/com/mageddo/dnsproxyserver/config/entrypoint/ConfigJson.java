package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.server.dns.IpSockAddr;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;

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

  List<IpSockAddr> getRemoteDnsServers();

  List<Config.Env> getEnvs();

  SimpleServer.Protocol getServerProtocol();

  URI getDockerHost();
}
