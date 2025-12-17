package com.mageddo.dnsproxyserver.config.dataformat.v3;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigV3 {

  public int version;
  public Server server;
  public Solver solver;
  public DefaultDns defaultDns;
  public Log log;

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "strategy",
      defaultImpl = CircuitBreaker.CanaryRateThreshold.class
  )
  @JsonSubTypes({
      @JsonSubTypes.Type(
          name = "STATIC_THRESHOLD",
          value = CircuitBreaker.StaticThreshold.class
      ),
      @JsonSubTypes.Type(
          name = "CANARY_RATE_THRESHOLD",
          value = CircuitBreaker.CanaryRateThreshold.class
      )
  })
  public static interface CircuitBreaker {

    CircuitBreakerStrategyConfig.Name getName();

    @Data
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class StaticThreshold implements CircuitBreaker {

      private Integer failureThreshold;
      private Integer failureThresholdCapacity;
      private Integer successThreshold;

      @JsonSerialize(using = DurationSerializer.class)
      @JsonDeserialize(using = DurationDeserializer.class)
      private Duration testDelay;

      @Override
      public CircuitBreakerStrategyConfig.Name getName() {
        return CircuitBreakerStrategyConfig.Name.STATIC_THRESHOLD;
      }
    }

    @Data
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class CanaryRateThreshold implements CircuitBreaker {

      private float failureRateThreshold;
      private int minimumNumberOfCalls;
      private int permittedNumberOfCallsInHalfOpenState;

      @Override
      public CircuitBreakerStrategyConfig.Name getName() {
        return CircuitBreakerStrategyConfig.Name.CANARY_RATE_THRESHOLD;
      }
    }
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class DefaultDns {
    public Boolean active;
    public ResolvConf resolvConf;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Dns {
    public Integer port;
    public Integer noEntriesResponseCode;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Docker {
    public Boolean registerContainerNames;
    public String domain;
    public Boolean hostMachineFallback;
    public DpsNetwork dpsNetwork;
    //    public Networks networks;
    public String dockerDaemonUri;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class DpsNetwork {
    public String name;
    public Boolean autoCreate;
    public Boolean autoConnect;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Env {
    public String name;
    public List<Hostname> hostnames;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Hostname {
    public String type;
    public String hostname;
    public String ip;
    public Integer ttl;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Local {
    public String activeEnv;
    public List<Env> envs;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Log {
    public String level;
    public String file;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Networks {
    public List<String> preferredNetworkNames;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Remote {
    public Boolean active;
    public List<String> dnsServers;
    public CircuitBreaker circuitBreaker;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ResolvConf {
    public String paths;
    public Boolean overrideNameServers;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Server {
    public Dns dns;
    public Web web;
    public String protocol;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Solver {
    public Remote remote;
    public Docker docker;
    public System system;
    public Local local;
    public Stub stub;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Stub {
    public String domainName;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class System {
    public String hostMachineHostname;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Web {
    public Integer port;
  }


}
