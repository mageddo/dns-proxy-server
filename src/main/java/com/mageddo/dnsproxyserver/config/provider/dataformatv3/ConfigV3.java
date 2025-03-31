package com.mageddo.dnsproxyserver.config.provider.dataformatv3;

import java.util.ArrayList;

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
    public boolean active;
    public ResolvConf resolvConf;
  }

  public static class Dns {
    public int port;
    public int noEntriesResponseCode;
  }

  public static class Docker {
    public boolean registerContainerNames;
    public String domain;
    public boolean hostMachineFallback;
    public DpsNetwork dpsNetwork;
    public Networks networks;
    public Object dockerDaemonUri;
  }

  public static class DpsNetwork {
    public String name;
    public boolean autoCreate;
    public boolean autoConnect;
  }

  public static class Env {
    public String name;
    public ArrayList<Hostname> hostnames;
  }

  public static class Hostname {
    public String type;
    public String hostname;
    public String ip;
    public int ttl;
  }

  public static class Local {
    public String activeEnv;
    public ArrayList<Env> envs;
  }

  public static class Log {
    public String level;
    public String file;
  }

  public static class Networks {
    public ArrayList<String> preferredNetworkNames;
  }

  public static class Remote {
    public boolean active;
    public ArrayList<String> dnsServers;
    public CircuitBreaker circuitBreaker;
  }

  public static class ResolvConf {
    public String paths;
    public boolean overrideNameServers;
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
    public int port;
  }


}
