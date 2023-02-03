package com.mageddo.dnsproxyserver.config.entrypoint;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.EntryType;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ConfigJsonV2 implements ConfigJson {

  private int version = 2;

  private String activeEnv = Config.Env.DEFAULT_ENV;

  private List<String> remoteDnsServers = new ArrayList<>(); // dns servers formatted like 192.168.0.1:53

  @Getter(onMethod_ = {@JsonGetter("envs")})
  @Setter(onMethod_ = {@JsonGetter("envs")})
  private List<Env> _envs = new ArrayList<>();

  private Integer webServerPort;

  private Integer dnsServerPort;

  private Boolean defaultDns;

  private String logLevel;

  private String logFile;

  private Boolean registerContainerNames;

  private String hostMachineHostname;

  private String domain;

  private Boolean dpsNetwork;

  private Boolean dpsNetworkAutoConnect;

  public List<IpAddr> getRemoteDnsServers(){
    return this.remoteDnsServers
      .stream()
      .map(IpAddr::of)
      .toList();
  }

  @Override
  public List<Config.Env> getEnvs() {
    return ConfigJsonV2EnvsConverter.toDomainEnvs(this._envs);
  }

  @Data
  @Accessors(chain = true)
  public static class Env {
    private String name;
    private List<Hostname> hostnames;
  }

  @Data
  @Accessors(chain = true)
  public static class Hostname {

    private Long id;
    private String hostname;
    private String ip;
    private String target; // target hostname when type=CNAME

    private Integer ttl;
    private EntryType type;
  }

}
