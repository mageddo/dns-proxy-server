package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.ContainerConfig;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Docker {

  public static final String HOSTNAME_ENV = "HOSTNAMES=";

  public static String findContainerHostname(ContainerConfig config) {
    final var hostname = config.getHostName();
    if (StringUtils.isBlank(hostname)) {
      return null;
    }
    final var domainName = config.getDomainName();
    if (StringUtils.isBlank(domainName)) {
      return hostname;
    }
    return String.format("%s.%s", hostname, domainName);
  }

  public static Set<Hostname> findHostnameFromEnv(String[] envs) {
    if (envs == null) {
      return Collections.emptySet();
    }
    for (String env : envs) {
      if (env.startsWith(HOSTNAME_ENV)) {
        final var hosts = env.substring(HOSTNAME_ENV.length()).split("\s,\s");
        return Arrays
          .stream(hosts)
          .map(Hostname::of)
          .collect(Collectors.toSet());
      }
    }
    return Collections.emptySet();
  }
}
