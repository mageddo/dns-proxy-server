package com.mageddo.dnsproxyserver.config.validator;

import com.mageddo.dnsproxyserver.config.Config;
import org.apache.commons.lang3.Validate;

public class ConfigValidator {
  public static void validate(Config config) {
    Validate.notNull(config.getVersion(), "version");
    Validate.notNull(config.getRemoteDnsServers(), "remote dns servers");
    Validate.isTrue(config.getWebServerPort() != null && config.getWebServerPort() > 0, "web server port");
    Validate.isTrue(config.getDnsServerPort() != null && config.getDnsServerPort() > 0, "dns server port");
    Validate.notNull(config.getLogFile(), "log file");
    Validate.notNull(config.getRegisterContainerNames(), "register container names");
    Validate.notNull(config.getHostMachineHostname(), "host machine hostname");
    Validate.notNull(config.getDomain(), "domain");
    Validate.notNull(config.getMustConfigureDpsNetwork(), "must configure dps network");
    Validate.notNull(config.getDpsNetworkAutoConnect(), "DPS network auto connect");
    Validate.notNull(config.getResolvConfPaths(), "Resolvconf paths");
    Validate.notNull(config.getResolvConfOverrideNameServers(), "Resolvconf override name servers");
    Validate.notNull(config.getDockerSolverHostMachineFallbackActive(), "Docker solver host machine fallback active");
    Validate.notNull(config.getServerProtocol(), "Server Protocol");

    Validate.notNull(config.getSolverRemote(), "Solver Remote");
    Validate.notNull(config.isSolverRemoteActive(), "Solver remote active");

    CircuitBreakerValidator.validate(config.getSolverRemoteCircuitBreaker());
  }
}
