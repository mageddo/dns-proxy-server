package com.mageddo.dnsproxyserver.config.dataformat.v3;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigV3 {

  public int version;
  public Server server;
  public Solver solver;
  public DefaultDns defaultDns;
  public Log log;

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class CircuitBreaker {
    public String name;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class DefaultDns {
    public Boolean active;
    public ResolvConf resolvConf;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Dns {
    public Integer port;
    public Integer noEntriesResponseCode;
  }

  @Data
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
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class DpsNetwork {
    public String name;
    public Boolean autoCreate;
    public Boolean autoConnect;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Env {
    public String name;
    public List<Hostname> hostnames;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Hostname {
    public String type;
    public String hostname;
    public String ip;
    public Integer ttl;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Local {
    public String activeEnv;
    public List<Env> envs;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Log {
    public String level;
    public String file;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Networks {
    public List<String> preferredNetworkNames;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Remote {
    public Boolean active;
    public List<String> dnsServers;
    public CircuitBreaker circuitBreaker;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ResolvConf {
    public String paths;
    public Boolean overrideNameServers;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Server {
    public Dns dns;
    public Web web;
    public String protocol;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Solver {
    public Remote remote;
    public Docker docker;
    public System system;
    public Local local;
    public Stub stub;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Stub {
    public String domainName;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class System {
    public String hostMachineHostname;
  }

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Web {
    public Integer port;
  }


}
