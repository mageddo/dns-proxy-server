package com.mageddo.dnsproxyserver.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.commons.lang.Objects.mapOrNull;

/**
 * @see com.mageddo.dnsproxyserver.config.application.ConfigService
 */
@Value
@Builder(toBuilder = true, builderClassName = "ConfigBuilder")
public class Config {

  private String version;

  @Builder.Default
  private List<IpAddr> remoteDnsServers = new ArrayList<>();

  private Integer webServerPort;

  private Integer dnsServerPort;

  private SimpleServer.Protocol serverProtocol;

  //  private Boolean defaultDns;
  private DefaultDns defaultDns;
//  private String resolvConfPaths;
  //  private Boolean resolvConfOverrideNameServers;

  public boolean isResolvConfOverrideNameServersActive() {
    return this.getDefaultDns()
      .getResolvConf()
      .getOverrideNameServers();
  }

  public String getResolvConfPaths() {
    return this.defaultDns.getResolvConf().getPaths();
  }

  public Boolean isDefaultDnsActive() {
    return this.defaultDns.active;
  }

  public String getDefaultDnsResolvConfPaths() {
    return this.defaultDns.resolvConf.paths;
  }

  public Boolean getDefaultDnsResolvConfOverrideNameServers() {
    return this.defaultDns.resolvConf.overrideNameServers;
  }

  @Value
  @Builder(toBuilder = true)
  public static class DefaultDns {

    private Boolean active;
    private ResolvConf resolvConf;

    @Value
    @Builder(toBuilder = true)
    public static class ResolvConf {
      private String paths;
      private Boolean overrideNameServers;
    }
  }

  private LogLevel logLevel;

  private String logFile;

  private Boolean registerContainerNames;

  private String hostMachineHostname;

  private String domain;

  private Boolean mustConfigureDpsNetwork;

  private Boolean dpsNetworkAutoConnect;

  private Path configPath;

  private URI dockerHost;


  private Integer noEntriesResponseCode;

  private Boolean dockerSolverHostMachineFallbackActive;

  private SolverStub solverStub;

  private SolverRemote solverRemote;


  private String activeEnv;
  private List<Env> envs;

  @NonNull
  private Source source;

  @JsonIgnore
  public Boolean isSolverRemoteActive() {
    if (this.solverRemote == null) {
      return null;
    }
    return this.solverRemote.getActive();
  }

  public void resetConfigFile() {
    if (this.getConfigPath() == null) {
      throw new IllegalStateException("config file is null");
    }
    try {
      Files.deleteIfExists(this.getConfigPath());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @JsonIgnore
  public CircuitBreakerStrategyConfig getSolverRemoteCircuitBreakerStrategy() {
    if (this.solverRemote == null) {
      return null;
    }
    return this.solverRemote.getCircuitBreaker();
  }

  @JsonIgnore
  public String getSolverStubDomainName() {
    if (this.solverStub == null) {
      return null;
    }
    return this.solverStub.getDomainName();
  }

  public enum Source {
    JSON,
    FLAG,
    DEFAULT,
    MERGED,
    ENV,

    /**
     * Used for testing only;
     */
    @Deprecated
    TESTS_TEMPLATE,

  }

  @Value
  public static class Server {

    private Dns dns;
    private Web web;

    @Value
    public static class Dns {
      private Integer port;
    }

    @Value
    public static class Web {
      private Integer port;
    }
  }


  @Value
  public static class Env {

    public static final String DEFAULT_ENV = "";

    private String name;
    private List<Entry> entries;

    public static Env empty(String name) {
      return of(name, Collections.emptyList());
    }

    public Env add(Entry entry) {
      this.entries.add(entry);
      return this;
    }

    public static Env of(String name, List<Entry> entries) {
      return new Env(name.replaceAll("[^-_\\w\\s]+", ""), entries);
    }

    public static Env theDefault() {
      return new Env(DEFAULT_ENV, new ArrayList<>());
    }

  }

  @Value
  @Builder(builderClassName = "EntryBuilder", buildMethodName = "_build", toBuilder = true)
  public static class Entry {

    @NonNull
    private Long id;

    @NonNull
    private String hostname;

    /**
     * Used when {@link #type} in {@link Type#AAAA} , {@link Type#A}
     */
    private IP ip;

    /**
     * Target hostname when {@link #type} = {@link Type#CNAME}
     */
    private String target;

    @NonNull
    private Integer ttl;

    @NonNull
    private Config.Entry.Type type;

    public String requireTextIp() {
      Validate.isTrue(this.type.isAddressSolving() && this.ip != null, "IP is required");
      return this.ip.toText();
    }

    public String getIpAsText() {
      return mapOrNull(this.ip, IP::toText);
    }

    public static class EntryBuilder {
      public Entry build() {
        if (this.id == null) {
          this.id = System.nanoTime();
        }
        return this._build();
      }
    }

    @RequiredArgsConstructor
    public enum Type {

      A(org.xbill.DNS.Type.A),
      CNAME(org.xbill.DNS.Type.CNAME),
      AAAA(org.xbill.DNS.Type.AAAA),
      ;

      /**
       * See {@link org.xbill.DNS.Type}
       */
      private final int type;

      public boolean isNot(Type... types) {
        return ConfigEntryTypes.isNot(this.type, types);
      }

      public static Type of(Integer code) {
        for (final var t : values()) {
          if (Objects.equals(t.type, code)) {
            return t;
          }
        }
        return null;
      }

      public static Set<Type> asSet() {
        return Stream.of(values()).collect(Collectors.toSet());
      }

      public static boolean contains(Type type) {
        return asSet().contains(type);
      }

      public static boolean contains(Integer type) {
        return contains(of(type));
      }

      public IP.Version toVersion() {
        return switch (this) {
          case A -> IP.Version.IPV4;
          case AAAA -> IP.Version.IPV6;
          default -> throw new IllegalStateException("Unexpected value: " + this);
        };
      }

      public boolean isAddressSolving() {
        return ConfigEntryTypes.is(this, Config.Entry.Type.A, Config.Entry.Type.AAAA);
      }
    }
  }

  public static class ConfigBuilder {


  }
}
