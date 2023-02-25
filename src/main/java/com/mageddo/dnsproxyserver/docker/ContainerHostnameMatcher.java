package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.function.Predicate;

import static com.mageddo.dnsproxyserver.docker.Docker.buildHostnamesFromServiceOrContainerNames;
import static com.mageddo.dnsproxyserver.docker.Docker.findContainerHostname;
import static com.mageddo.dnsproxyserver.docker.DpsContainer.findHostnamesFromEnv;

public class ContainerHostnameMatcher {

  public static boolean test(final InspectContainerResponse inspect, final Hostname host, final Config config) {
    return buildPredicate(host, config).test(inspect);
  }

  static Predicate<InspectContainerResponse> buildPredicate(final Hostname host) {
    return buildPredicate(host, Configs.getInstance());
  }

  static Predicate<InspectContainerResponse> buildPredicate(final Hostname host, final Config config) {
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

  public static boolean hostnameMatches(InspectContainerResponse c, Hostname host) {
    return host.isEqualTo(findContainerHostname(c.getConfig()));
  }

  public static boolean hostnamesEnvMatches(InspectContainerResponse c, Hostname host) {
    return findHostnamesFromEnv(c.getConfig().getEnv()).contains(host);
  }

  public static boolean serviceOrContainerNameMatches(InspectContainerResponse c, Hostname host, Config config) {
    return isRegisterContainerNames(config)
      && buildHostnamesFromServiceOrContainerNames(c, config.getDomain()).contains(host);
  }
}
