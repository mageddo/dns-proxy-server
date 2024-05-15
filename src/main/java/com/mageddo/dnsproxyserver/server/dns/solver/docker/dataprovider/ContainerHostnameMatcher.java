package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.function.Predicate;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.Docker.buildHostnamesFromServiceOrContainerNames;
import static com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.Docker.findContainerHostname;
import static com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DpsContainerUtils.findHostnamesFromEnv;

public class ContainerHostnameMatcher {

  public static boolean test(final InspectContainerResponse inspect, final HostnameQuery host, final Config config) {
    return buildPredicate(host, config).test(inspect);
  }

  public static Predicate<InspectContainerResponse> buildPredicate(final HostnameQuery host) {
    return buildPredicate(host, Configs.getInstance());
  }

  static Predicate<InspectContainerResponse> buildPredicate(final HostnameQuery host, final Config config) {
    return container -> {

      final List<Predicate<InspectContainerResponse>> predicates = List.of(
        (it) -> hostnameMatches(it, host),
        (it) -> hostnamesEnvMatches(it, host),
        (it) -> serviceOrContainerNameMatches(it, host, config)
      );
      for (final var predicate : predicates) {
        if (predicate.test(container)) {
          return true;
        }
      }
      return false;
    };
  }

  static boolean isRegisterContainerNames(final Config config) {
    return BooleanUtils.isTrue(config.getRegisterContainerNames());
  }

  public static boolean hostnameMatches(InspectContainerResponse c, HostnameQuery host) {
    return host.matches(findContainerHostname(c.getConfig()));
  }

  public static boolean hostnamesEnvMatches(InspectContainerResponse c, HostnameQuery hostnameQuery) {
    return findHostnamesFromEnv(c.getConfig().getEnv())
      .stream()
      .anyMatch(hostnameQuery::matches)
      ;
  }

  public static boolean serviceOrContainerNameMatches(InspectContainerResponse c, HostnameQuery hostQuery, Config config) {
    return isRegisterContainerNames(config)
      && buildHostnamesFromServiceOrContainerNames(c, config.getDomain())
      .stream()
      .anyMatch(hostQuery::matches)
      ;
  }
}
