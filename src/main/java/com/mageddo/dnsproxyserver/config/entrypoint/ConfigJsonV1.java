package com.mageddo.dnsproxyserver.config.entrypoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.List;

@Data
public class ConfigJsonV1 implements ConfigJson {

  private String activeEnv;

  private Integer webServerPort;

  private Integer dnsServerPort;

  private String logLevel;

  private String logFile;

  private Boolean registerContainerNames;

  private List<Integer[]> remoteDnsServers;

  @JsonProperty("envs")
  private List<Env> _envs;

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
      .map(IpAddr::of)
      .toList();
  }

  @JsonIgnore
  @Override
  public List<Config.Env> getEnvs() {
    return ConfigJsonV1EnvsConverter.toDomainEnvs(this._envs);
  }

  public ConfigJsonV2 toConfigV2() {
    return new ConfigJsonV2()
      .setDomain(this.getDomain())
      .setActiveEnv(this.getActiveEnv())
      .setDefaultDns(this.getDefaultDns())
      .setDpsNetwork(this.getDpsNetwork())
      .setDnsServerPort(this.getDnsServerPort())
      .setWebServerPort(this.getWebServerPort())
      .setDpsNetworkAutoConnect(this.getDpsNetworkAutoConnect())
      .setHostMachineHostname(this.getHostMachineHostname())
      .setLogFile(this.getLogFile())
      .setLogLevel(this.getLogLevel())
      .setRemoteDnsServers(this
        .getRemoteDnsServers()
        .stream()
        .map(IpAddr::toString).toList()
      )
      .set_envs(this.getEnvs()
        .stream()
        .map(ConfigJsonV2.Env::from)
        .toList()
      )
      ;
  }


  @Data
  @Accessors(chain = true)
  public static class Env {

    private String name;

    @JsonProperty("hostnames")
    private List<Entry> entries;
  }

  @Data
  @Accessors(chain = true)
  @NoArgsConstructor
  public static class Entry {
    private Long id;

    @NonNull
    private String hostname;

    @NonNull
    private Integer[] ip;

    @NonNull
    private Integer ttl;
  }

}
