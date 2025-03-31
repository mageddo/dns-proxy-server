package com.mageddo.dnsproxyserver.config.provider.dataformatv3;

import java.util.List;

public class ConfigV3 {

  public int version;
  public Server server;
  public Solver solver;
  public DefaultDns defaultDns;
  public Log log;

  public static class CircuitBreaker {
    public String name;
  }

  public static class DefaultDns {
    public Boolean active;
    public ResolvConf resolvConf;
  }

  public static class Dns {
    public Integer port;
    public Integer noEntriesResponseCode;
  }

  public static class Docker {
    public Boolean registerContainerNames;
    public String domain;
    public Boolean hostMachineFallback;
    public DpsNetwork dpsNetwork;
//    public Networks networks;
    public String dockerDaemonUri;
  }

  public static class DpsNetwork {
    public String name;
    public Boolean autoCreate;
    public Boolean autoConnect;
  }

  public static class Env {
    public String name;
    public List<Hostname> hostnames;
  }

  public static class Hostname {
    public String type;
    public String hostname;
    public String ip;
    public Integer ttl;
  }

  public static class Local {
    public String activeEnv;
    public List<Env> envs;
  }

  public static class Log {
    public String level;
    public String file;
  }

  public static class Networks {
    public List<String> preferredNetworkNames;
  }

  public static class Remote {
    public Boolean active;
    public List<String> dnsServers;
    public CircuitBreaker circuitBreaker;
  }

  public static class ResolvConf {
    public String paths;
    public Boolean overrideNameServers;
  }

  public static class Server {
    public Dns dns;
    public Web web;
    public String protocol;
  }

  public static class Solver {
    public Remote remote;
    public Docker docker;
    public System system;
    public Local local;
    public Stub stub;
  }

  public static class Stub {
    public String domainName;
  }

  public static class System {
    public String hostMachineHostname;
  }

  public static class Web {
    public Integer port;
  }


}
