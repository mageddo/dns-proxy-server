package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import java.net.URI;
import java.util.Objects;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class ConfigMapper {

  public static Config of(final ConfigV3 c) {
    if (c == null) {
      return null;
    }

    return Config.builder()
        .version(String.valueOf(c.getVersion()))
        .server(mapServer(c.getServer()))
        .defaultDns(mapDefaultDns(c.getDefaultDns()))
        .log(mapLog(c.getLog()))
        .solverRemote(mapSolverRemote(c.getSolver()))
        .solverDocker(mapSolverDocker(c.getSolver()))
        .solverLocal(mapSolverLocal(c.getSolver()))
        .solverStub(mapSolverStub(c.getSolver()))
        .solverSystem(mapSolverSystem(c.getSolver()))
        .source(Config.Source.FILE)
        .build();
  }

  public static ConfigV3 toV3(final Config config) {
    if (config == null) {
      return null;
    }

    final var v3 = new ConfigV3();
    v3.setVersion(Integer.parseInt(config.getVersion()));
    v3.setServer(mapServerV3(config));
    v3.setDefaultDns(mapDefaultDnsV3(config.getDefaultDns()));
    v3.setLog(mapLogV3(config.getLog()));
    v3.setSolver(mapSolverV3(config));

    return v3;
  }

  private static Config.Server mapServer(final ConfigV3.Server s) {
    if (s == null) {
      return null;
    }

    return Config.Server.builder()
        .dnsServerPort(s.getDns() != null ? s.getDns()
            .getPort() : null)
        .dnsServerNoEntriesResponseCode(
            s.getDns() != null ? s.getDns()
                .getNoEntriesResponseCode() : null
        )
        .webServerPort(s.getWeb() != null ? s.getWeb()
            .getPort() : null)
        .serverProtocol(
            s.getProtocol() != null
                ? SimpleServer.Protocol.valueOf(s.getProtocol())
                : null
        )
        .build();
  }

  private static ConfigV3.Server mapServerV3( Config config) {
    if (config.getServer() == null) {
      return null;
    }

    final var server = new ConfigV3.Server();

    final var dns = new ConfigV3.Dns();
    dns.setPort(config.getDnsServerPort());
    dns.setNoEntriesResponseCode(config.getNoEntriesResponseCode());
    server.setDns(dns);

    final var web = new ConfigV3.Web();
    web.setPort(config.getWebServerPort());
    server.setWeb(web);

    server.setProtocol(Objects.toString(config.getServerProtocol(), null));

    return server;
  }

  /* ================= DEFAULT DNS ================= */

  private static Config.DefaultDns mapDefaultDns(final ConfigV3.DefaultDns d) {
    if (d == null) {
      return null;
    }

    return Config.DefaultDns.builder()
        .active(d.getActive())
        .resolvConf(
            d.getResolvConf() == null
                ? null
                : Config.DefaultDns.ResolvConf.builder()
                .paths(d.getResolvConf()
                    .getPaths())
                .overrideNameServers(d.getResolvConf()
                    .getOverrideNameServers())
                .build()
        )
        .build();
  }

  private static ConfigV3.DefaultDns mapDefaultDnsV3(final Config.DefaultDns d) {
    if (d == null) {
      return null;
    }

    final var v3 = new ConfigV3.DefaultDns();
    v3.setActive(d.getActive());

    if (d.getResolvConf() != null) {
      final var rc = new ConfigV3.ResolvConf();
      rc.setPaths(d.getResolvConf()
          .getPaths());
      rc.setOverrideNameServers(d.getResolvConf()
          .getOverrideNameServers());
      v3.setResolvConf(rc);
    }

    return v3;
  }

  /* ================= LOG ================= */

  private static Config.Log mapLog(final ConfigV3.Log l) {
    if (l == null) {
      return null;
    }

    return Config.Log.builder()
        .level(l.getLevel() != null ? Config.Log.Level.valueOf(l.getLevel()) : null)
        .file(l.getFile())
        .build();
  }

  private static ConfigV3.Log mapLogV3(final Config.Log l) {
    if (l == null) {
      return null;
    }

    final var v3 = new ConfigV3.Log();
    v3.setLevel(l.getLevel() != null ? l.getLevel()
        .name() : null);
    v3.setFile(l.getFile());
    return v3;
  }

  /* ================= SOLVERS ================= */

  private static Config.SolverRemote mapSolverRemote(final ConfigV3.Solver s) {
    if (s == null || s.getRemote() == null) {
      return null;
    }

    return Config.SolverRemote.builder()
        .active(s.getRemote()
            .getActive())
        .dnsServers(
            s.getRemote()
                .getDnsServers() == null
                ? emptyList()
                : s.getRemote()
                .getDnsServers()
                .stream()
                .map(IpAddr::of)
                .collect(toList())
        )
        .circuitBreaker(null)
        .build();
  }

  private static Config.SolverDocker mapSolverDocker(final ConfigV3.Solver s) {
    if (s == null || s.getDocker() == null) {
      return null;
    }

    return Config.SolverDocker.builder()
        .registerContainerNames(s.getDocker()
            .getRegisterContainerNames())
        .domain(s.getDocker()
            .getDomain())
        .hostMachineFallback(s.getDocker()
            .getHostMachineFallback())
        .dockerDaemonUri(
            s.getDocker()
                .getDockerDaemonUri() != null
                ? URI.create(s.getDocker()
                .getDockerDaemonUri())
                : null
        )
        .dpsNetwork(
            s.getDocker()
                .getDpsNetwork() == null
                ? null
                : Config.SolverDocker.DpsNetwork.builder()
                .autoCreate(s.getDocker()
                    .getDpsNetwork()
                    .getAutoCreate())
                .autoConnect(s.getDocker()
                    .getDpsNetwork()
                    .getAutoConnect())
                .build()
        )
        .build();
  }

  private static Config.SolverLocal mapSolverLocal(final ConfigV3.Solver s) {
    if (s == null || s.getLocal() == null) {
      return null;
    }

    return Config.SolverLocal.builder()
        .activeEnv(s.getLocal()
            .getActiveEnv())
        .envs(
            s.getLocal()
                .getEnvs() == null
                ? emptyList()
                : s.getLocal()
                .getEnvs()
                .stream()
                .map(ConfigMapper::mapEnv)
                .collect(toList())
        )
        .build();
  }

  private static Config.Env mapEnv(final ConfigV3.Env e) {
    return Config.Env.of(
        e.getName(),
        e.getHostnames() == null
            ? emptyList()
            : e.getHostnames()
            .stream()
            .map(ConfigMapper::mapEntry)
            .collect(toList())
    );
  }

  private static Config.Entry mapEntry(final ConfigV3.Hostname h) {
    return Config.Entry.builder()
        .hostname(h.getHostname())
        .ttl(h.getTtl())
        .type(Config.Entry.Type.valueOf(h.getType()))
        .ip(h.getIp() != null ? IP.of(h.getIp()) : null)
        .build();
  }

  private static Config.SolverStub mapSolverStub(final ConfigV3.Solver s) {
    if (s == null || s.getStub() == null) {
      return null;
    }

    return Config.SolverStub.builder()
        .domainName(s.getStub()
            .getDomainName())
        .build();
  }

  private static Config.SolverSystem mapSolverSystem(final ConfigV3.Solver s) {
    if (s == null || s.getSystem() == null) {
      return null;
    }

    return Config.SolverSystem.builder()
        .hostMachineHostname(s.getSystem()
            .getHostMachineHostname())
        .build();
  }

  private static ConfigV3.Solver mapSolverV3(final Config config) {
    final var solver = new ConfigV3.Solver();

    if (config.getSolverRemote() != null) {
      final var r = new ConfigV3.Remote();
      r.setActive(config.isSolverRemoteActive());
      r.setDnsServers(
          config.getRemoteDnsServers()
              .stream()
              .map(IpAddr::toString)
              .collect(toList())
      );
      solver.setRemote(r);
    }

    if (config.getSolverDocker() != null) {
      final var d = new ConfigV3.Docker();
      d.setDomain(config.getDockerDomain());
      d.setRegisterContainerNames(config.getRegisterContainerNames());
      d.setHostMachineFallback(config.getDockerSolverHostMachineFallbackActive());
      d.setDockerDaemonUri(Objects.toString(config.getDockerDaemonUri(), null));
      solver.setDocker(d);
    }

    if (config.getSolverLocal() != null) {
      final var l = new ConfigV3.Local();
      l.setActiveEnv(config.getActiveEnv());
      l.setEnvs(
          config.getEnvs()
              .stream()
              .map(env -> {
                final var e = new ConfigV3.Env();
                e.setName(env.getName());
                e.setHostnames(
                    env.getEntries()
                        .stream()
                        .map(entry -> {
                          final var h = new ConfigV3.Hostname();
                          h.setHostname(entry.getHostname());
                          h.setType(entry.getType()
                              .name());
                          h.setIp(entry.getIpAsText());
                          h.setTtl(entry.getTtl());
                          return h;
                        })
                        .collect(toList())
                );
                return e;
              })
              .collect(toList())
      );
      solver.setLocal(l);
    }

    if (config.getSolverStub() != null) {
      final var s = new ConfigV3.Stub();
      s.setDomainName(config.getSolverStub()
          .getDomainName());
      solver.setStub(s);
    }

    if (config.getSolverSystem() != null) {
      final var s = new ConfigV3.System();
      s.setHostMachineHostname(config.getHostMachineHostname());
      solver.setSystem(s);
    }

    return solver;
  }
}
