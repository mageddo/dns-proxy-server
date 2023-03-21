package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.entrypoint.LogLevel;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

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

  @NonNull
  private String resolvConfPaths;

  @NonNull
  private SimpleServer.Protocol serverProtocol;

  private URI dockerHost;

  public void resetConfigFile() {
    try {
      Files.deleteIfExists(this.getConfigPath());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
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

      public static boolean isNot(Integer code, Type... types) {
        return !is(of(code), types);
      }

      public static boolean is(Type current, Type... possible) {
        return Stream
          .of(possible)
          .collect(Collectors.toSet())
          .contains(current);
      }
    }
  }
}
