package com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigV3EnvTemplates {

  private ConfigV3EnvTemplates() {
  }

  public static Map<String, String> build() {
    final var env = new LinkedHashMap<String, String>();
    env.put("DPS_VERSION", "3");
    env.put("DPS_SERVER__DNS__PROTOCOL", "UDP_TCP");
    env.put("DPS_SERVER__DNS__PORT", "53");
    env.put("DPS_SERVER__DNS__NO_ENTRIES_RESPONSE_CODE", "3");
    env.put("DPS_SERVER__WEB__PORT", "5380");
    env.put("DPS_SOLVER__REMOTE__ACTIVE", "true");
    env.put("DPS_SOLVER__REMOTE__DNS_SERVERS_0", "8.8.8.8");
    env.put("DPS_SOLVER__REMOTE__DNS_SERVERS_1", "4.4.4.4:53");
    env.put("DPS_SOLVER__REMOTE__CIRCUIT_BREAKER__TYPE", "STATIC_THRESHOLD");
    env.put("DPS_SOLVER__REMOTE__CIRCUIT_BREAKER__FAILURE_THRESHOLD", null);
    env.put("DPS_SOLVER__REMOTE__CIRCUIT_BREAKER__FAILURE_THRESHOLD_CAPACITY", null);
    env.put("DPS_SOLVER__REMOTE__CIRCUIT_BREAKER__SUCCESS_THRESHOLD", null);
    env.put("DPS_SOLVER__REMOTE__CIRCUIT_BREAKER__TEST_DELAY", null);
    env.put("DPS_SOLVER__DOCKER__REGISTER_CONTAINER_NAMES", "false");
    env.put("DPS_SOLVER__DOCKER__DOMAIN", "docker");
    env.put("DPS_SOLVER__DOCKER__HOST_MACHINE_FALLBACK", "true");
    env.put("DPS_SOLVER__DOCKER__DPS_NETWORK__NAME", "dps");
    env.put("DPS_SOLVER__DOCKER__DPS_NETWORK__AUTO_CREATE", "false");
    env.put("DPS_SOLVER__DOCKER__DPS_NETWORK__AUTO_CONNECT", "false");
    env.put("DPS_SOLVER__DOCKER__DOCKER_DAEMON_URI", "null");
    env.put("DPS_SOLVER__SYSTEM__HOST_MACHINE_HOSTNAME", "host.docker");
    env.put("DPS_SOLVER__LOCAL__ACTIVE_ENV", "");
    env.put("DPS_SOLVER__LOCAL__ENVS_0__NAME", "");
    env.put("DPS_SOLVER__LOCAL__ENVS_0__HOSTNAMES_0__TYPE", "A");
    env.put("DPS_SOLVER__LOCAL__ENVS_0__HOSTNAMES_0__TARGET", null);
    env.put("DPS_SOLVER__LOCAL__ENVS_0__HOSTNAMES_0__HOSTNAME", "github.com");
    env.put("DPS_SOLVER__LOCAL__ENVS_0__HOSTNAMES_0__IP", "192.168.0.1");
    env.put("DPS_SOLVER__LOCAL__ENVS_0__HOSTNAMES_0__TTL", "255");
    env.put("DPS_SOLVER__STUB__DOMAIN_NAME", "stub");
    env.put("DPS_DEFAULT_DNS__ACTIVE", "true");
    env.put("DPS_DEFAULT_DNS__RESOLV_CONF__PATHS",
        "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,"
            + "/etc/resolv.conf"
    );
    env.put("DPS_DEFAULT_DNS__RESOLV_CONF__OVERRIDE_NAME_SERVERS", "true");
    env.put("DPS_LOG__LEVEL", "DEBUG");
    env.put("DPS_LOG__FILE", "console");
    return env;
  }
}
