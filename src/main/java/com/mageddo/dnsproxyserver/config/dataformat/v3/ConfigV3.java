package com.mageddo.dnsproxyserver.config.dataformat.v3;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.dataformat.v3.jackson.CircuitBreakerConverter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigV3 {

  int version;
  Server server;
  Solver solver;
  DefaultDns defaultDns;
  Log log;


  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class StaticThreshold implements CircuitBreakerStrategyConfig {

    private Integer failureThreshold;
    private Integer failureThresholdCapacity;
    private Integer successThreshold;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration testDelay;

    @Override
    public CircuitBreakerStrategyConfig.Type getType() {
      return CircuitBreakerStrategyConfig.Type.STATIC_THRESHOLD;
    }
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class CanaryRateThreshold implements CircuitBreakerStrategyConfig {

    private float failureRateThreshold;
    private int minimumNumberOfCalls;
    private int permittedNumberOfCallsInHalfOpenState;

    @Override
    public CircuitBreakerStrategyConfig.Type getType() {
      return CircuitBreakerStrategyConfig.Type.CANARY_RATE_THRESHOLD;
    }
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class DefaultDns {
    Boolean active;
    ResolvConf resolvConf;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Dns {
    Integer port;
    Integer noEntriesResponseCode;
    String protocol;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Docker {
    Boolean registerContainerNames;
    String domain;
    Boolean hostMachineFallback;
    DpsNetwork dpsNetwork;
    Networks networks;
    String dockerDaemonUri;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class DpsNetwork {

    String name;
    Boolean autoCreate;
    Boolean autoConnect;
    List<Config> configs;

    @Data
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Config {
      String subNet;
      String ipRange;
      String gateway;
    }
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Env {
    String name;
    List<Hostname> hostnames;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Hostname {
    String type;
    String hostname;
    String target;
    String ip;
    Integer ttl;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Local {
    String activeEnv;
    List<Env> envs;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Log {
    String level;
    String file;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Networks {

    Preferred preferred;

    @Data
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Preferred {

      List<String> names;

      Boolean overrideDefault;


    }
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Remote {

    Boolean active;
    List<String> dnsServers;

    @JsonDeserialize(using = CircuitBreakerConverter.Deserializer.class)
    CircuitBreakerStrategyConfig circuitBreaker;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class ResolvConf {
    String paths;
    Boolean overrideNameServers;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Server {
    String host;
    Dns dns;
    Web web;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Solver {
    Remote remote;
    Docker docker;
    System system;
    Local local;
    Stub stub;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Stub {
    String domainName;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class System {
    String hostMachineHostname;
  }

  @Data
  @Accessors(chain = true)
  @FieldDefaults(level = AccessLevel.PRIVATE)
  static public class Web {
    Integer port;
  }


}
