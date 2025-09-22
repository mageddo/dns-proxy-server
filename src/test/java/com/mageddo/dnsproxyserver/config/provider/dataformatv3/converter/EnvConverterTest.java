package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvConverterTest {

  @Test
  void shouldParseEnvironmentVariablesIntoConfigV3() {
    var expected = new ConfigV3Templates().build();

    var converter = new EnvConverter(Map.ofEntries(
      entry("DPS_VERSION", "3"),
      entry("DPS_SERVER_DNS_PORT", "53"),
      entry("DPS_SERVER_DNS_NO_ENTRIES_RESPONSE_CODE", "3"),
      entry("DPS_SERVER_WEB_PORT", "5380"),
      entry("DPS_SERVER_PROTOCOL", "UDP_TCP"),
      entry("DPS_SOLVER_REMOTE_ACTIVE", "true"),
      entry("DPS_SOLVER_REMOTE_DNS_SERVERS_0", "8.8.8.8"),
      entry("DPS_SOLVER_REMOTE_DNS_SERVERS_1", "4.4.4.4:53"),
      entry("DPS_SOLVER_REMOTE_CIRCUIT_BREAKER_NAME", "STATIC_THRESHOLD"),
      entry("DPS_SOLVER_DOCKER_REGISTER_CONTAINER_NAMES", "false"),
      entry("DPS_SOLVER_DOCKER_DOMAIN", "docker"),
      entry("DPS_SOLVER_DOCKER_HOST_MACHINE_FALLBACK", "true"),
      entry("DPS_SOLVER_DOCKER_DPS_NETWORK_NAME", "dps"),
      entry("DPS_SOLVER_DOCKER_DPS_NETWORK_AUTO_CREATE", "false"),
      entry("DPS_SOLVER_DOCKER_DPS_NETWORK_AUTO_CONNECT", "false"),
      entry("DPS_SOLVER_SYSTEM_HOST_MACHINE_HOSTNAME", "host.docker"),
      entry("DPS_SOLVER_LOCAL_ACTIVE_ENV", ""),
      entry("DPS_SOLVER_LOCAL_ENVS_0_NAME", ""),
      entry("DPS_SOLVER_LOCAL_ENVS_0_HOSTNAMES_0_TYPE", "A"),
      entry("DPS_SOLVER_LOCAL_ENVS_0_HOSTNAMES_0_HOSTNAME", "github.com"),
      entry("DPS_SOLVER_LOCAL_ENVS_0_HOSTNAMES_0_IP", "192.168.0.1"),
      entry("DPS_SOLVER_LOCAL_ENVS_0_HOSTNAMES_0_TTL", "255"),
      entry("DPS_SOLVER_STUB_DOMAIN_NAME", "stub"),
      entry("DPS_DEFAULT_DNS_ACTIVE", "true"),
      entry("DPS_DEFAULT_DNS_RESOLV_CONF_PATHS", "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf"),
      entry("DPS_DEFAULT_DNS_RESOLV_CONF_OVERRIDE_NAME_SERVERS", "true"),
      entry("DPS_LOG_LEVEL", "DEBUG"),
      entry("DPS_LOG_FILE", "console")
    ));

    ConfigV3 actual = converter.parse();

    assertEquals(expected, actual);
  }

  @Test
  void shouldFailWhenUnknownVariableIsProvided() {
    var converter = new EnvConverter(Map.of("DPS_UNKNOWN", "value"));

    assertThrows(IllegalArgumentException.class, converter::parse);
  }
}
