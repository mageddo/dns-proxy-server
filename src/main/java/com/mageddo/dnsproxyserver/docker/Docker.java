package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerConfig;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
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

  public static Set<Hostname> findHostnamesFromEnv(String[] envs) {
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

  static Set<Hostname> buildHostnamesFromServiceOrContainerNames(InspectContainerResponse container, String domain) {
    return Stream
      .of(
        buildFromContainerName(container, domain),
        buildFromServiceName(container, domain)
      ).map(Hostname::of)
      .collect(Collectors.toSet())
      ;
  }

  static String buildFromServiceName(InspectContainerResponse container, String domain) {
    final var serviceName = Labels.findLabelValue(container, Labels.SERVICE_NAME_LABEL);
    log.debug("status=serviceFindResult, service={}", serviceName);
    if (StringUtils.isBlank(serviceName)) {
      return null;
    }
    return String.format("%s.%s", serviceName, domain);
  }

  static String buildFromContainerName(InspectContainerResponse container, String domain) {
    return String.format("%s.%s", StringUtils.substring(container.getName(), 1), domain);
  }
}
