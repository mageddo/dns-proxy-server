package com.mageddo.dnsproxyserver.config.dataprovider.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigJsonV1EnvsMapper;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import com.mageddo.net.IpAddr;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.net.URI;
import java.util.List;

/**
 * @deprecated it won't be extended, so it makes no sense to maintain it,
 * a roadmap to remove that feature is necessary.
 */
@Deprecated
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
    return ConfigJsonV1EnvsMapper.toDomainEnvs(this._envs);
  }

  @Override
  public SimpleServer.Protocol getServerProtocol() {
    return null;
  }

  @Override
  public URI getDockerHost() {
    return null;
  }

  @Override
  public Boolean getResolvConfOverrideNameServers() {
    return null;
  }

  @Override
  public Boolean getNoRemoteServers() {
    return null;
  }

  @Override
  public Integer getNoEntriesResponseCode() {
    return null;
  }

  @Override
  public Boolean getDockerSolverHostMachineFallbackActive() {
    return null;
  }

  @Override
  public ConfigJsonV2.SolverRemote getSolverRemote() {
    return null;
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
      .setRegisterContainerNames(this.getRegisterContainerNames())
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
