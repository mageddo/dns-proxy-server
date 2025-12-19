package com.mageddo.dnsproxyserver.config;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mageddo.dnsserver.SimpleServer.Protocol;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

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

  @Deprecated(forRemoval = true)
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
  DefaultDns.ResolvConf getDefaultDnsResolvConf() {
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
    if (this.solverDocker == null) {
      return null;
    }
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
    if (this.getDnsServer() == null) {
      return null;
    }
    return this.getDnsServer()
        .getNoEntriesResponseCode();
  }

  public Integer getDnsServerPort() {
    if (this.getDnsServer() == null) {
      return null;
    }
    return this.getDnsServer()
        .getPort();
  }

  public Integer getWebServerPort() {
    if (this.server == null) {
      return null;
    }
    return this.server.getWebServerPort();
  }

  public Protocol getServerProtocol() {
    if (this.server == null) {
      return null;
    }
    return this.server.dns.protocol;
  }

  @JsonIgnore
  public Log.Level getLogLevel() {
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
      return Collections.emptyList();
    }
    return this.solverLocal.getEnvs();
  }

  public String getActiveEnv() {
    if (this.solverLocal == null) {
      return null;
    }
    return this.solverLocal.getActiveEnv();
  }

  public SolverDocker.Networks getDockerSolverNetworks() {
    if (this.solverDocker == null) {
      return null;
    }
    return this.solverDocker.networks;
  }

  public Server.Dns getDnsServer() {
    if (this.server == null) {
      return null;
    }
    return this.server.dns;
  }

  @Value
  @Builder(toBuilder = true)
  public static class DefaultDns {

    Boolean active;
    ResolvConf resolvConf;

    @Value
    @Builder(toBuilder = true)
    public static class ResolvConf {
      String paths;
      Boolean overrideNameServers;
    }
  }

  @JsonIgnore
  public Boolean isSolverRemoteActive() {
    if (this.solverRemote == null) {
      return null;
    }
    return this.solverRemote.getActive();
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
    YAML,
    FILE,
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
  @Builder(toBuilder = true)
  public static class Env {

    public static final String DEFAULT_ENV = "";

    String name;

    List<Entry> entries;

    public List<Entry> getHostnames() {
      return this.entries;
    }

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
    Long id;

    @NonNull
    String hostname;

    /**
     * Used when {@link #type} in {@link Type#AAAA} , {@link Type#A}
     */
    IP ip;

    /**
     * Target hostname when {@link #type} = {@link Type#CNAME}
     */
    String target;

    @NonNull
    Integer ttl;

    @NonNull
    Config.Entry.Type type;

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

    // TODO move to a separate mapper
    @RequiredArgsConstructor
    public enum Type {

      A(org.xbill.DNS.Type.A),
      CNAME(org.xbill.DNS.Type.CNAME),
      AAAA(org.xbill.DNS.Type.AAAA),
      ;

      /**
       * See {@link org.xbill.DNS.Type}
       */
      final int type;

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
        return Stream.of(values())
            .collect(Collectors.toSet());
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

  @Value
  @Builder
  public static class Log {
    Level level;
    String file;

    public enum Level {

      ERROR,
      WARNING("WARN"),
      INFO,
      DEBUG,
      TRACE,
      ;

      final String slf4jName;

      Level() {
        this.slf4jName = null;
      }

      Level(String slf4jName) {
        this.slf4jName = slf4jName;
      }

      public String getSlf4jName() {
        return StringUtils.firstNonBlank(this.slf4jName, this.name());
      }

      @Override
      public String toString() {
        return this.name();
      }

      public ch.qos.logback.classic.Level toLogbackLevel() {
        return ch.qos.logback.classic.Level.convertAnSLF4JLevel(
            org.slf4j.event.Level.valueOf(this.getSlf4jName())
        );
      }
    }
  }

  @Value
  @Builder
  public static class Server {
    String host;
    Dns dns;
    Integer webServerPort;

    @Value
    @Builder
    public static class Dns {
      Protocol protocol;
      Integer port;
      Integer noEntriesResponseCode;
    }
  }

  @Value
  @Builder(toBuilder = true)
  public static class SolverDocker {

    URI dockerDaemonUri;
    Boolean registerContainerNames;
    String domain;
    DpsNetwork dpsNetwork;
    Boolean hostMachineFallback;

    Networks networks;

    public boolean shouldUseHostMachineFallback() {
      return BooleanUtils.toBoolean(hostMachineFallback);
    }

    public boolean shouldAutoCreateDpsNetwork() {
      if (this.dpsNetwork == null) {
        return false;
      }
      return this.dpsNetwork.shouldAutoCreate();
    }

    public boolean shouldAutoConnect() {
      if (this.dpsNetwork == null) {
        return false;
      }
      return this.dpsNetwork.shouldAutoConnect();
    }

    @Value
    @Builder
    public static class Networks {

      Preferred preferred;

      @Value
      @Builder
      public static class Preferred {

        boolean overrideDefault;

        List<String> names;

      }
    }

    @Value
    @Builder
    public static class DpsNetwork {

      String name;
      Boolean autoCreate;
      Boolean autoConnect;
      List<NetworkConfig> configs;

      @Value
      @Builder
      public static class NetworkConfig {
        String subNet;
        String ipRange;
        String gateway;
      }

      public boolean shouldAutoConnect() {
        return BooleanUtils.isTrue(this.autoConnect);
      }

      public boolean shouldAutoCreate() {
        return BooleanUtils.isTrue(this.autoCreate);
      }
    }
  }

  @Value
  @Builder(toBuilder = true)
  public static class SolverLocal {

    String activeEnv;

    List<Env> envs;

    public List<Env> getEnvs() {
      return ObjectUtils.firstNonNull(this.envs, Collections.emptyList());
    }

    @JsonIgnore
    public Env getFirst() {
      if (this.envs == null || this.envs.isEmpty()) {
        return null;
      }
      return this.envs.getFirst();
    }
  }

  @Value
  @Builder
  public static class SolverRemote {

    Boolean active;

    CircuitBreakerStrategyConfig circuitBreaker;

    @Builder.Default
    List<IpAddr> dnsServers = new ArrayList<>();

  }

  @Value
  @Builder
  public static class SolverStub {
    String domainName;
  }

  @Value
  @Builder
  public static class SolverSystem {
    String hostMachineHostname;
  }
}
