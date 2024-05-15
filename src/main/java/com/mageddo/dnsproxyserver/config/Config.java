package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
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
 * Domain object which owns the configs.
 *
 * @see ConfigJson
 * @see ConfigFlag
 * @see ConfigEnv
 */
@Value
@Builder
public class Config {

//  @NonNull
  private String version;

//  @NonNull
  @Builder.Default
  private List<IpAddr> remoteDnsServers = new ArrayList<>();

//  @NonNull
  private Integer webServerPort;

//  @NonNull
  private Integer dnsServerPort;

  private Boolean defaultDns;

  private LogLevel logLevel;

//  @NonNull
  private String logFile;

//  @NonNull
  private Boolean registerContainerNames;

//  @NonNull
  private String hostMachineHostname;

//  @NonNull
  private String domain;

//  @NonNull
  private Boolean mustConfigureDpsNetwork;

//  @NonNull
  private Boolean dpsNetworkAutoConnect;

//  @NonNull
  private Path configPath;

//  @NonNull
  private String resolvConfPaths;

//  @NonNull
  private SimpleServer.Protocol serverProtocol;

  private URI dockerHost;

  private Boolean resolvConfOverrideNameServers;

  private Boolean noRemoteServers;

  private Integer noEntriesResponseCode;

  private Boolean dockerSolverHostMachineFallbackActive;

  public void resetConfigFile() {
    try {
      Files.deleteIfExists(this.getConfigPath());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public boolean isHelp() {
    throw new UnsupportedOperationException();
  }

  public boolean isVersion() {
    throw new UnsupportedOperationException();
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
}
