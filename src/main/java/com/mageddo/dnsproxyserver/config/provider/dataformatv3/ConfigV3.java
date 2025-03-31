package com.mageddo.dnsproxyserver.config.provider.dataformatv3;

import lombok.Data;

import java.util.List;

@Data
public class ConfigV3 {

  public int version;
  public Server server;
  public Solver solver;
  public DefaultDns defaultDns;
  public Log log;

  @Data
  public static class CircuitBreaker {
    public String name;
  }

  @Data
  public static class DefaultDns {
    public Boolean active;
    public ResolvConf resolvConf;
  }

  @Data
  public static class Dns {
    public Integer port;
    public Integer noEntriesResponseCode;
  }

  @Data
  public static class Docker {
    public Boolean registerContainerNames;
    public String domain;
    public Boolean hostMachineFallback;
    public DpsNetwork dpsNetwork;
    //    public Networks networks;
    public String dockerDaemonUri;
  }

  @Data
  public static class DpsNetwork {
    public String name;
    public Boolean autoCreate;
    public Boolean autoConnect;
  }

  @Data
  public static class Env {
    public String name;
    public List<Hostname> hostnames;
  }

  @Data
  public static class Hostname {
    public String type;
    public String hostname;
    public String ip;
    public Integer ttl;
  }

  @Data
  public static class Local {
    public String activeEnv;
    public List<Env> envs;
  }

  @Data
  public static class Log {
    public String level;
    public String file;
  }

  @Data
  public static class Networks {
    public List<String> preferredNetworkNames;
  }

  @Data
  public static class Remote {
    public Boolean active;
    public List<String> dnsServers;
    public CircuitBreaker circuitBreaker;
  }

  @Data
  public static class ResolvConf {
    public String paths;
    public Boolean overrideNameServers;
  }

  @Data
  public static class Server {
    public Dns dns;
    public Web web;
    public String protocol;
  }

  @Data
  public static class Solver {
    public Remote remote;
    public Docker docker;
    public System system;
    public Local local;
    public Stub stub;
  }

  @Data
  public static class Stub {
    public String domainName;
  }

  @Data
  public static class System {
    public String hostMachineHostname;
  }

  @Data
  public static class Web {
    public Integer port;
  }


}
