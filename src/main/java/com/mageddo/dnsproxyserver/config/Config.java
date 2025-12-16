package com.mageddo.dnsproxyserver.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigV2Service;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.commons.lang.Objects.mapOrNull;

/**
 * @see ConfigService
 */
@Value
@Builder(toBuilder = true, builderClassName = "ConfigBuilder")
public class Config {

  String version;

  Server server;

  DefaultDns defaultDns;

  Log log;

  Path configPath;

  SolverStub solverStub;

  SolverRemote solverRemote;

  SolverDocker solverDocker;

  SolverSystem solverSystem;

  SolverLocal solverLocal;

  @NonNull
  Source source;

  @JsonIgnore
  public Boolean isDefaultDnsActive() {
    if (this.defaultDns == null) {
      return null;
    }
    return this.defaultDns.active;
  }

  @JsonIgnore
  public String getDefaultDnsResolvConfPaths() {
    if (this.getDefaultDnsResolvConf() == null) {
      return null;
    }
    return this.getDefaultDnsResolvConf().paths;
  }

  @JsonIgnore
  public Boolean isResolvConfOverrideNameServersActive() {
    if (this.getDefaultDnsResolvConf() == null) {
      return null;
    }
    return this.getDefaultDnsResolvConf().overrideNameServers;
  }

  @JsonIgnore
  private DefaultDns.ResolvConf getDefaultDnsResolvConf() {
    if (this.defaultDns == null) {
      return null;
    }
    return this.defaultDns.resolvConf;
  }

  @JsonIgnore
  public List<IpAddr> getRemoteDnsServers() {
    if (this.solverRemote == null) {
      return Collections.emptyList();
    }
    return this.solverRemote.getDnsServers();
  }

  @JsonIgnore
  public Boolean getRegisterContainerNames() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.getRegisterContainerNames();
  }

  @JsonIgnore
  public String getDockerDomain() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.getDomain();
  }

  @JsonIgnore
  public Boolean getDockerSolverMustConfigureDpsNetwork() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.shouldAutoCreateDpsNetwork();
  }

  @JsonIgnore
  public Boolean getDpsNetworkAutoConnect() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.shouldAutoConnect();
  }

  @JsonIgnore
  public URI getDockerDaemonUri() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.getDockerDaemonUri();
  }

  @JsonIgnore
  public SolverDocker.DpsNetwork getDockerSolverDpsNetwork() {
    return this.solverDocker.getDpsNetwork();
  }

  @JsonIgnore
  public Boolean getDockerSolverHostMachineFallbackActive() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.shouldUseHostMachineFallback();
  }

  public String getHostMachineHostname() {
    if (this.solverSystem == null) {
      return null;
    }
    return this.solverSystem.getHostMachineHostname();
  }

  public Integer getNoEntriesResponseCode() {
    if (this.server == null) {
      return null;
    }
    return this.server.getDnsServerNoEntriesResponseCode();
  }

  public Integer getDnsServerPort() {
    if (this.server == null) {
      return null;
    }
    return this.server.getDnsServerPort();
  }

  public Integer getWebServerPort() {
    if (this.server == null) {
      return null;
    }
    return this.server.getWebServerPort();
  }

  public SimpleServer.Protocol getServerProtocol() {
    if (this.server == null) {
      return null;
    }
    return this.server.getServerProtocol();
  }

  @JsonIgnore
  public LogLevel getLogLevel() {
    if (this.log == null) {
      return null;
    }
    return this.log.getLevel();
  }

  @JsonIgnore
  public String getLogFile() {
    if (this.log == null) {
      return null;
    }
    return this.log.getFile();
  }

  @JsonIgnore
  public List<Env> getEnvs() {
    if (this.solverLocal == null) {
      return null;
    }
    return this.solverLocal.getEnvs();
  }

  @JsonIgnore
  public String getActiveEnv() {
    if (this.solverLocal == null) {
      return null;
    }
    return this.solverLocal.getActiveEnv();
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

    public Entry getFirstEntry() {
      if (this.entries == null || this.entries.isEmpty()) {
        return null;
      }
      return this.entries.getFirst();
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
