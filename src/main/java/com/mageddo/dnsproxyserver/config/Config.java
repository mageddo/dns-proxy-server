package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.entrypoint.LogLevel;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain object which owns the configs.
 *
 * @see com.mageddo.dnsproxyserver.config.entrypoint.ConfigJson
 * @see com.mageddo.dnsproxyserver.config.entrypoint.ConfigFlag
 * @see com.mageddo.dnsproxyserver.config.entrypoint.ConfigEnv
 */
@Value
@Builder
public class Config {

  @NonNull
  private String version;

  @NonNull
  @Builder.Default
  private List<IpAddr> remoteDnsServers = new ArrayList<>();

  @NonNull
  private Integer webServerPort;

  @NonNull
  private Integer dnsServerPort;

  @NonNull
  private Boolean defaultDns;

  @NonNull
  private LogLevel logLevel;

  @NonNull
  private String logFile;

  @NonNull
  private Boolean registerContainerNames;

  @NonNull
  private String hostMachineHostname;

  @NonNull
  private String domain;

  @NonNull
  private Boolean dpsNetwork;

  @NonNull
  private Boolean dpsNetworkAutoConnect;

  @NonNull
  private Path configPath;

  public static SimpleServer.Protocol findDnsServerProtocol() {
    return SimpleServer.Protocol.BOTH;
  }

  @Value
  public static class Env {

    public static final String DEFAULT_ENV = "";

    private String name;
    private List<Entry> entries;

    public Env add(Entry entry){
      this.entries.add(entry);
      return this;
    }

    public static Env of(String name, List<Entry> entries) {
      return new Env(name, entries);
    }

    public static Env theDefault() {
      return new Env(DEFAULT_ENV, new ArrayList<>());
    }

  }

  @Value
  @Builder(builderClassName = "EntryBuilder", buildMethodName = "_build")
  public static class Entry {
    @NonNull
    private Long id;

    @NonNull
    private String hostname;

    private String ip; // hostname ip when type=A

    private String target; // target hostname when type=CNAME

    @NonNull
    private Integer ttl;

    @NonNull
    private Config.Entry.Type type;

    public static class EntryBuilder {
      public Entry build() {
        if (this.id == null) {
          this.id = System.nanoTime();
        }
        return this._build();
      }
    }

    public enum Type {
      A,
      CNAME
    }
  }
}
