package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerConfig;
import com.mageddo.dnsproxyserver.docker.application.Labels;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Docker {

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

  static Set<Hostname> buildHostnamesFromServiceOrContainerNames(InspectContainerResponse container, String domain) {
    return Stream
      .of(
        buildFromContainerName(container, domain),
        buildFromServiceName(container, domain)
      )
      .filter(Objects::nonNull)
      .map(Hostname::of)
      .collect(Collectors.toSet())
      ;
  }

  static String buildFromServiceName(InspectContainerResponse container, String domain) {
    final var serviceName = Labels.findLabelValue(container, Labels.SERVICE_NAME_LABEL);
    log.trace("status=serviceFindResult, service={}", serviceName);
    if (StringUtils.isBlank(serviceName)) {
      return null;
    }
    return String.format("%s.%s", serviceName, domain);
  }

  static String buildFromContainerName(InspectContainerResponse container, String domain) {
    return String.format("%s.%s", StringUtils.substring(container.getName(), 1), domain);
  }
}
