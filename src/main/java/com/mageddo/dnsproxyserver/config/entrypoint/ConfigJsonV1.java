package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.utils.Bytes;
import lombok.Data;

import java.util.List;

@Data
public class ConfigJsonV1 implements ConfigJson {

  private String activeEnv;

  private Integer webServerPort;

  private Integer dnsServerPort;

  private String logLevel;

  private String logFile;

  private Boolean registerContainerNames;

  private List<Byte[]> remoteDnsServers;


  @Override
  public Boolean getDefaultDns() {
    return null;
  }

  @Override
  public String getHostMachineHostname() {
    return null;
  }

  @Override
  public String getDomain() {
    return null;
  }

  @Override
  public Boolean getDpsNetwork() {
    return null;
  }

  @Override
  public Boolean getDpsNetworkAutoConnect() {
    return null;
  }

  @Override
  public List<IpAddr> getRemoteDnsServers() {
    return this.remoteDnsServers
      .stream()
      .map(it -> toIpAddr(Bytes.toNative(it)))
      .toList();
  }

  private IpAddr toIpAddr(byte[] ip) {
    return IpAddr.of(IP.of(ip));
  }
}
