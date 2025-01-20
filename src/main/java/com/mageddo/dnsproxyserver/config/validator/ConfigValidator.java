package com.mageddo.dnsproxyserver.config.validator;

import com.mageddo.dnsproxyserver.config.Config;
import org.apache.commons.lang3.Validate;

public class ConfigValidator {
  public static void validate(Config config) {
    Validate.notNull(config.getVersion(), "version");
    Validate.notNull(config.getRemoteDnsServers(), "remote dns servers");

    validateServer(config);

    Validate.notNull(config.getLogFile(), "log file");
    Validate.notNull(config.getHostMachineHostname(), "host machine hostname");
    Validate.notNull(config.getDefaultDnsResolvConfPaths(), "Resolvconf paths");

    validateDefaultDns(config);

    validateDockerSolver(config);

    Validate.notNull(config.getSolverRemote(), "Solver Remote");
    Validate.notNull(config.getSolverStub(), "Solver Stub");
    Validate.notNull(config.isSolverRemoteActive(), "Solver remote active");

    CircuitBreakerValidator.validate(config.getSolverRemoteCircuitBreakerStrategy());
  }

  private static void validateDockerSolver(Config config) {
    Validate.notNull(config.getRegisterContainerNames(), "register container names");
    Validate.notNull(config.getDockerDomain(), "domain");
    Validate.notNull(config.getDockerSolverMustConfigureDpsNetwork(), "must configure dps network");
    Validate.notNull(config.getDpsNetworkAutoConnect(), "DPS network auto connect");
    Validate.notNull(config.getDockerSolverHostMachineFallbackActive(), "Docker solver host machine fallback active");
    Validate.notNull(config.getServerProtocol(), "Server Protocol");
  }

  private static void validateServer(Config config) {
    Validate.isTrue(config.getWebServerPort() != null && config.getWebServerPort() > 0, "web server port");
    Validate.isTrue(config.getDnsServerPort() != null && config.getDnsServerPort() > 0, "dns server port");
  }

  private static void validateDefaultDns(Config config) {

    final var defaultDns = config.getDefaultDns();
    Validate.notNull(defaultDns, "Default DNS");
    Validate.notNull(defaultDns.getActive(), "Default DNS: Active");

    final var resolvConf = defaultDns.getResolvConf();
    Validate.notNull(resolvConf, "Default DNS: ResolvConf");
    Validate.notNull(resolvConf.getPaths(), "Default DNS: ResolvConf: Paths");
    Validate.notNull(resolvConf.getOverrideNameServers(), "Default DNS: ResolvConf: override name servers");
  }
}
