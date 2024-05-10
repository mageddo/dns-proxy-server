package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.utils.Splits;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DpsContainer {

  public static final String HOSTNAME_ENV = "HOSTNAMES=";

  public static Set<Hostname> findHostnamesFromEnv(String[] envs) {
    if (envs == null) {
      return Collections.emptySet();
    }
    for (String env : envs) {
      if (env.startsWith(HOSTNAME_ENV)) {
        final var hosts = env.substring(HOSTNAME_ENV.length()).split(Splits.COMMA_SEPARATED);
        return Arrays
          .stream(hosts)
          .map(Hostname::of)
          .collect(Collectors.toCollection(LinkedHashSet::new));
      }
    }
    return Collections.emptySet();
  }
}
