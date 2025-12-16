package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.mapper.ConfigJsonV2EnvsMapper;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ConfigJsonV2 implements ConfigJson {

  private int version = 2;

  private String activeEnv = Config.Env.DEFAULT_ENV;

  @JsonProperty("remoteDnsServers")
  private List<String> remoteDnsServers = new ArrayList<>(); // dns servers formatted like 192.168.0.1:53

  @JsonProperty("envs")
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

  private SimpleServer.Protocol serverProtocol;

  private URI dockerHost;

  private Boolean resolvConfOverrideNameServers;

  private Boolean noRemoteServers;

  private Integer noEntriesResponseCode;

  private Boolean dockerSolverHostMachineFallbackActive;

  private SolverRemote solverRemote;

  private SolverStub solverStub;

  @JsonIgnore
  public List<IpAddr> getRemoteDnsServers() {
    return this.remoteDnsServers
      .stream()
      .map(IpAddr::of)
      .toList();
  }

  @JsonIgnore
  @Override
  public List<Config.Env> getEnvs() {
    return ConfigJsonV2EnvsMapper.toDomainEnvs(this._envs);
  }

  @Override
  public Boolean getDockerSolverHostMachineFallbackActive() {
    return this.dockerSolverHostMachineFallbackActive;
  }

  @Override
  public SolverStub getSolverStub() {
    return this.solverStub;
  }

  @JsonIgnore
  @Override
  public CircuitBreaker getSolverRemoteCircuitBreaker() {
    if (this.solverRemote != null) {
      return this.solverRemote.circuitBreaker;
    }
    return null;
  }

  @Data
  @Accessors(chain = true)
  public static class Env {

    private String name;
    private List<Entry> hostnames = new ArrayList<>();

    public Env add(Entry env) {
      this.hostnames.add(env);
      return this;
    }

    public static Env from(Config.Env from) {
      return new Env()
        .setName(from.getName())
        .setHostnames(Entry.from(from.getEntries()))
        ;
    }
  }

  @Data
  @Accessors(chain = true)
  public static class Entry {

    private Long id;
    private String hostname;
    private String ip;
    private String target; // target hostname when type=CNAME

    private Integer ttl;
    private Type type;

    public static Entry from(Config.Entry entry) {
      return new Entry()
        .setHostname(entry.getHostname())
        .setId(entry.getId())
        .setIp(entry.getIpAsText())
        .setTtl(entry.getTtl())
        .setTarget(entry.getTarget())
        .setType(entry.getType())
        ;
    }

    public static List<Entry> from(List<Config.Entry> entries) {
      return entries
        .stream()
        .map(Entry::from)
        .collect(Collectors.toList());
    }

    public static Entry sample() {
      return Entry.from(Config.Entry
        .builder()
        .type(Type.A)
        .hostname("dps-sample.dev")
        .ip(IP.of("192.168.0.254"))
        .ttl(30)
        .id(1L)
        .build()
      );
    }
  }

  @Data
  public static class SolverRemote {

    private CircuitBreaker circuitBreaker;

  }

  @Data
  public static class SolverStub {

    private String domainName;

  }


  @JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "strategy",
    defaultImpl = StaticThresholdCircuitBreaker.class
  )
  @JsonSubTypes({
    @JsonSubTypes.Type(value = StaticThresholdCircuitBreaker.class, name = "STATIC_THRESHOLD"),
    @JsonSubTypes.Type(value = CanaryRateThresholdCircuitBreaker.class, name = "CANARY_RATE_THRESHOLD")
  })
  public interface CircuitBreaker {

    CircuitBreakerStrategyConfig.Name strategy();

  }


  @Data
  public static class StaticThresholdCircuitBreaker implements CircuitBreaker {

    private Integer failureThreshold;
    private Integer failureThresholdCapacity;
    private Integer successThreshold;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration testDelay;

    @Override
    public CircuitBreakerStrategyConfig.Name strategy() {
      return CircuitBreakerStrategyConfig.Name.STATIC_THRESHOLD;
    }
  }

  @Data
  public static class CanaryRateThresholdCircuitBreaker implements CircuitBreaker {

    private float failureRateThreshold;
    private int minimumNumberOfCalls;
    private int permittedNumberOfCallsInHalfOpenState;

    @Override
    public CircuitBreakerStrategyConfig.Name strategy() {
      return CircuitBreakerStrategyConfig.Name.CANARY_RATE_THRESHOLD;
    }
  }
}
